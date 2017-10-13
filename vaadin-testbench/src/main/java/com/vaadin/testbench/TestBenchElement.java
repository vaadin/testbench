/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.commands.CanCompareScreenshots;
import com.vaadin.testbench.commands.CanWaitForVaadin;
import com.vaadin.testbench.commands.ScreenshotComparator;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchCommands;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.parallel.BrowserUtil;

import elemental.json.Json;
import elemental.json.JsonValue;

/**
 * TestBenchElement is a WebElement wrapper. It provides Vaadin specific helper
 * functionality. TestBenchElements are created when you search for elements
 * from TestBenchTestCase or a context relative search from TestBenchElement.
 */
public class TestBenchElement extends AbstractHasTestBenchCommandExecutor
        implements WrapsElement, WebElement, TestBenchElementCommands,
        CanWaitForVaadin, HasDriver, CanCompareScreenshots {

    private WebElement actualElement = null;
    private TestBenchCommandExecutor tbCommandExecutor = null;

    protected TestBenchElement() {

    }

    protected TestBenchElement(WebElement webElement,
            TestBenchCommandExecutor tbCommandExecutor) {
        init(webElement, tbCommandExecutor);
    }

    /**
     * TestBenchElement initialization function. If a subclass of
     * TestBenchElement needs to run some initialization code, it should
     * override init(), not this function.
     *
     * @param element
     *            WebElement to wrap
     * @param tbCommandExecutor
     *            TestBenchCommandExecutor instance
     */
    protected void init(WebElement element,
            TestBenchCommandExecutor tbCommandExecutor) {
        if (null == this.tbCommandExecutor) {
            this.tbCommandExecutor = tbCommandExecutor;
            actualElement = element;
            init();
        }
    }

    /**
     * Checks if the current test is running on PhantomJS.
     *
     * @return <code>true</code> if the test is running on PhantomJS,
     *         <code>false</code> otherwise
     */
    protected boolean isPhantomJS() {
        return BrowserUtil.isPhantomJS(getCapabilities());
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
     * Checks if the current test is running on Internet Explorer.
     *
     * @return <code>true</code> if the test is running on Internet Explorer,
     *         <code>false</code> otherwise
     */
    protected boolean isIE() {
        return BrowserUtil.isIE(getCapabilities());
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
     * @see org.openqa.selenium.Capabilities
     * @return information about current browser used
     */
    protected Capabilities getCapabilities() {
        WebDriver driver;
        if (getDriver() instanceof TestBenchDriverProxy) {
            driver = ((TestBenchDriverProxy) getDriver()).getActualDriver();
        } else {
            driver = getDriver();
        }

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
        return actualElement;
    }

    @Override
    public void waitForVaadin() {
        if (getCommandExecutor() != null) {
            getCommandExecutor().waitForVaadin();
        }
    }

    @Override
    public void showTooltip() {
        waitForVaadin();
        new Actions(getCommandExecutor().getWrappedDriver())
                .moveToElement(actualElement).perform();
        // Wait for a small moment for the tooltip to appear
        try {
            Thread.sleep(1000); // VTooltip.OPEN_DELAY = 750;
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Sets the number of pixels that an element's content is scrolled from the
     * top.
     *
     * @param scrollTop
     *            value set to Element.scroll property
     * @see com.vaadin.testbench.commands.TestBenchElementCommands#scroll(int)
     */
    @Override
    public void scroll(int scrollTop) {
        setProperty("scrollTop", scrollTop);
    }

    /**
     * Sets the number of pixels that an element's content is scrolled to the
     * left.
     *
     * @param scrollLeft
     *            value set to Element.scrollLeft property
     * @see com.vaadin.testbench.commands.TestBenchElementCommands#scrollLeft(int)
     */
    @Override
    public void scrollLeft(int scrollLeft) {
        setProperty("scrollLeft", scrollLeft);
    }

    @Override
    public void click() {
        autoScrollIntoView();
        waitForVaadin();
        actualElement.click();
    }

    @Override
    public void submit() {
        click();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        autoScrollIntoView();
        waitForVaadin();
        actualElement.sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        autoScrollIntoView();
        waitForVaadin();
        actualElement.clear();
    }

    @Override
    public String getTagName() {
        waitForVaadin();
        return actualElement.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        waitForVaadin();
        return actualElement.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        autoScrollIntoView();
        waitForVaadin();
        return actualElement.isSelected();
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
        return !hasClassName("v-disabled") && actualElement.isEnabled();
    }

    @Override
    public String getText() {
        autoScrollIntoView();
        waitForVaadin();
        return actualElement.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return (List) TestBenchElement.wrapElements(
                actualElement.findElements(by), getCommandExecutor());
    }

    @Override
    public TestBenchElement findElement(By by) {
        waitForVaadin();
        return wrapElement(actualElement.findElement(by), getCommandExecutor());
    }

    /**
     * Calls the Javascript click method on the element.
     *
     * Useful for elements that are hidden or covered by a pseudo-element on
     * some browser-theme combinations (for instance Firefox-Valo)
     */
    public void clickHiddenElement() {
        callFunction("click");
    }

    @Override
    public boolean isDisplayed() {
        waitForVaadin();
        return actualElement.isDisplayed();
    }

    @Override
    public Point getLocation() {
        waitForVaadin();
        return actualElement.getLocation();
    }

    @Override
    public Dimension getSize() {
        waitForVaadin();
        return actualElement.getSize();
    }

    @Override
    public String getCssValue(String propertyName) {
        waitForVaadin();
        return actualElement.getCssValue(propertyName);
    }

    @Override
    public void click(int x, int y, Keys... modifiers) {
        autoScrollIntoView();
        waitForVaadin();
        Actions actions = new Actions(getCommandExecutor().getWrappedDriver());
        actions.moveToElement(actualElement, x, y);
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

    public void doubleClick() {
        autoScrollIntoView();
        waitForVaadin();
        new Actions(getDriver()).doubleClick(actualElement).build().perform();
        // Wait till vaadin component will process the event. Without it may
        // cause problems with phantomjs
    }

    public void contextClick() {
        autoScrollIntoView();
        waitForVaadin();
        new Actions(getDriver()).contextClick(actualElement).build().perform();
        // Wait till vaadin component will process the event. Without it may
        // cause problems with phantomjs
    }

    @Override
    public <T extends TestBenchElement> T wrap(Class<T> elementType) {
        return TestBench.wrap(this, elementType);
    }

    @Override
    public TestBenchCommandExecutor getCommandExecutor() {
        return tbCommandExecutor;
    }

    @Override
    public WebDriver getDriver() {
        return getCommandExecutor().getWrappedDriver();
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
    @Override
    public void focus() {
        waitForVaadin();
        getCommandExecutor().focusElement(this);
    }

    protected static List<TestBenchElement> wrapElements(
            List<WebElement> elements,
            TestBenchCommandExecutor tbCommandExecutor) {
        List<TestBenchElement> wrappedList = new ArrayList<>();

        for (WebElement e : elements) {
            wrappedList.add(wrapElement(e, tbCommandExecutor));
        }

        return wrappedList;
    }

    protected static TestBenchElement wrapElement(WebElement element,
            TestBenchCommandExecutor tbCommandExecutor) {
        if (element instanceof TestBenchElement) {
            return (TestBenchElement) element;
        } else {
            return TestBench.createElement(element, tbCommandExecutor);
        }
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target)
            throws WebDriverException {
        waitForVaadin();
        return actualElement.getScreenshotAs(target);
    }

    @Override
    public Rectangle getRect() {
        waitForVaadin();
        return actualElement.getRect();
    }

    /**
     * Gets all the class names set for this element.
     *
     * @return a set of class names
     */
    public Set<String> getClassNames() {
        String classAttribute = getAttribute("class");
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
        if (actualElement == null) {
            return false;
        }
        return actualElement.equals(obj);
    }

    @Override
    public int hashCode() {
        if (actualElement == null) {
            return 32;
        }

        return actualElement.hashCode();
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
        callFunction("scrollIntoView");
    }

    /**
     * Scrolls the element into the visible area of the browser window if
     * {@link TestBenchCommands#isAutoScrollIntoView()} is enabled and the
     * element is not displayed
     */
    private void autoScrollIntoView() {
        if (getCommandExecutor().isAutoScrollIntoView()) {
            if (!actualElement.isDisplayed()) {
                scrollIntoView();
            }
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
        return new WebDriverWait(getDriver(), timeoutInSeconds)
                .until(condition);
    }

    /**
     * Waits up to 10 seconds for the given condition to become neither null nor
     * false. {@link NotFoundException}s are ignored by default.
     * <p>
     * Use e.g. as
     * <code>waitUntil(ExpectedConditions.presenceOfElementLocated(by));</code>
     *
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

    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name
     *            the name of the property
     * @param value
     *            the value to set
     */
    public void setProperty(String name, String value) {
        internalSetProperty(name, value);
    }

    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name
     *            the name of the property
     * @param value
     *            the value to set
     */
    public void setProperty(String name, Boolean value) {
        internalSetProperty(name, value);
    }

    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name
     *            the name of the property
     * @param value
     *            the value to set
     */
    public void setProperty(String name, Double value) {
        internalSetProperty(name, value);
    }

    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name
     *            the name of the property
     * @param value
     *            the value to set
     */
    public void setProperty(String name, Integer value) {
        internalSetProperty(name, value);
    }

    /**
     * Gets a JavaScript property of the given element as a string.
     *
     * @param name
     *            the name of the property
     */
    public String getPropertyString(String name) {
        Object value = internalGetProperty(name);
        if (value == null) {
            return null;
        }
        return createJsonValue(value).asString();
    }

    /**
     * Gets a JavaScript property of the given element as a boolean.
     *
     * @param name
     *            the name of the property
     */
    public Boolean getPropertyBoolean(String name) {
        Object value = internalGetProperty(name);
        if (value == null) {
            return null;
        }
        return createJsonValue(value).asBoolean();
    }

    /**
     * Gets a JavaScript property of the given element as a double.
     *
     * @param name
     *            the name of the property
     */
    public Double getPropertyDouble(String name) {
        Object value = internalGetProperty(name);
        if (value == null) {
            return null;
        }
        return createJsonValue(value).asNumber();
    }

    private void internalSetProperty(String name, Object value) {
        if ((isIE() || isFirefox()) && value instanceof Double) {
            // IE 11 fails with java.lang.NumberFormatException
            // if we try to send a double...
            executeScript("arguments[0][arguments[1]]=Number(arguments[2])",
                    this, name, String.valueOf((value)));
        } else {
            executeScript("arguments[0][arguments[1]]=arguments[2]", this, name,
                    value);
        }
    }

    private Object internalGetProperty(String name) {
        String script = "var value = arguments[0][arguments[1]];";
        if (isIE() || isFirefox()) {
            String isNumberScript = script + "return typeof value == 'number';";
            boolean number = (boolean) executeScript(isNumberScript, this,
                    name);

            if (number) {
                String str = (String) executeScript(
                        script + "return value.toString();", this, name);
                return Double.parseDouble(str);
            }
        }
        return executeScript(script + "return value;", this, name);
    }

    private JsonValue createJsonValue(Object value) {
        if (value == null) {
            return Json.createNull();
        } else if (value instanceof String) {
            return Json.create((String) value);
        } else if (value instanceof Number) {
            return Json.create(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            return Json.create((Boolean) value);
        } else {
            throw new IllegalArgumentException(
                    "Type of property is unsupported: "
                            + value.getClass().getName());
        }
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
     *         {@link org.openqa.selenium.JavascriptExecutor#executeScript(String, Object...)}
     *         returns
     * @throws UnsupportedOperationException
     *             if the underlying driver does not support JavaScript
     *             execution
     * @see JavascriptExecutor#executeScript(String, Object...)
     */
    protected Object executeScript(String script, Object... args) {
        return getCommandExecutor().executeScript(script, args);
    }

    /**
     * Invoke the given method on this element using the given arguments as
     * arguments to the method.
     *
     * @param methodName
     *            the method to invoke
     * @param args
     *            the arguments to pass to the method
     * @return the value returned by the method
     */
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

}
