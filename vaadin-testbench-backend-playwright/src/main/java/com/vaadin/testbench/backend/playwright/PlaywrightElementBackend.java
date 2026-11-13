/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.playwright;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.ElementState;
import com.microsoft.playwright.options.MouseButton;
import org.jspecify.annotations.Nullable;

import com.vaadin.testbench.StaleElementReferenceException;
import com.vaadin.testbench.spi.ElementBackend;

/**
 * The {@link ElementBackend} on a Playwright {@link ElementHandle}. Reads run
 * Selenium's own atoms or the script WebDriver specifies, through
 * {@link PlaywrightBackend#executeScript}, which also throws
 * {@link StaleElementReferenceException} once the node is detached. Actions use
 * Playwright's, whose own checks hold them until the element can take them, up
 * to {@code com.vaadin.testbench.playwright.actionTimeout}; Selenium's fail at
 * once.
 */
public class PlaywrightElementBackend implements ElementBackend {

    private final PlaywrightBackend backend;
    private final ElementHandle handle;
    private final String id;

    /**
     * @param id
     *            the node's id in its page, the same for every handle on it
     */
    PlaywrightElementBackend(PlaywrightBackend backend, ElementHandle handle,
            String id) {
        this.backend = backend;
        this.handle = handle;
        this.id = id;
    }

    /** The node, as Playwright holds it; the escape hatch. */
    public ElementHandle getElementHandle() {
        return handle;
    }

    private double timeout() {
        return backend.getActionTimeout().toMillis();
    }

    @Override
    public void click() {
        act(() -> handle
                .click(new ElementHandle.ClickOptions().setTimeout(timeout())));
    }

    /**
     * Selenium's offset is from the element's in-view centre, rounded down to a
     * pixel; Playwright's position is from its padding box.
     */
    @Override
    public void click(int xOffsetFromCenter, int yOffsetFromCenter,
            CharSequence... modifiers) {
        act(() -> handle.scrollIntoViewIfNeeded(
                new ElementHandle.ScrollIntoViewIfNeededOptions()
                        .setTimeout(timeout())));
        List<?> position = (List<?>) script(
                """
                        var e = arguments[0], r = e.getBoundingClientRect();
                        return [Math.floor((r.left + r.right) / 2) - r.left - e.clientLeft + arguments[1],
                                Math.floor((r.top + r.bottom) / 2) - r.top - e.clientTop + arguments[2]];
                        """,
                xOffsetFromCenter, yOffsetFromCenter);
        act(() -> handle
                .click(new ElementHandle.ClickOptions().setTimeout(timeout())
                        .setPosition(((Number) position.get(0)).doubleValue(),
                                ((Number) position.get(1)).doubleValue())
                        .setModifiers(KeyInput.modifiers(modifiers))));
    }

    @Override
    public void doubleClick() {
        act(() -> handle.dblclick(
                new ElementHandle.DblclickOptions().setTimeout(timeout())));
    }

    @Override
    public void contextClick() {
        act(() -> handle.click(new ElementHandle.ClickOptions()
                .setButton(MouseButton.RIGHT).setTimeout(timeout())));
    }

    @Override
    public void hover() {
        act(() -> handle
                .hover(new ElementHandle.HoverOptions().setTimeout(timeout())));
    }

    @Override
    public void sendKeys(CharSequence... keys) {
        // WebDriver's Element Send Keys on a file input: the text is the
        // files' paths, one per line, and the input may be hidden. They
        // replace the chosen files, where chromedriver adds to a multiple
        // input's
        if (Boolean.TRUE.equals(script(IS_FILE_INPUT))) {
            setInputFiles(Arrays.stream(String.join("", keys).split("\n"))
                    .map(File::new).toList());
            return;
        }
        act(() -> handle.waitForElementState(ElementState.VISIBLE,
                new ElementHandle.WaitForElementStateOptions()
                        .setTimeout(timeout())));
        script(FOCUS_FOR_TYPING);
        act(() -> new KeyInput(backend.getPage().keyboard()).send(keys));
    }

    private static final String IS_FILE_INPUT = "return arguments[0] instanceof HTMLInputElement"
            + " && arguments[0].type === 'file'";

    // WebDriver's Element Send Keys: focus the element unless the focus is in
    // it, through its own focus(), which a Vaadin field hands to its input;
    // then the caret goes to the end
    private static final String FOCUS_FOR_TYPING = """
            var e = arguments[0];
            var deepest = function() {
              var a = document.activeElement;
              while (a && a.shadowRoot && a.shadowRoot.activeElement) a = a.shadowRoot.activeElement;
              return a;
            };
            var active = deepest();
            if (active && (e === active || e.contains(active) || (e.shadowRoot && e.shadowRoot.contains(active)))) {
              return null;
            }
            e.focus();
            var a = deepest();
            if (a instanceof HTMLInputElement || a instanceof HTMLTextAreaElement) {
              try { a.setSelectionRange(a.value.length, a.value.length); } catch (x) {}
            }
            return null;
            """;

    // WebDriver's Element Clear
    private static final String CLEAR = """
            var e = arguments[0];
            var editable = e instanceof HTMLTextAreaElement || (e instanceof HTMLInputElement
                && !/^(button|checkbox|color|hidden|image|radio|range|reset|submit)$/.test(e.type));
            if (editable && !e.disabled && !e.readOnly) {
              e.focus();
              e.value = '';
              e.dispatchEvent(new Event('change', {bubbles: true}));
              e.blur();
              return null;
            }
            if (e.isContentEditable) {
              e.focus();
              e.innerHTML = '';
              e.blur();
              return null;
            }
            throw new Error('invalid element state: element must be user-editable in order to clear it');
            """;

    @Override
    public void clear() {
        script(CLEAR);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ElementBackend> findElements(String cssSelector) {
        return (List<ElementBackend>) script(
                "return Array.from(arguments[0].querySelectorAll(arguments[1]))",
                cssSelector);
    }

    // As chromedriver: an HTML tag in lower case, an SVG one as written
    @Override
    public String getTagName() {
        return (String) script("return arguments[0].localName");
    }

    @Override
    public @Nullable String getAttribute(String name) {
        return (String) script(Atoms.GET_ATTRIBUTE, name);
    }

    // A boolean attribute is "true" when present, as WebDriver's Get Element
    // Attribute has it; the names are those of Selenium's getAttribute atom
    @Override
    public @Nullable String getDomAttribute(String name) {
        return (String) script(
                """
                        var e = arguments[0], name = arguments[1];
                        if (BOOLEAN.has(name.toLowerCase())) return e.hasAttribute(name) ? 'true' : null;
                        return e.getAttribute(name);
                        """
                        .replace("BOOLEAN", BOOLEAN_ATTRIBUTES),
                name);
    }

    private static final String BOOLEAN_ATTRIBUTES = "new Set(['" + String.join(
            "', '", "allowfullscreen", "allowpaymentrequest", "allowusermedia",
            "async", "autofocus", "autoplay", "checked", "compact", "complete",
            "controls", "declare", "default", "defaultchecked",
            "defaultselected", "defer", "disabled", "ended", "formnovalidate",
            "hidden", "indeterminate", "iscontenteditable", "ismap",
            "itemscope", "loop", "multiple", "muted", "nohref", "nomodule",
            "noresize", "noshade", "novalidate", "nowrap", "open", "paused",
            "playsinline", "pubdate", "readonly", "required", "reversed",
            "scoped", "seamless", "seeking", "selected", "truespeed",
            "typemustmatch", "willvalidate") + "'])";

    @Override
    public @Nullable String getDomProperty(String name) {
        Object value = script("return arguments[0][arguments[1]]", name);
        return value == null ? null : String.valueOf(value);
    }

    @Override
    public String getText() {
        return (String) script(Atoms.GET_TEXT);
    }

    // Selenium reports a color as rgba()
    @Override
    public String getCssValue(String propertyName) {
        return (String) script(
                """
                        var v = window.getComputedStyle(arguments[0]).getPropertyValue(arguments[1]);
                        var m = /^rgb\\((\\d+), (\\d+), (\\d+)\\)$/.exec(v);
                        return m ? 'rgba(' + m[1] + ', ' + m[2] + ', ' + m[3] + ', 1)' : v;
                        """,
                propertyName);
    }

    @Override
    public boolean isDisplayed() {
        return Boolean.TRUE.equals(script(Atoms.IS_DISPLAYED));
    }

    @Override
    public boolean isEnabled() {
        // Selenium's bot.dom.isEnabled: only form controls can be disabled
        return Boolean.TRUE.equals(
                script("""
                        var e = arguments[0];
                        return !/^(button|input|optgroup|option|select|textarea)$/.test(e.localName)
                            || !e.matches(':disabled');
                        """));
    }

    @Override
    public boolean isSelected() {
        return Boolean.TRUE.equals(
                script("""
                        var e = arguments[0];
                        if (e instanceof HTMLOptionElement) return e.selected;
                        if (e instanceof HTMLInputElement && (e.type === 'checkbox' || e.type === 'radio')) return e.checked;
                        return false;
                        """));
    }

    // The size as Closure's goog.style.getSize, which Selenium's atoms use:
    // offsetWidth and offsetHeight, so whole pixels, and an element that is
    // not displayed measured while shown for a moment
    @Override
    public Rect getRect() {
        List<?> rect = (List<?>) script(
                """
                        var e = arguments[0], r = e.getBoundingClientRect();
                        var size = function() {
                          var w = e.offsetWidth, h = e.offsetHeight;
                          if ((w === undefined || !w && !h) && e.getBoundingClientRect) {
                            var c = e.getBoundingClientRect();
                            return [c.right - c.left, c.bottom - c.top];
                          }
                          return [w, h];
                        };
                        var wh;
                        if (window.getComputedStyle(e).display !== 'none') {
                          wh = size();
                        } else {
                          var s = e.style, old = [s.display, s.visibility, s.position];
                          s.visibility = 'hidden'; s.position = 'absolute'; s.display = 'inline';
                          wh = size();
                          s.display = old[0]; s.visibility = old[1]; s.position = old[2];
                        }
                        return [r.left + window.pageXOffset, r.top + window.pageYOffset, wh[0], wh[1]];
                        """);
        return new Rect(intOf(rect.get(0)), intOf(rect.get(1)),
                intOf(rect.get(2)), intOf(rect.get(3)));
    }

    // Truncated, as Selenium's Rectangle is
    private static int intOf(Object number) {
        return ((Number) number).intValue();
    }

    @Override
    public void setInputFiles(List<File> files) {
        act(() -> {
            handle.setInputFiles(
                    files.stream().map(File::toPath)
                            .toArray(java.nio.file.Path[]::new),
                    new ElementHandle.SetInputFilesOptions()
                            .setTimeout(timeout()));
            return null;
        });
    }

    @Override
    public byte[] getScreenshot() {
        return act(() -> handle.screenshot(
                new ElementHandle.ScreenshotOptions().setTimeout(timeout())));
    }

    private @Nullable Object script(String body, Object... more) {
        Object[] args = new Object[more.length + 1];
        args[0] = this;
        System.arraycopy(more, 0, args, 1, more.length);
        return backend.executeScript(body, args);
    }

    private static <T> T act(Supplier<T> playwrightCall) {
        try {
            return playwrightCall.get();
        } catch (PlaywrightException e) {
            throw PlaywrightBackend.translate(e);
        }
    }

    private static void act(Runnable playwrightCall) {
        act(() -> {
            playwrightCall.run();
            return null;
        });
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlaywrightElementBackend other
                && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "PlaywrightElementBackend[" + id + "]";
    }
}
