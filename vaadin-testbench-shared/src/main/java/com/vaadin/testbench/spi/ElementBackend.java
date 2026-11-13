/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.spi;

import java.io.File;
import java.util.List;

import org.jspecify.annotations.Nullable;

/**
 * One DOM node a {@link BrowserBackend} has found: the input and the reads a
 * {@code TestBenchElement} cannot do through
 * {@link BrowserBackend#executeScript(String, Object...)}. A
 * {@code TestBenchElement} holds one and forwards to it:
 *
 * <pre>{@code
 * public String getText() {
 *     ensureVisible();
 *     waitForVaadin();
 *     return getElementBackend().getText();
 * }
 * }</pre>
 *
 * Each method behaves as Selenium's {@code WebElement} method of the same name.
 * Nothing here waits for Vaadin or scrolls; the caller does.
 * <p>
 * Keys are W3C WebDriver key codes: a character, or one of the code points
 * U+E000 to U+E05D for a special key, as Selenium's {@code Keys} constants are.
 * <p>
 * {@code equals} and {@code hashCode} compare the DOM node.
 */
public interface ElementBackend {

    /** A native click in the element's centre. */
    void click();

    /**
     * A click at an offset from the element's centre, holding the modifiers.
     *
     * @param modifiers
     *            pressed before the click, released after it
     */
    void click(int xOffsetFromCenter, int yOffsetFromCenter,
            CharSequence... modifiers);

    void doubleClick();

    void contextClick();

    /** Moves the mouse onto the element's centre. */
    void hover();

    void sendKeys(CharSequence... keys);

    void clear();

    /**
     * The descendants matching {@code cssSelector}, in document order, as
     * {@code querySelectorAll} on this node finds them: the light DOM only,
     * never inside a shadow root.
     *
     * @return empty when nothing matches
     */
    List<ElementBackend> findElements(String cssSelector);

    String getTagName();

    /**
     * Selenium's attribute-or-property read: a property when one exists,
     * {@code "true"} or {@code null} for a boolean attribute.
     */
    @Nullable
    String getAttribute(String name);

    @Nullable
    String getDomAttribute(String name);

    @Nullable
    String getDomProperty(String name);

    /** The rendered text, as Selenium's {@code getText()} computes it. */
    String getText();

    String getCssValue(String propertyName);

    boolean isDisplayed();

    boolean isEnabled();

    boolean isSelected();

    /** The element's box in CSS pixels, relative to the page. */
    Rect getRect();

    /**
     * @return a PNG of the element
     * @throws UnsupportedOperationException
     *             if the browser takes no screenshot of an element; the caller
     *             crops one of the page
     */
    byte[] getScreenshot();

    /**
     * Gives a file input these local files, as typing their paths into it does
     * on a local Selenium browser; a remote one is sent the files first.
     */
    void setInputFiles(List<File> files);

    /**
     * A box in CSS pixels, truncated to whole pixels as Selenium does.
     */
    record Rect(int x, int y, int width, int height) {
    }
}
