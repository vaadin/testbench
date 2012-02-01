package com.vaadin.testbench;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;

import com.google.common.collect.ImmutableMap;
import com.vaadin.testbench.commands.TestBenchCommands;

public class TestBenchDriver<WD extends WebDriver> implements WrapsDriver,
        TestBenchCommands {
    private static final Logger LOGGER = Logger.getLogger(TestBenchDriver.class
            .getName());

    private WD actualDriver;

    /**
     * Constructs a TestBenchDriver using the provided web driver for the actual
     * driving.
     * 
     * @param webDriver
     */
    protected TestBenchDriver(WD webDriver) {
        actualDriver = webDriver;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.internal.WrapsDriver#getWrappedDriver()
     */
    @Override
    public WebDriver getWrappedDriver() {
        return actualDriver;
    }

    protected Response execute(String driverCommand, Map<String, ?> parameters) {
        try {
            Method exec = RemoteWebDriver.class.getMethod("execute",
                    String.class, Map.class);
            exec.setAccessible(true);
            return (Response) exec.invoke(actualDriver, driverCommand,
                    parameters);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#setTestName(java.lang
     * .String)
     */
    @Override
    public void setTestName(String testName) {
        if (actualDriver instanceof RemoteWebDriver) {
            execute(TestBenchCommands.SET_TEST_NAME,
                    ImmutableMap.of("name", testName));
        } else {
            LOGGER.info(String.format("Currently running \"%s\"", testName));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#getRemoteControlName()
     */
    @Override
    public String getRemoteControlName() {
        InetAddress ia = null;
        try {
            if (actualDriver instanceof RemoteWebDriver) {
                RemoteWebDriver rwd = (RemoteWebDriver) actualDriver;
                if (rwd.getCommandExecutor() instanceof HttpCommandExecutor) {
                    ia = InetAddress.getByName(((HttpCommandExecutor) rwd
                            .getCommandExecutor()).getAddressOfRemoteServer()
                            .getHost());
                }
            } else {
                ia = InetAddress.getLocalHost();
            }
        } catch (UnknownHostException e) {
            LOGGER.log(Level.WARNING, "Could not find name of remote control",
                    e);
            return "unknown";
        }

        if (ia != null) {
            return String.format("%s (%s)", ia.getCanonicalHostName(),
                    ia.getHostAddress());
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchElementCommands#expectDialog()
     */
    @Override
    public void expectDialog(WebElement element, String value) {
        String[] val = value.split(":");
        Actions actions = new Actions(actualDriver);
        // Press modifier key(s)
        if (val.length > 1) {
            if (val[1].contains("shift")) {
                actions = actions.keyDown(Keys.SHIFT);
            }
            if (val[1].contains("ctrl")) {
                actions = actions.keyDown(Keys.CONTROL);
            }
            if (val[1].contains("alt")) {
                actions = actions.keyDown(Keys.ALT);
            }
        }
        actions = actions.click(element);
        // Release modifier key(s)
        if (val.length > 1) {
            if (val[1].contains("shift")) {
                actions = actions.keyUp(Keys.SHIFT);
            }
            if (val[1].contains("ctrl")) {
                actions = actions.keyUp(Keys.CONTROL);
            }
            if (val[1].contains("alt")) {
                actions = actions.keyUp(Keys.ALT);
            }
        }
        actions.perform();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#closeNotification(org
     * .openqa.selenium.WebElement)
     */
    @Override
    public boolean closeNotification(WebElement element) {
        element.click();
        // Wait for 5000 ms or until the element is no longer visible.
        int times = 0;
        while (element.isDisplayed() || times > 25) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            times++;
        }
        return element.isDisplayed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.testbench.commands.TestBenchElementCommands#showTooltip()
     */
    @Override
    public void showTooltip(WebElement element) {
        new Actions(actualDriver).moveToElement(element).perform();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#scroll(org.openqa.selenium
     * .WebElement, int)
     */
    @Override
    public void scroll(WebElement element, int scrollTop) {
        JavascriptExecutor js = (JavascriptExecutor) actualDriver;
        js.executeScript("arguments[0].setScrollTop(arguments[1])", element,
                scrollTop);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#scrollLeft(org.openqa
     * .selenium.WebElement, int)
     */
    @Override
    public void scrollLeft(WebElement element, int scrollLeft) {
        JavascriptExecutor js = (JavascriptExecutor) actualDriver;
        js.executeScript("arguments[0].setScrollLeft(arguments[1])", element,
                scrollLeft);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.testbench.commands.TestBenchCommands#waitForVaadin()
     */
    @Override
    public void waitForVaadin() {
        String getVaadinConnector = "function getVaadinConnector(wnd) {\n"
                + "  if (wnd.wrappedJSObject) { \n"
                + "    wnd = wnd.wrappedJSObject; \n" + "  } \n"
                + "  var connector = null; \n" + "  if (wnd.itmill) { \n"
                + "    connector = wnd.itmill; \n"
                + "  } else if (wnd.vaadin) { \n"
                + "    connector = wnd.vaadin; \n" + "  } \n"
                + "  return connector; \n" + "} \n";
        String waitForVaadin = "        // max time to wait for toolkit to settle \n"
                + "var timeout = 20000; \n"
                + "var foundClientOnce = false; \n"
                + "return Selenium.decorateFunctionWithTimeout( function() { \n"
                + "  var wnd = selenium.browserbot.getCurrentWindow(); \n"
                + "  var connector = getVaadinConnector(wnd); \n"
                + "  if (!connector) { \n"
                +
                // No connector found == Not a Vaadin application so we don't
                // need to wait
                "    return true; \n"
                + "  } \n"
                + "  var clients = connector.clients; \n"
                + "  if (clients) { \n"
                + "    for ( var client in clients) { \n"
                + "      if (clients[client].isActive()) { \n"
                + "        return false; \n"
                + "      } \n"
                + "    } \n"
                + "    return true; \n" + "  } else { \n" +
                // A Vaadin connector was found so this is most likely a Vaadin
                // application. Keep waiting.
                "    return false; \n" + "  } \n" + "}, timeout);";

        JavascriptExecutor js = (JavascriptExecutor) actualDriver;
        js.executeScript(getVaadinConnector + waitForVaadin);
    }

}
