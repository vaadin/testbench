/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests;

import org.junit.Before;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.parallel.ParallelTest;
import com.vaadin.ui.Component;

/**
 * Base class for TestBench 3+ tests. All TB3+ tests in the project should
 * extend this class.
 *
 * Provides:
 * <ul>
 * <li>Helpers for browser selection</li>
 * <li>Hub connection setup and teardown</li>
 * <li>Automatic generation of URL for a given test on the development server
 * using {@link #getTestView()}.
 * <li>Generic helpers for creating TB3+ tests</li>
 * </ul>
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractTB3Test extends ParallelTest {
    /**
     * Height of the screenshots we want to capture
     */
    private static final int SCREENSHOT_HEIGHT = 850;

    /**
     * Width of the screenshots we want to capture
     */
    private static final int SCREENSHOT_WIDTH = 1500;

    /**
     * Timeout used by the TB grid
     */
    private static final int BROWSER_TIMEOUT_IN_MS = 30 * 1000;

    static {
        com.vaadin.testbench.Parameters
                .setScreenshotComparisonCursorDetection(true);
    }

    /**
     * Connect to the hub using a remote web driver, set the canvas size and
     * opens the initial URL as specified by {@link #getTestUrl()}
     *
     * @throws Exception
     */
    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        int w = SCREENSHOT_WIDTH;
        int h = SCREENSHOT_HEIGHT;

        try {
            testBench().resizeViewPortTo(w, h);
        } catch (UnsupportedOperationException e) {
            // Opera does not support this...
        }
    }

    protected boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(System.getProperty(key));
    }

    protected Coordinates getCoordinates(TestBenchElement element) {
        return ((Locatable) element.getWrappedElement()).getCoordinates();
    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()}.
     */
    protected void openTestURL() {
        openTestURL("");
    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()} by adding the
     * given parameters.
     */
    protected void openTestURL(String extraParameters) {
        String url = getTestUrl();
        if (url.contains("?")) {
            url = url + "&" + extraParameters;
        } else {
            url = url + "?" + extraParameters;
        }
        driver.get(url);
    }

    /**
     * Returns the full URL to be used for the test
     *
     * @return the full URL for the test
     */
    protected String getTestUrl() {
        String baseUrl = getBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        return baseUrl + getDeploymentPath();
    }

    /**
     * Used to determine what URL to initially open for the test
     *
     * @return the host name of development server
     */
    protected abstract String getDeploymentHostname();

    /**
     * Used to determine what port the test is running on
     *
     * @return The port teh test is running on, by default 8888
     */
    protected int getDeploymentPort() {
        return 8080;
    }

    /**
     * Uses JavaScript to determine the currently focused element.
     *
     * @return Focused element or null
     */
    protected WebElement getFocusedElement() {
        Object focusedElement = executeScript("return document.activeElement");
        if (null != focusedElement) {
            return (WebElement) focusedElement;
        } else {
            return null;
        }
    }

    /**
     * Asserts that {@literal a} is &gt;= {@literal b}
     *
     * @param message
     *            The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *             If comparison fails
     */
    public static final <T> void assertGreaterOrEqual(String message,
            Comparable<T> a, T b) throws AssertionError {
        if (a.compareTo(b) >= 0) {
            return;
        }

        throw new AssertionError(decorate(message, a, b));
    }

    /**
     * Asserts that {@literal a} is &gt; {@literal b}
     *
     * @param message
     *            The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *             If comparison fails
     */
    public static final <T> void assertGreater(String message, Comparable<T> a,
            T b) throws AssertionError {
        if (a.compareTo(b) > 0) {
            return;
        }
        throw new AssertionError(decorate(message, a, b));
    }

    /**
     * Asserts that {@literal a} is &lt;= {@literal b}
     *
     * @param message
     *            The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *             If comparison fails
     */
    public static final <T> void assertLessThanOrEqual(String message,
            Comparable<T> a, T b) throws AssertionError {
        if (a.compareTo(b) <= 0) {
            return;
        }

        throw new AssertionError(decorate(message, a, b));
    }

    /**
     * Asserts that {@literal a} is &lt; {@literal b}
     *
     * @param message
     *            The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *             If comparison fails
     */
    public static final <T> void assertLessThan(String message, Comparable<T> a,
            T b) throws AssertionError {
        if (a.compareTo(b) < 0) {
            return;
        }
        throw new AssertionError(decorate(message, a, b));
    }

    private static <T> String decorate(String message, Comparable<T> a, T b) {
        message = message.replace("{0}", a.toString());
        message = message.replace("{1}", b.toString());
        return message;
    }

    /**
     * Returns the path that should be used for the test. The path contains the
     * full path (appended to hostname+port) and must start with a slash.
     *
     * @return The URL path to the UI class to test
     */
    protected String getDeploymentPath() {
        return "/" + getTestView().getSimpleName();
    }

    /**
     * Used to determine what URL to initially open for the test
     *
     * @return The base URL for the test. Does not include a trailing slash.
     */
    protected String getBaseURL() {
        return "http://" + getDeploymentHostname() + ":" + getDeploymentPort();
    }

    /**
     * Sleeps for the given number of ms but ensures that the browser connection
     * does not time out.
     *
     * @param timeoutMillis
     *            Number of ms to wait
     * @throws InterruptedException
     */
    protected void sleep(int timeoutMillis) throws InterruptedException {
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
    public Mouse getMouse() {
        return ((HasInputDevices) getDriver()).getMouse();
    }

    /**
     * Returns the keyboard object for controlling keyboard events
     *
     * @return Return the keyboard
     */
    public Keyboard getKeyboard() {
        return ((HasInputDevices) getDriver()).getKeyboard();
    }

    /**
     * Should the "require window focus" be enabled for Internet Explorer.
     * RequireWindowFocus makes tests more stable but seems to be broken with
     * certain commands such as sendKeys. Therefore it is not enabled by default
     * for all tests
     *
     * @return true, to use the "require window focus" feature, false otherwise
     */
    protected boolean requireWindowFocusForIE() {
        return false;
    }

    /**
     * Should the "enable persistent hover" be enabled for Internet Explorer.
     *
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
    protected boolean usePersistentHoverForIE() {
        return true;
    }

    protected abstract Class<? extends Component> getTestView();

}
