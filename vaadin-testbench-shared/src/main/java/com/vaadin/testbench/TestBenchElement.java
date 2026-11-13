/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;

import com.vaadin.testbench.commands.CanCompareScreenshots;
import com.vaadin.testbench.commands.ScreenshotComparator;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchCommands;
import com.vaadin.testbench.elementsbase.Element;
import com.vaadin.testbench.spi.ElementBackend;
import com.vaadin.testbench.spi.Input;

/**
 * One DOM node of the page, with Vaadin specific helper functionality.
 * TestBenchElements are created when you search for elements from
 * TestBenchTestCase or a context relative search from TestBenchElement. The
 * browser underneath is reached through {@link #getElementBackend()}.
 */
@Element("*")
public class TestBenchElement
        implements CanCompareScreenshots, HasTestBenchCommandExecutor,
        HasElementQuery, HasPropertySettersGetters, HasCallFunction {

    private ElementBackend elementBackend = null;
    private TestBenchCommandExecutor commandExecutor = null;
    private @Nullable Supplier<@Nullable ElementBackend> refind;

    protected TestBenchElement() {

    }

    /**
     * Initializes an element built by its no-argument constructor. A subclass
     * that needs initialization code overrides {@link #init()}, not this.
     *
     * @param element
     *            the DOM node this element stands for
     */
    protected void init(ElementBackend element,
            TestBenchCommandExecutor commandExecutor) {
        if (this.commandExecutor == null) {
            this.commandExecutor = commandExecutor;
            elementBackend = element;
            init();
        }
    }

    /**
     * Checks if the current test is running on Chrome.
     *
     * @return <code>true</code> if the test is running on Chrome,
     *         <code>false</code> otherwise
     */
    protected boolean isChrome() {
        return isBrowser("chrome", "chrome-headless-shell");
    }

    /**
     * Checks if the current test is running on Firefox.
     *
     * @return <code>true</code> if the test is running on Firefox,
     *         <code>false</code> otherwise
     */
    protected boolean isFirefox() {
        return isBrowser("firefox");
    }

    // The names Selenium's Browser.CHROME and Browser.FIREFOX match
    private boolean isBrowser(String... names) {
        String browserName = getCommandExecutor() == null ? null
                : getCommandExecutor().getBackend().getBrowserName();
        return Arrays.asList(names).contains(browserName);
    }

    /**
     * This is run after initializing a TestBenchElement. This can be overridden
     * in subclasses of TestBenchElement to run some initialization code.
     */
    protected void init() {

    }

    /**
     * The DOM node this element stands for, as the browser backend holds it.
     * Cast it to reach the browser library itself, as an escape hatch whose
     * calls do not wait for Vaadin:
     *
     * <pre>{@code
     * WebElement webElement = ((SeleniumElementBackend) element
     *         .getElementBackend()).getWebElement();
     * }</pre>
     */
    public ElementBackend getElementBackend() {
        return elementBackend;
    }

    /**
     * Lets a stale node be found again ({@code D_element_resolution}): set by
     * the query that found this element, to run that query again.
     */
    void setRefind(Supplier<@Nullable ElementBackend> refind) {
        this.refind = refind;
    }

    /**
     * Swaps a node that is no longer in the page for what this element's query
     * finds now at the same place.
     *
     * @return {@code true} if there is a new node; {@code false} if this
     *         element came from no query, or the query no longer finds one
     */
    boolean refind() {
        if (refind == null) {
            return false;
        }
        ElementBackend fresh;
        try {
            fresh = refind.get();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
        if (fresh == null || fresh.equals(elementBackend)) {
            return false;
        }
        elementBackend = fresh;
        return true;
    }

    void copyRefind(TestBenchElement from) {
        refind = from.refind;
    }

    boolean canRefind() {
        return refind != null;
    }

    /** Whether the node is gone; asks the browser. */
    boolean isStale() {
        try {
            getCommandExecutor().getBackend().executeScript("return null",
                    elementBackend);
            return false;
        } catch (StaleElementReferenceException e) {
            return true;
        }
    }

    // A call on the node, made once more on a fresh node when it is stale
    private <R> R onNode(Function<ElementBackend, R> call) {
        try {
            return call.apply(elementBackend);
        } catch (StaleElementReferenceException stale) {
            if (!refind()) {
                throw stale;
            }
            return call.apply(elementBackend);
        }
    }

    private void doOnNode(Consumer<ElementBackend> call) {
        onNode(node -> {
            call.accept(node);
            return null;
        });
    }

    protected void waitForVaadin() {
        if (getCommandExecutor() != null) {
            getCommandExecutor().waitForVaadin();
        }
    }

    /**
     * Gets the id set for this element.
     *
     * @return String value, can be null
     * @since 9.4
     */
    public String getId() {
        return getAttribute("id");
    }

    /**
     * Sets the number of pixels that an element's content is scrolled from the
     * top.
     *
     * @param scrollTop
     *            value set to Element.scroll property
     */
    public void scroll(int scrollTop) {
        setProperty("scrollTop", scrollTop);
    }

    /**
     * Sets the number of pixels that an element's content is scrolled to the
     * left.
     *
     * @param scrollLeft
     *            value set to Element.scrollLeft property
     */
    public void scrollLeft(int scrollLeft) {
        setProperty("scrollLeft", scrollLeft);
    }

    /**
     * Checks if the current element is the focused element on the page.
     *
     * @return {@code true} if this element is the currently focused element,
     *         {@code false} otherwise.
     * @since 9.4
     */
    public boolean isFocused() {
        return (boolean) onNode(node -> getCommandExecutor().getBackend()
                .executeScript("return arguments[0] === document.activeElement",
                        node));
    }

    public void click() {
        // JS call to click does not focus element, hence ensure focus
        focus();
        try {
            // Avoid strange "element not clickable at point" problems
            callFunction("click");
        } catch (Exception e) {
            if (e.getMessage()
                    .contains("Inspected target navigated or closed")) {
                // This happens with chromedriver although e.g. navigation
                // succeeds
                return;
            }
            // SVG elements and maybe others do not have a 'click' method
            ensureVisible();
            waitForVaadin();
            doOnNode(ElementBackend::click);
        }
    }

    public void submit() {
        click();
    }

    public void sendKeys(CharSequence... keysToSend) {
        // Viewport check: some drivers need the element in viewport for
        // reliable text entry.
        ensureInteractable();
        waitForVaadin();
        doOnNode(node -> node.sendKeys(keysToSend));
    }

    public void clear() {
        ensureVisible();
        waitForVaadin();
        doOnNode(ElementBackend::clear);
    }

    public String getTagName() {
        waitForVaadin();
        return onNode(ElementBackend::getTagName);
    }

    /**
     * @param name
     *            The name of the attribute.
     * @return The attribute/ property's current value or null if the value is
     *         not set.
     */
    public String getAttribute(String name) {
        waitForVaadin();
        return onNode(node -> node.getAttribute(name));
    }

    public @Nullable String getDomAttribute(String name) {
        waitForVaadin();
        return onNode(node -> node.getDomAttribute(name));
    }

    public @Nullable String getDomProperty(String name) {
        waitForVaadin();
        return onNode(node -> node.getDomProperty(name));
    }

    /**
     * Checks if the given attribute is present on the element.
     *
     * @param attribute
     *            the name of the attribute
     * @return <code>true</code> if the attribute is present, <code>false</code>
     *         otherwise
     */
    public boolean hasAttribute(String attribute) {
        return (boolean) callFunction("hasAttribute", attribute);
    }

    public boolean isSelected() {
        ensureVisible();
        waitForVaadin();
        return onNode(ElementBackend::isSelected);
    }

    /**
     * Returns whether the Vaadin component, that this element represents, is
     * enabled or not.
     *
     * @return true if the component is enabled.
     */
    public boolean isEnabled() {
        waitForVaadin();
        return !hasClassName("v-disabled") && !hasAttribute("disabled")
                && onNode(ElementBackend::isEnabled);
    }

    /**
     * Returns whether the Vaadin component, that this element represents, is
     * having readonly attribute set or not.
     *
     * @return true if the component has readonly attribute set.
     * @since 9.4
     */
    public boolean isReadOnly() {
        waitForVaadin();
        return hasAttribute("readonly");
    }

    /**
     * The element's text, as Selenium gives it: only what a user can see, with
     * shadow DOM text included, a hidden element giving {@code ""}.
     */
    public String getText() {
        ensureVisible();
        waitForVaadin();
        return onNode(ElementBackend::getText);
    }

    /**
     * The descendants {@code by} matches, in the light DOM only: unlike
     * {@code $}, this never looks inside a shadow root.
     */
    @SuppressWarnings("deprecation")
    public List<TestBenchElement> findElements(By by) {
        waitForVaadin();
        return onNode(node -> node.findElements(by.toCssSelector())).stream()
                .map(found -> TestBench.createElement(TestBenchElement.class,
                        found, getCommandExecutor()))
                .toList();
    }

    /**
     * The first descendant {@code by} matches, in the light DOM only: unlike
     * {@code $}, this never looks inside a shadow root.
     *
     * @throws NoSuchElementException
     *             if nothing matches
     */
    @SuppressWarnings("deprecation")
    public TestBenchElement findElement(By by) {
        waitForVaadin();
        List<ElementBackend> found = onNode(
                node -> node.findElements(by.toCssSelector()));
        if (found.isEmpty()) {
            throw new NoSuchElementException(
                    "Unable to locate an element with " + by);
        }
        return TestBench.createElement(TestBenchElement.class, found.get(0),
                getCommandExecutor());
    }

    public boolean isDisplayed() {
        waitForVaadin();
        return onNode(ElementBackend::isDisplayed);
    }

    public Point getLocation() {
        waitForVaadin();
        ElementBackend.Rect rect = onNode(ElementBackend::getRect);
        return new Point(rect.x(), rect.y());
    }

    public Dimension getSize() {
        waitForVaadin();
        ElementBackend.Rect rect = onNode(ElementBackend::getRect);
        return new Dimension(rect.width(), rect.height());
    }

    public String getCssValue(String propertyName) {
        waitForVaadin();
        return onNode(node -> node.getCssValue(propertyName));
    }

    /**
     * Clicks at an offset from the element's centre, holding the modifiers.
     */
    public void click(int x, int y, Keys... modifiers) {
        ensureInteractable();
        waitForVaadin();
        doOnNode(node -> node.click(x, y, modifiers));
    }

    public void doubleClick() {
        ensureInteractable();
        waitForVaadin();
        doOnNode(ElementBackend::doubleClick);
    }

    public void contextClick() {
        ensureInteractable();
        waitForVaadin();
        doOnNode(ElementBackend::contextClick);
    }

    public <T extends TestBenchElement> T wrap(Class<T> elementType) {
        return TestBench.wrap(this, elementType);
    }

    @Override
    public TestBenchCommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    /**
     * The element {@code $} searches inside: this one, unless a component keeps
     * its content in another element, as a notification does in its card.
     */
    public TestBenchElement getContext() {
        return this;
    }

    @Override
    public <T extends TestBenchElement> ElementQuery<T> $(Class<T> clazz) {
        return new ElementQuery<>(clazz).context(getContext());
    }

    @Override
    public ElementQuery<TestBenchElement> $(String tagName) {
        return new ElementQuery<>(TestBenchElement.class, tagName)
                .context(getContext());
    }

    /**
     * Move browser focus to this Element
     */
    public void focus() {
        waitForVaadin();
        Refind.onStale(() -> {
            getCommandExecutor().focusElement(this);
            return null;
        }, this);
    }

    public Rectangle getRect() {
        waitForVaadin();
        ElementBackend.Rect rect = onNode(ElementBackend::getRect);
        return new Rectangle(rect.x(), rect.y(), rect.height(), rect.width());
    }

    /**
     * Gets all the class names set for this element.
     *
     * @return a set of class names
     */
    public Set<String> getClassNames() {
        String classAttribute = getDomAttribute("class");
        Set<String> classes = new HashSet<>();
        if (classAttribute == null) {
            return classes;
        }
        classAttribute = classAttribute.trim();
        if (classAttribute.isEmpty()) {
            return classes;
        }

        Collections.addAll(classes, classAttribute.split("[ ]+"));
        return classes;
    }

    /**
     * Checks if this element has the given class name.
     * <p>
     * Matches only full class names, i.e. has ("foo") does not match
     * class="foobar bafoo"
     *
     * @param className
     *            the class name to check for
     * @return <code>true</code> if the element has the given class name,
     *         <code>false</code> otherwise
     */
    public boolean hasClassName(String className) {
        if (className == null || className.isEmpty()) {
            return false;
        }

        return getClassNames().contains(className);
    }

    @Override
    public boolean equals(Object obj) {
        if (elementBackend == null) {
            return false;
        }
        if (obj instanceof TestBenchElement other) {
            return elementBackend.equals(other.elementBackend);
        }
        return elementBackend.equals(obj);
    }

    @Override
    public int hashCode() {
        if (elementBackend == null) {
            return 32;
        }

        return elementBackend.hashCode();
    }

    @Override
    public boolean compareScreen(String referenceId) throws IOException {
        waitForVaadin();
        return ScreenshotComparator.compareScreen(referenceId,
                getCommandExecutor().getReferenceNameGenerator(),
                getCommandExecutor().getImageComparison(),
                getCommandExecutor().getBackend(), elementBackend);
    }

    @Override
    public boolean compareScreen(File reference) throws IOException {
        waitForVaadin();
        return ScreenshotComparator.compareScreen(reference,
                getCommandExecutor().getImageComparison(),
                getCommandExecutor().getBackend(), elementBackend);
    }

    @Override
    public boolean compareScreen(BufferedImage reference, String referenceName)
            throws IOException {
        waitForVaadin();
        return ScreenshotComparator.compareScreen(reference, referenceName,
                getCommandExecutor().getImageComparison(),
                getCommandExecutor().getBackend(), elementBackend);
    }

    /***
     * Scrolls the element into the visible area of the browser window
     */
    public void scrollIntoView() {
        callFunction("scrollIntoView");
    }

    /**
     * Scrolls the element into the visible area of the browser window with the
     * given options.
     *
     * @param options
     *            the scroll options, e.g.
     *            {@code Map.of("block", "end", "inline", "end")}
     * @see <a href=
     *      "https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView">Element.scrollIntoView()</a>
     * @since 10.1
     */
    public void scrollIntoView(Map<String, Object> options) {
        callFunction("scrollIntoView", options);
    }

    /**
     * Ensures the element is interactable by scrolling it into the browser
     * viewport if {@link TestBenchCommands#isAutoScrollIntoView()} is enabled
     * and the element is outside the viewport or clipped by a scrollable
     * ancestor.
     * <p>
     * Intended for methods that use the Selenium Actions API
     * ({@code moveToElement}, {@code doubleClick}, {@code contextClick}), which
     * require the element to be physically within the viewport.
     */
    private void ensureInteractable() {
        try {
            if (getCommandExecutor().isAutoScrollIntoView()) {
                if (!isElementVisibleInViewport()) {
                    scrollIntoView(
                            Map.of("block", "nearest", "inline", "nearest"));
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * Ensures the element is visible by scrolling it into view if
     * {@link TestBenchCommands#isAutoScrollIntoView()} is enabled and the
     * element is not displayed (e.g. {@code display:none} or detached).
     * <p>
     * Intended for methods that use the WebElement API ({@code clear},
     * {@code getText}, etc.), where WebDriver handles its own scrolling
     * internally and only needs the element to be rendered.
     */
    private void ensureVisible() {
        try {
            if (getCommandExecutor().isAutoScrollIntoView()) {
                if (!onNode(ElementBackend::isDisplayed)) {
                    scrollIntoView();
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * Checks whether the element is visible within the browser viewport and not
     * clipped by any scrollable ancestor container.
     *
     * @return {@code true} if the element is in the viewport, {@code true} on
     *         error as a safe fallback
     */
    private boolean isElementVisibleInViewport() {
        try {
            return (boolean) executeScript(
                    """
                            try {
                              var elem = arguments[0];
                              var rect = elem.getBoundingClientRect();
                              if (rect.width === 0 && rect.height === 0) return false;
                              var vw = window.innerWidth || document.documentElement.clientWidth;
                              var vh = window.innerHeight || document.documentElement.clientHeight;
                              if (rect.bottom < 0 || rect.top > vh || rect.right < 0 || rect.left > vw) return false;
                              var parent = elem.parentElement;
                              while (parent) {
                                var style = getComputedStyle(parent);
                                var overflow = style.overflow + style.overflowX + style.overflowY;
                                if (/auto|scroll|hidden/.test(overflow)) {
                                  var parentRect = parent.getBoundingClientRect();
                                  if (rect.bottom <= parentRect.top || rect.top >= parentRect.bottom
                                      || rect.right <= parentRect.left || rect.left >= parentRect.right) return false;
                                }
                                parent = parent.parentElement;
                              }
                              return true;
                            } catch(e) { return true; }
                            """,
                    this);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Waits up to {@code timeoutInSeconds} for the condition to return neither
     * {@code null} nor {@code false}, polling as {@link Wait} does:
     *
     * <pre>{@code
     * waitUntil(driver -> !getPropertyBoolean("loading"), 30);
     * }</pre>
     *
     * @return the condition's first value that is neither {@code null} nor
     *         {@code false}
     * @throws TimeoutException
     *             if the time runs out
     */
    protected <T> T waitUntil(WaitCondition<T> condition,
            long timeoutInSeconds) {
        return Wait.until(new Supplier<>() {
            @Override
            public T get() {
                return condition.apply(getCommandExecutor());
            }

            @Override
            public String toString() {
                return condition.toString();
            }
        }, Duration.ofSeconds(timeoutInSeconds));
    }

    /**
     * Waits up to 10 seconds, as {@link #waitUntil(WaitCondition, long)}.
     */
    protected <T> T waitUntil(WaitCondition<T> condition) {
        return waitUntil(condition, 10);
    }

    @Override
    public void setProperty(String name, String value) {
        internalSetProperty(name, value);
    }

    @Override
    public void setProperty(String name, Boolean value) {
        internalSetProperty(name, value);
    }

    @Override
    public void setProperty(String name, Double value) {
        internalSetProperty(name, value);
    }

    @Override
    public void setProperty(String name, Integer value) {
        internalSetProperty(name, value);
    }

    @Override
    public String getPropertyString(String... propertyNames) {
        Object value = getProperty(propertyNames);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }

        throw new IllegalArgumentException("The property is of type "
                + value.getClass().getName() + ", not String");
    }

    @Override
    public Boolean getPropertyBoolean(String... propertyNames) {
        Object value = getProperty(propertyNames);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new IllegalArgumentException("The property is of type "
                + value.getClass().getName() + ", not Boolean");
    }

    @Override
    public TestBenchElement getPropertyElement(String... propertyNames) {
        return (TestBenchElement) getProperty(propertyNames);
    }

    @Override
    public List<TestBenchElement> getPropertyElements(String... propertyNames) {
        return (List<TestBenchElement>) getProperty(propertyNames);
    }

    @Override
    public Double getPropertyDouble(String... propertyNames) {
        Object value = getProperty(propertyNames);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        throw new IllegalArgumentException("The property is of type "
                + value.getClass().getName() + ", not Number");
    }

    @Override
    public Integer getPropertyInteger(String... propertyNames) {
        Double number = getPropertyDouble(propertyNames);
        return (number == null) ? null : number.intValue();
    }

    private void internalSetProperty(String name, Object value) {
        if (isFirefox() && value instanceof Double) {
            executeScript("arguments[0][arguments[1]]=Number(arguments[2])",
                    this, name, String.valueOf((value)));
        } else {
            executeScript("arguments[0][arguments[1]]=arguments[2]", this, name,
                    value);
        }
    }

    @Override
    public Object getProperty(String... propertyNames) {
        String script = "var value = arguments[0];"
                + createPropertyChain(propertyNames) + ";";
        Object[] jsParameters = Stream
                .concat(Stream.of(this), Stream.of(propertyNames)).toArray();

        if (isFirefox()) {
            String isNumberScript = script + "return typeof value == 'number';";
            boolean number = (boolean) executeScript(isNumberScript,
                    jsParameters);

            if (number) {
                String str = (String) executeScript(
                        script + "return value.toString();", jsParameters);
                return Double.parseDouble(str);
            }
        }
        return executeScript(script + "return value;", jsParameters);
    }

    private static String createPropertyChain(String[] propertyNames) {
        String result = "";
        for (int i = 0; i < propertyNames.length; i++) {
            result += "if (typeof value != 'undefined') value = value[arguments["
                    + (i + 1) + "]];";

        }
        return result;
    }

    /**
     * Executes the given JavaScript in the context of the currently selected
     * frame or window. The script fragment provided will be executed as the
     * body of an anonymous function.
     *
     * @param script
     *            the script to execute
     * @param args
     *            the arguments, available in the script as
     *            {@code arguments[0]...arguments[N]}
     * @return as
     *         {@link TestBenchCommandExecutor#executeScript(String, Object...)}
     *         returns it: DOM nodes as {@link TestBenchElement}s
     */
    protected Object executeScript(String script, Object... args) {
        return Refind.onStale(
                () -> getCommandExecutor().executeScript(script, args), args);
    }

    @Override
    public Object callFunction(String methodName, Object... args) {
        // arguments[0].method(arguments[1],arguments[2],arguments[3])
        String paramPlaceholderString = IntStream.range(1, args.length + 1)
                .mapToObj(i -> "arguments[" + i + "]")
                .collect(Collectors.joining(","));
        Object[] jsParameters = Stream.concat(Stream.of(this), Stream.of(args))
                .toArray(size -> new Object[size]);

        return executeScript("return arguments[0]." + methodName + "("
                + paramPlaceholderString + ")", jsParameters);
    }

    /**
     * Dispatches (fires) a custom event of the given type on the element.
     * <p>
     * The event is created without any parameters.
     *
     * @param eventType
     *            the type of custom event to dispatch
     */
    public void dispatchEvent(String eventType) {
        executeScript(
                "arguments[0].dispatchEvent(new CustomEvent(arguments[1]));",
                this, eventType);
    }

    /**
     * Dispatches (fires) a custom event of the given type on the element with
     * the given properties
     *
     * @param eventType
     *            the type of custom event to dispatch
     * @param customEventInit
     *            map with properties and values that will be used to initialize
     *            the event
     * @since 6.1
     */
    public void dispatchEvent(String eventType,
            Map<String, Object> customEventInit) {
        executeScript(
                "arguments[0].dispatchEvent(new CustomEvent(arguments[1], arguments[2]));",
                this, eventType, customEventInit);
    }

    /**
     * Gets the parent element of this element.
     *
     * @return The parent as TestBenchElement, can be null.
     * @since 9.4
     */
    public TestBenchElement getParent() {
        return getPropertyElement("parentElement");
    }

    /**
     * Get list of immediate children of this element.
     *
     * @return List of TestBenchElements
     * @since 9.4
     */
    public List<TestBenchElement> getChildren() {
        return getPropertyElements("children");
    }

    /**
     * Hover mouse pointer on this element.
     *
     * Note: This works well only if this element is atomic.
     *
     * @since 9.4
     */
    public void hover() {
        ensureInteractable();
        waitForVaadin();
        doOnNode(ElementBackend::hover);
    }

    /**
     * Drags this element onto {@code target} with the mouse: presses the left
     * button in this element's centre, moves to the target's, releases.
     *
     * <pre>{@code
     * spreadsheet.getColumnHeader(3).getResizeHandle()
     *         .dragTo(spreadsheet.getColumnHeader(5));
     * }</pre>
     *
     * The mouse events of a user's drag; from a {@code draggable} element,
     * Chromium starts HTML drag and drop, on both backends.
     */
    public void dragTo(TestBenchElement target) {
        ensureInteractable();
        waitForVaadin();
        Refind.onStale(() -> {
            getCommandExecutor().getBackend()
                    .perform(List.of(
                            new Input.MoveToElement(elementBackend, 0, 0),
                            new Input.MouseDown(Input.Button.LEFT),
                            new Input.MoveToElement(target.getElementBackend(),
                                    0, 0),
                            new Input.MouseUp(Input.Button.LEFT)));
            return null;
        }, this, target);
    }

    /**
     * Drags this element by an offset with the mouse: presses the left button
     * in its centre, moves by {@code (x, y)} CSS pixels, releases.
     *
     * <pre>{@code
     * splitLayout.getSplitter().dragBy(150, 0);
     * }</pre>
     *
     * The mouse events, as {@link #dragTo(TestBenchElement)}.
     */
    public void dragBy(int x, int y) {
        ensureInteractable();
        waitForVaadin();
        doOnNode(node -> getCommandExecutor().getBackend()
                .perform(List.of(new Input.MoveToElement(node, 0, 0),
                        new Input.MouseDown(Input.Button.LEFT),
                        new Input.MoveBy(x, y),
                        new Input.MouseUp(Input.Button.LEFT))));
    }
}
