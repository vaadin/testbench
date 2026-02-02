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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.commands.CanCompareScreenshots;
import com.vaadin.testbench.commands.ScreenshotComparator;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchCommands;
import com.vaadin.testbench.elementsbase.Element;
import com.vaadin.testbench.parallel.BrowserUtil;

/**
 * TestBenchElement is a WebElement wrapper. It provides Vaadin specific helper
 * functionality. TestBenchElements are created when you search for elements
 * from TestBenchTestCase or a context relative search from TestBenchElement.
 */
@Element("*")
public class TestBenchElement implements WrapsElement, WebElement, HasDriver,
        CanCompareScreenshots, HasTestBenchCommandExecutor, HasElementQuery,
        HasPropertySettersGetters, HasCallFunction {

    private WebElement wrappedElement = null;
    private TestBenchCommandExecutor commandExecutor = null;

    protected TestBenchElement() {

    }

    protected TestBenchElement(WebElement webElement,
            TestBenchCommandExecutor commandExecutor) {
        init(webElement, commandExecutor);
    }

    /**
     * TestBenchElement initialization function. If a subclass of
     * TestBenchElement needs to run some initialization code, it should
     * override {@link #init()}, not this function.
     *
     * @param element
     *            WebElement to wrap
     * @param commandExecutor
     *            TestBenchCommandExecutor instance
     */
    protected void init(WebElement element,
            TestBenchCommandExecutor commandExecutor) {
        if (this.commandExecutor == null) {
            this.commandExecutor = commandExecutor;
            wrappedElement = element;
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
        return BrowserUtil.isChrome(getCapabilities());
    }

    /**
     * Checks if the current test is running on Firefox.
     *
     * @return <code>true</code> if the test is running on Firefox,
     *         <code>false</code> otherwise
     */
    protected boolean isFirefox() {
        return BrowserUtil.isFirefox(getCapabilities());
    }

    /**
     * Returns information about current browser used
     *
     * @see Capabilities
     * @return information about current browser used
     */
    protected Capabilities getCapabilities() {
        WebDriver driver = getDriver();
        if (driver instanceof HasCapabilities) {
            return ((HasCapabilities) driver).getCapabilities();
        } else {
            return null;
        }
    }

    /**
     * This is run after initializing a TestBenchElement. This can be overridden
     * in subclasses of TestBenchElement to run some initialization code.
     */
    protected void init() {

    }

    @Override
    public WebElement getWrappedElement() {
        return wrappedElement;
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
     */
    public boolean isFocused() {
        return this.getWrappedElement()
                .equals(getDriver().switchTo().activeElement());
    }

    @Override
    public void click() {
        autoScrollIntoView();
        waitForVaadin();
        new Actions(getDriver()).click(wrappedElement).build().perform();
    }

    @Override
    public void submit() {
        click();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        autoScrollIntoView();
        waitForVaadin();
        wrappedElement.sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        autoScrollIntoView();
        waitForVaadin();
        wrappedElement.clear();
    }

    @Override
    public String getTagName() {
        waitForVaadin();
        return wrappedElement.getTagName();
    }

    /**
     * @param name
     *            The name of the attribute.
     * @return The attribute/ property's current value or null if the value is
     *         not set.
     */
    @Override
    public String getAttribute(String name) {
        waitForVaadin();
        return wrappedElement.getAttribute(name);
    }

    @Override
    public @Nullable String getDomAttribute(String name) {
        waitForVaadin();
        return wrappedElement.getDomAttribute(name);
    }

    @Override
    public @Nullable String getDomProperty(String name) {
        waitForVaadin();
        return wrappedElement.getDomProperty(name);
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

    @Override
    public boolean isSelected() {
        autoScrollIntoView();
        waitForVaadin();
        return wrappedElement.isSelected();
    }

    /**
     * Returns whether the Vaadin component, that this element represents, is
     * enabled or not.
     *
     * @return true if the component is enabled.
     */
    @Override
    public boolean isEnabled() {
        waitForVaadin();
        return !hasClassName("v-disabled") && !hasAttribute("disabled")
                && wrappedElement.isEnabled();
    }

    /**
     * Returns whether the Vaadin component, that this element represents, is
     * having readonly attribute set or not.
     *
     * @return true if the component has readonly attribute set.
     */
    public boolean isReadOnly() {
        waitForVaadin();
        return hasAttribute("readonly");
    }

    @Override
    public String getText() {
        autoScrollIntoView();
        waitForVaadin();
        return wrappedElement.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return (List) TestBenchElement.wrapElements(
                wrappedElement.findElements(by), getCommandExecutor());
    }

    @Override
    public TestBenchElement findElement(By by) {
        waitForVaadin();
        return wrapElement(wrappedElement.findElement(by),
                getCommandExecutor());
    }

    @Override
    public boolean isDisplayed() {
        waitForVaadin();
        return wrappedElement.isDisplayed();
    }

    @Override
    public Point getLocation() {
        waitForVaadin();
        return wrappedElement.getLocation();
    }

    @Override
    public Dimension getSize() {
        waitForVaadin();
        return wrappedElement.getSize();
    }

    @Override
    public String getCssValue(String propertyName) {
        waitForVaadin();
        return wrappedElement.getCssValue(propertyName);
    }

    public void click(int x, int y, Keys... modifiers) {
        autoScrollIntoView();
        waitForVaadin();
        Actions actions = new Actions(getDriver());
        actions.moveToElement(wrappedElement, x, y);
        // Press any modifier keys
        for (Keys modifier : modifiers) {
            actions.keyDown(modifier);
        }
        actions.click();
        // Release any modifier keys
        for (Keys modifier : modifiers) {
            actions.keyUp(modifier);
        }
        actions.build().perform();
    }

    /**
     * Performs a double-click action on this element.
     *
     * @see click()
     */
    public void doubleClick() {
        autoScrollIntoView();
        waitForVaadin();
        new Actions(getDriver()).doubleClick(wrappedElement).build().perform();
    }

    /**
     * Performs a context-click (right click) action on this element.
     */
    public void contextClick() {
        autoScrollIntoView();
        waitForVaadin();
        new Actions(getDriver()).contextClick(wrappedElement).build().perform();
    }

    public <T extends TestBenchElement> T wrap(Class<T> elementType) {
        return TestBench.wrap(this, elementType);
    }

    @Override
    public TestBenchCommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    @Override
    public WebDriver getDriver() {
        return getCommandExecutor().getDriver();
    }

    /**
     * Returns this TestBenchElement cast to a SearchContext. Method provided
     * for compatibility and consistency.
     */
    @Override
    public SearchContext getContext() {
        return this;
    }

    /**
     * Move browser focus to this Element
     */
    public void focus() {
        waitForVaadin();
        getCommandExecutor().focusElement(this);
    }

    protected static List<TestBenchElement> wrapElements(
            List<WebElement> elements,
            TestBenchCommandExecutor commandExecutor) {
        List<TestBenchElement> wrappedList = new ArrayList<>();

        for (WebElement e : elements) {
            wrappedList.add(wrapElement(e, commandExecutor));
        }

        return wrappedList;
    }

    protected static TestBenchElement wrapElement(WebElement element,
            TestBenchCommandExecutor commandExecutor) {
        if (element instanceof TestBenchElement) {
            return (TestBenchElement) element;
        } else {
            return TestBench.createElement(element, commandExecutor);
        }
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target)
            throws WebDriverException {
        waitForVaadin();
        return wrappedElement.getScreenshotAs(target);
    }

    @Override
    public Rectangle getRect() {
        waitForVaadin();
        return wrappedElement.getRect();
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
        if (wrappedElement == null) {
            return false;
        }
        return wrappedElement.equals(obj);
    }

    @Override
    public int hashCode() {
        if (wrappedElement == null) {
            return 32;
        }

        return wrappedElement.hashCode();
    }

    @Override
    public boolean compareScreen(String referenceId) throws IOException {
        return ScreenshotComparator.compareScreen(referenceId,
                getCommandExecutor().getReferenceNameGenerator(),
                getCommandExecutor().getImageComparison(), this,
                (HasCapabilities) getDriver());
    }

    @Override
    public boolean compareScreen(File reference) throws IOException {
        return ScreenshotComparator.compareScreen(reference,
                getCommandExecutor().getImageComparison(),
                (TakesScreenshot) this, (HasCapabilities) getDriver());

    }

    @Override
    public boolean compareScreen(BufferedImage reference, String referenceName)
            throws IOException {
        return ScreenshotComparator.compareScreen(reference, referenceName,
                getCommandExecutor().getImageComparison(),
                (TakesScreenshot) this, (HasCapabilities) getDriver());
    }

    /***
     * Scrolls the element into the visible area of the browser window
     */
    public void scrollIntoView() {
        var params = Map.<String, Object>of("block", "end", "inline", "end");
        callFunction("scrollIntoView", params);
    }

    /**
     * Scrolls the element into the visible area of the browser window if
     * {@link TestBenchCommands#isAutoScrollIntoView()} is enabled and the
     * element is not displayed
     */
    private void autoScrollIntoView() {
        try {
            if (getCommandExecutor().isAutoScrollIntoView()) {
                if (!isElementInViewport()) {
                    scrollIntoView();
                }
            }
        } catch (Exception e) {
        }
    }

    private boolean isElementInViewport() {
        try {
            Boolean result = (Boolean) executeScript(
                    """
                            function isVisible(elem) {
                              // Check if element has zero dimensions (truly hidden)
                              if (!elem.offsetParent && elem.offsetWidth === 0 && elem.offsetHeight === 0) {
                                return false;
                              }

                              var rect = elem.getBoundingClientRect();
                              if (rect.width === 0 || rect.height === 0) {
                                return false;
                              }

                              // Check if element intersects with viewport
                              var windowHeight = window.innerHeight || document.documentElement.clientHeight;
                              var windowWidth = window.innerWidth || document.documentElement.clientWidth;
                              if (rect.bottom < 0 || rect.top > windowHeight || rect.right < 0 || rect.left > windowWidth) {
                                return false;
                              }

                              // Check if clipped by any scrollable ancestor
                              var parent = elem.parentElement;
                              while (parent && parent !== document.body) {
                                var style = window.getComputedStyle(parent);
                                var overflow = style.overflow + style.overflowX + style.overflowY;

                                if (overflow.includes('hidden') || overflow.includes('scroll') || overflow.includes('auto')) {
                                  var parentRect = parent.getBoundingClientRect();
                                  // Check if element is outside parent's visible area
                                  if (rect.bottom < parentRect.top || rect.top > parentRect.bottom ||
                                      rect.right < parentRect.left || rect.left > parentRect.right) {
                                    return false;
                                  }
                                }
                                parent = parent.parentElement;
                              }
                              return true;
                            }
                            return isVisible(arguments[0]);
                            """,
                    this);
            return result != null && result;
        } catch (Exception e) {
            return true; // Assume visible on error
        }
    }

    /**
     * Waits the given number of seconds for the given condition to become
     * neither null nor false. {@link NotFoundException}s are ignored by
     * default.
     * <p>
     * Use e.g. as
     * <code>waitUntil(ExpectedConditions.presenceOfElementLocated(by), 10);</code>
     *
     * @param <T>
     *            The return type of the {@link ExpectedCondition} and this
     *            method
     * @param condition
     *            Models a condition that might reasonably be expected to
     *            eventually evaluate to something that is neither null nor
     *            false.
     * @param timeoutInSeconds
     *            The timeout in seconds for the wait.
     * @return The condition's return value if it returned something different
     *         from null or false before the timeout expired.
     *
     * @throws TimeoutException
     *             If the timeout expires.
     *
     * @see FluentWait#until
     * @see ExpectedCondition
     */
    protected <T> T waitUntil(ExpectedCondition<T> condition,
            long timeoutInSeconds) {
        return new WebDriverWait(getDriver(),
                Duration.ofSeconds(timeoutInSeconds)).until(condition);
    }

    /**
     * Waits up to 10 seconds for the given condition to become neither null nor
     * false. {@link NotFoundException}s are ignored by default.
     * <p>
     * Use e.g. as
     * <code>waitUntil(ExpectedConditions.presenceOfElementLocated(by));</code>
     *
     * @param <T>
     *            The return type of the {@link ExpectedCondition} and this
     *            method
     * @param condition
     *            Models a condition that might reasonably be expected to
     *            eventually evaluate to something that is neither null nor
     *            false.
     * @return The condition's return value if it returned something different
     *         from null or false before the timeout expired.
     *
     * @throws TimeoutException
     *             If 10 seconds passed.
     *
     * @see FluentWait#until
     * @see ExpectedCondition
     */
    protected <T> T waitUntil(ExpectedCondition<T> condition) {
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
     * <p>
     * This method wraps any returned {@link WebElement} as
     * {@link TestBenchElement}.
     *
     * @param script
     *            the script to execute
     * @param args
     *            the arguments, available in the script as
     *            {@code arguments[0]...arguments[N]}
     * @return whatever
     *         {@link JavascriptExecutor#executeScript(String, Object...)}
     *         returns
     * @throws UnsupportedOperationException
     *             if the underlying driver does not support JavaScript
     *             execution
     * @see JavascriptExecutor#executeScript(String, Object...)
     */
    protected Object executeScript(String script, Object... args) {
        return getCommandExecutor().executeScript(script, args);
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
     */
    public TestBenchElement getParent() {
        return getPropertyElement("parentElement");
    }

    /**
     * Not implemented. This method is not supported for the
     * {@link TestBenchElement}. Use {@code $} instead to find elements in the
     * shadow root.
     *
     * Example usage:
     *
     * <pre>
     * {@code
     * TestBenchElement element = ...;
     * TestBenchElement child = element.$(TestBenchElement.class).id("elementid");
     * }
     * </pre>
     *
     * @return the {@link SearchContext} representing the shadow root.
     * @deprecated This method is deprecated. Use {@code $} instead to find
     *             elements in the shadow root.
     */
    @Override
    @Deprecated
    public SearchContext getShadowRoot() {
        throw new UnsupportedOperationException("getShadowRoot");
    }

    /**
     * Get list of immediate children of this element.
     *
     * @return List of TestBenchElements
     */
    public List<TestBenchElement> getChildren() {
        return getPropertyElements("children");
    }

    /**
     * Hover mouse pointer on this element.
     *
     * Note: This works well only if this element is atomic.
     */
    public void hover() {
        Actions action = new Actions(getDriver());
        action.moveToElement(this).perform();
    }
}
