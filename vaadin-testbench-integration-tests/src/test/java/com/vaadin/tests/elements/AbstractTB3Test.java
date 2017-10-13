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

package com.vaadin.tests.elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.vaadin.testbench.TestBenchDriverProxy;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.ParallelTest;
import com.vaadin.testbench.parallel.setup.SetupDriver;
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
 * using {@link #getUIClass()} or by automatically finding an enclosing UI class
 * and based on requested features, e.g. {@link #isDebug()},
 * {@link #isPush()}</li>
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

    private static final int BROWSER_INIT_ATTEMPTS = 5;

    private boolean debug = false;

    private boolean push = false;

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
        // override local driver behaviour, so we can easily specify local
        // PhantomJS
        // with a system property
        if (getBooleanProperty("localPhantom")) {
            WebDriver driver = new SetupDriver()
                    .setupLocalDriver(Browser.PHANTOMJS);
            setDriver(driver);
        } else {
            super.setup();
        }

        int w = SCREENSHOT_WIDTH;
        int h = SCREENSHOT_HEIGHT;

        if (BrowserUtil.isIE8(getDesiredCapabilities())) {
            // IE8 gets size wrong, who would have guessed...
            w += 4;
            h += 4;
        }
        try {
            testBench().resizeViewPortTo(w, h);
        } catch (UnsupportedOperationException e) {
            // Opera does not support this...
        }
    }

    protected boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(System.getProperty(key));
    }

    protected WebElement getTooltipElement() {
        return getDriver().findElement(
                com.vaadin.testbench.By.className("v-tooltip-text"));
    }

    protected Coordinates getCoordinates(TestBenchElement element) {
        return ((Locatable) element.getWrappedElement()).getCoordinates();
    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()}, optionally with
     * debug window and/or push (depending on {@link #isDebug()} and
     * {@link #isPush()}.
     */
    protected void openTestURL() {
        openTestURL("");
    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()}, optionally with
     * debug window and/or push (depending on {@link #isDebug()} and
     * {@link #isPush()}.
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
     * @param push
     *            true if "?debug" should be added
     * @param debug
     *            true if /run-push should be used instead of /run
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

    // FIXME: Remove this once TB4 getRemoteControlName works properly
    private RemoteWebDriver getRemoteDriver() {
        WebDriver d = getDriver();
        if (d instanceof TestBenchDriverProxy) {
            try {
                Field f = TestBenchDriverProxy.class
                        .getDeclaredField("actualDriver");
                f.setAccessible(true);
                return (RemoteWebDriver) f.get(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (d instanceof RemoteWebDriver) {
            return (RemoteWebDriver) d;
        }

        return null;

    }

    // FIXME: Remove this once TB4 getRemoteControlName works properly
    protected String getRemoteControlName() {
        try {
            RemoteWebDriver d = getRemoteDriver();
            if (d == null) {
                return null;
            }
            HttpCommandExecutor ce = (HttpCommandExecutor) d
                    .getCommandExecutor();
            String hostName = ce.getAddressOfRemoteServer().getHost();
            int port = ce.getAddressOfRemoteServer().getPort();
            HttpHost host = new HttpHost(hostName, port);
            DefaultHttpClient client = new DefaultHttpClient();
            URL sessionURL = new URL("http://" + hostName + ":" + port
                    + "/grid/api/testsession?session=" + d.getSessionId());
            BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest(
                    "POST", sessionURL.toExternalForm());
            HttpResponse response = client.execute(host, r);
            JsonObject object = extractObject(response);
            URL myURL = new URL(object.get("proxyId").getAsString());
            if ((myURL.getHost() != null) && (myURL.getPort() != -1)) {
                return myURL.getHost();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JsonObject extractObject(HttpResponse resp)
            throws IOException, JsonSyntaxException {
        InputStream contents = resp.getEntity().getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(contents, writer, "UTF8");
        JsonObject objToReturn = new JsonParser().parse(writer.toString())
                .getAsJsonObject();
        return objToReturn;
    }

    protected abstract Class<? extends Component> getTestView();

}
