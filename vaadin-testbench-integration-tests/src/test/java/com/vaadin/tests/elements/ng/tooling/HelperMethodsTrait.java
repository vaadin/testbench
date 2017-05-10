package com.vaadin.tests.elements.ng.tooling;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.UIProvider;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.ui.UI;

/**
 *
 */
public interface HelperMethodsTrait {

    /**
     * Height of the screenshots we want to capture
     */
    int SCREENSHOT_HEIGHT = 850;

    /**
     * Width of the screenshots we want to capture
     */
    int SCREENSHOT_WIDTH = 1500;

    /**
     * Timeout used by the TB grid
     */
    int BROWSER_TIMEOUT_IN_MS = 30 * 1000;

    int BROWSER_INIT_ATTEMPTS = 5;


    WebDriver getDriver();

    default WebElement getTooltipElement() {
        return getDriver().findElement(
            com.vaadin.testbench.By.className("v-tooltip-text"));
    }

    default Coordinates getCoordinates(TestBenchElement element) {
        return ((Locatable) element.getWrappedElement()).getCoordinates();
    }

    default boolean hasDebugMessage(String message) {
        return getDebugMessage(message) != null;
    }

    default WebElement getDebugMessage(String message) {
        return getDriver().findElement(By.xpath(String.format(
            "//span[@class='v-debugwindow-message' and text()='%s']",
            message)));
    }

    default void waitForDebugMessage(final String expectedMessage) {
        waitForDebugMessage(expectedMessage, 30);
    }

    default void waitForDebugMessage(final String expectedMessage, int timeout) {
        waitUntil(input -> hasDebugMessage(expectedMessage), timeout);
    }

    default void clearDebugMessages() {
        getDriver().findElement(
            By.xpath("//button[@class='v-debugwindow-button' and @title='Clear log']"))
              .click();
    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()}, optionally with
     * debug window and/or push (depending on {@link #isDebug()} and
     * {@link #isPush()}.
     */
    default void openTestURL() {
        openTestURL("");
    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()}, optionally with
     * debug window and/or push (depending on {@link #isDebug()} and
     * {@link #isPush()}.
     */
    default void openTestURL(String extraParameters) {
        String url = getTestUrl();
        url = url.contains("?")
            ? url + "&" + extraParameters
            : url + "?" + extraParameters;
        getDriver().get(url);
    }

    /**
     * Returns the full URL to be used for the test
     *
     * @return the full URL for the test
     */
    default String getTestUrl() {
        String baseUrl = getBaseURL();
        return (baseUrl.endsWith("/"))
            ? baseUrl.substring(0, baseUrl.length() - 1) + getDeploymentPath()
            : baseUrl + getDeploymentPath();
    }


    /**
     * Finds an element based on the part of a TB2 style locator following the
     * :: (e.g. vaadin=runLabelModes::PID_Scheckboxaction-Enabled/domChild[0] ->
     * PID_Scheckboxaction-Enabled/domChild[0]).
     *
     * @param vaadinLocator
     *     The part following :: of the vaadin locator string
     * @return
     */
    default WebElement vaadinElement(String vaadinLocator) {
        return getDriver().findElement(vaadinLocator(vaadinLocator));
    }

    /**
     * Uses JavaScript to determine the currently focused element.
     *
     * @return Focused element or null
     */
    default WebElement getFocusedElement() {
        Object focusedElement = executeScript("return document.activeElement");
        return null != focusedElement
            ? (WebElement) focusedElement
            : null;
    }

    /**
     * Executes the given Javascript
     *
     * @param script
     *     the script to execute
     * @return whatever
     * {@link JavascriptExecutor#executeScript(String, Object...)}
     * returns
     */
    default Object executeScript(String script) {
        return ((JavascriptExecutor) getDriver()).executeScript(script);
    }

    /**
     * Find a Vaadin element based on its id given using Component.setId
     *
     * @param id
     *     The id to locate
     * @return
     */
    default WebElement vaadinElementById(String id) {
        return getDriver().findElement(vaadinLocatorById(id));
    }

    /**
     * Finds a {@link By} locator based on the part of a TB2 style locator
     * following the :: (e.g.
     * vaadin=runLabelModes::PID_Scheckboxaction-Enabled/domChild[0] ->
     * PID_Scheckboxaction-Enabled/domChild[0]).
     *
     * @param vaadinLocator
     *     The part following :: of the vaadin locator string
     * @return
     */
    default By vaadinLocator(String vaadinLocator) {
        String base = getApplicationId(getDeploymentPath());

        base += "::";
        return com.vaadin.testbench.By.vaadin(base + vaadinLocator);
    }

    /**
     * Constructs a {@link By} locator for the id given using Component.setId
     *
     * @param id
     *     The id to locate
     * @return a locator for the given id
     */
    default By vaadinLocatorById(String id) {
        return vaadinLocator("PID_S" + id);
    }

    /**
     * Waits up to 10s for the given condition to become true. Use e.g. as
     * {@link #waitUntil(ExpectedConditions.textToBePresentInElement(by, text))}
     *
     * @param condition
     *     the condition to wait for to become true
     */
    default <T> void waitUntil(ExpectedCondition<T> condition) {
        waitUntil(condition, 10);
    }

    /**
     * Waits the given number of seconds for the given condition to become true.
     * Use e.g. as {@link
     * #waitUntil(ExpectedConditions.textToBePresentInElement(by, text))}
     *
     * @param condition
     *     the condition to wait for to become true
     */
    default <T> void waitUntil(ExpectedCondition<T> condition,
                                 long timeoutInSeconds) {
        new WebDriverWait(getDriver(), timeoutInSeconds).until(condition);
    }

    /**
     * Waits up to 10s for the given condition to become false. Use e.g. as
     * {@link #waitUntilNot(ExpectedConditions.textToBePresentInElement(by,
     * text))}
     *
     * @param condition
     *     the condition to wait for to become false
     */
    default <T> void waitUntilNot(ExpectedCondition<T> condition) {
        waitUntilNot(condition, 10);
    }

    /**
     * Waits the given number of seconds for the given condition to become
     * false. Use e.g. as {@link
     * #waitUntilNot(ExpectedConditions.textToBePresentInElement(by, text))}
     *
     * @param condition
     *     the condition to wait for to become false
     */
    default <T> void waitUntilNot(ExpectedCondition<T> condition,
                                    long timeoutInSeconds) {
        waitUntil(ExpectedConditions.not(condition), timeoutInSeconds);
    }

    default void waitForElementPresent(final By by) {
        waitUntil(ExpectedConditions.presenceOfElementLocated(by));
    }

    default void waitForElementVisible(final By by) {
        waitUntil(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * Checks if the given element has the given class name.
     * <p>
     * Matches only full class names, i.e. has ("foo") does not match
     * class="foobar"
     *
     * @param element
     * @param className
     * @return
     */
    default boolean hasCssClass(WebElement element, String className) {
        String classes = element.getAttribute("class");
        if (classes == null || classes.isEmpty()) {
            return (className == null || className.isEmpty());
        }

        for (String cls : classes.split(" ")) {
            if (className.equals(cls)) {
                return true;
            }
        }

        return false;
    }

    /**
     * For tests extending {@link AbstractTestUIWithLog}, returns the element
     * for the Nth log row
     *
     * @param rowNr
     *     The log row to retrieve
     * @return the Nth log row
     */
    default WebElement getLogRowElement(int rowNr) {
        return vaadinElementById("Log_row_" + rowNr);
    }

    /**
     * For tests extending {@link AbstractTestUIWithLog}, returns the text in
     * the Nth log row
     *
     * @param rowNr
     *     The log row to retrieve text for
     * @return the text in the log row
     */
    default String getLogRow(int rowNr) {
        return getLogRowElement(rowNr).getText();
    }





    /**
     * Returns whether to run the test in debug mode (with the debug console
     * open) or not
     *
     * @return true to run with the debug window open, false by default
     */
    boolean isDebug();

    /**
     * Sets whether to run the test in debug mode (with the debug console open)
     * or not.
     *
     * @param debug
     *     true to open debug window, false otherwise
     */
    void setDebug(boolean debug);

    /**
     * Returns whether to run the test with push enabled (using /run-push) or
     * not. Note that push tests can and should typically be created using @Push
     * on the UI instead of overriding this method
     *
     * @return true if /run-push is used, false otherwise
     */
    boolean isPush();
    /**
     * Sets whether to run the test with push enabled (using /run-push) or not.
     * Note that push tests can and should typically be created using @Push on
     * the UI instead of overriding this method
     *
     * @param push
     *     true to use /run-push in the test, false otherwise
     */
    void setPush(boolean push);




    /**
     * Returns the path for the given UI class when deployed on the test server.
     * The path contains the full path (appended to hostname+port) and must
     * start with a slash.
     * <p>
     * This method takes into account {@link #isPush()} and {@link #isDebug()}
     * when the path is generated.
     *
     * @param uiClass
     * @param push
     *     true if "?debug" should be added
     * @param debug
     *     true if /run-push should be used instead of /run
     * @return The path to the given UI class
     */
    default String getDeploymentPath(Class<?> uiClass) {
        String runPath = "";
        if (UI.class.isAssignableFrom(uiClass)) {
            return runPath + "/" + uiClass.getSimpleName()
                   + (isDebug() ? "?debug" : "");
        } else if (LegacyApplication.class.isAssignableFrom(uiClass)) {
            return runPath + "/" + uiClass.getSimpleName()
                   + "?restartApplication" + (isDebug() ? "&debug" : "");
        } else {
            throw new IllegalArgumentException(
                "Unable to determine path for enclosing class "
                + uiClass.getCanonicalName());
        }
    }

    /**
     * Used to determine what URL to initially open for the test
     *
     * @return The base URL for the test. Does not include a trailing slash.
     */
    default String getBaseURL() {
        return "http://" + getDeploymentHostname() + ":" + getDeploymentPort();
    }

    /**
     * Generates the application id based on the URL in a way compatible with
     * VaadinServletService.
     *
     * @param pathWithQueryParameters
     *     The path part of the URL, possibly still containing query
     *     parameters
     * @return The application ID string used in Vaadin locators
     */
    default String getApplicationId(String pathWithQueryParameters) {
        // Remove any possible URL parameters
        String pathWithoutQueryParameters = pathWithQueryParameters.replaceAll(
            "\\?.*", "");
        if ("".equals(pathWithoutQueryParameters)) {
            return "ROOT";
        }

        // Retain only a-z and numbers
        return pathWithoutQueryParameters.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * Sleeps for the given number of ms but ensures that the browser connection
     * does not time out.
     *
     * @param timeoutMillis
     *     Number of ms to wait
     * @throws InterruptedException
     */
    default void sleep(int timeoutMillis)
        throws InterruptedException {
        while (timeoutMillis > 0) {
            int d = Math.min(BROWSER_TIMEOUT_IN_MS, timeoutMillis);
            Thread.sleep(d);
            timeoutMillis -= d;

            // Do something to keep the connection alive
            getDriver().getTitle();
        }
    }

    /**
     * Returns the mouse object for doing mouse commands
     *
     * @return Returns the mouse
     */
    default Mouse getMouse() {
        return ((HasInputDevices) getDriver()).getMouse();
    }

    /**
     * Returns the keyboard object for controlling keyboard events
     *
     * @return Return the keyboard
     */
    default Keyboard getKeyboard() {
        return ((HasInputDevices) getDriver()).getKeyboard();
    }

    default void openDebugLogTab() {

        waitUntil(input -> {
            WebElement element = getDebugLogButton();
            return element != null;
        }, 15);
        getDebugLogButton().click();
    }

    default WebElement getDebugLogButton() {
        return findElement(By.xpath("//button[@title='Debug message log']"));
    }

    WebElement findElement(By by);

    /**
     * Should the "require window focus" be enabled for Internet Explorer.
     * RequireWindowFocus makes tests more stable but seems to be broken with
     * certain commands such as sendKeys. Therefore it is not enabled by default
     * for all tests
     *
     * @return true, to use the "require window focus" feature, false otherwise
     */
    default boolean requireWindowFocusForIE() {
        return false;
    }

    /**
     * Should the "enable persistent hover" be enabled for Internet Explorer.
     * <p>
     * Persistent hovering causes continuous firing of mouse over events at the
     * last location the mouse cursor has been moved to. This is to avoid
     * problems where the real mouse cursor is inside the browser window and
     * Internet Explorer uses that location for some undefined operation
     * (http://
     * jimevansmusic.blogspot.fi/2012/06/whats-wrong-with-internet-explorer
     * .html)
     *
     * @return true, to use the "persistent hover" feature, false otherwise
     */
    default boolean usePersistentHoverForIE() {
        return true;
    }



    /**
     * Used to determine what port the test is running on
     *
     * @return The port teh test is running on, by default 8888
     */
    default int getDeploymentPort() {
        return 8080;
    }


    /**
     * Returns the path that should be used for the test. The path contains the
     * full path (appended to hostname+port) and must start with a slash.
     *
     * @param push
     *     true if "?debug" should be added
     * @param debug
     *     true if /run-push should be used instead of /run
     * @return The URL path to the UI class to test
     */
    default String getDeploymentPath() {
        Class<?> uiClass = getUIClass();
        if (uiClass != null) {
            return getDeploymentPath(uiClass);
        } else
            throw new IllegalArgumentException("Unable to determine path for "
                                               + getClass().getCanonicalName());
    }

    /**
     * Returns the UI class the current test is connected to (or in special
     * cases UIProvider or LegacyApplication). Uses the enclosing class if the
     * test class is a static inner class to a UI class.
     * <p>
     * Test which are not enclosed by a UI class must implement this method and
     * return the UI class they want to test.
     * <p>
     * Note that this method will update the test name to the enclosing class to
     * be compatible with TB2 screenshot naming
     *
     * @return the UI class the current test is connected to
     */
    default Class<?> getUIClass() {
        try {
            // Convention: SomeUI UI class is used by SomeUITest
            // or SomeUIIT (IT-integration test)
            String testUIpackage = "com.vaadin.testUI";
            String uiClassName = "";
            if (getClass().getSimpleName().endsWith("Test")) {
                uiClassName = getClass().getSimpleName().replaceFirst("Test$",
                                                                      "");
            } else if (getClass().getSimpleName().endsWith("IT")) {
                uiClassName = getClass().getSimpleName()
                                        .replaceFirst("IT$", "");
            }
            Class<?> cls = Class.forName(testUIpackage + "." + uiClassName);
            if (isSupportedRunnerClass(cls)) {
                return cls;
            }
        } catch (Exception e) {
        }
        throw new RuntimeException(
            "Could not determine UI class. Ensure the test is named UIClassTest and is in the same package as the UIClass");
    }

    /**
     * @return true if the given class is supported by ApplicationServletRunner
     */
    @SuppressWarnings("deprecation")
    default boolean isSupportedRunnerClass(Class<?> cls) {
        return UI.class.isAssignableFrom(cls)
               || UIProvider.class.isAssignableFrom(cls)
               || LegacyApplication.class.isAssignableFrom(cls);
    }


    /**
     * Used to determine what URL to initially open for the test
     *
     * @return the host name of development server
     */
    String getDeploymentHostname();


}
