package com.vaadin.testbench;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
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
     * @see com.vaadin.testbench.commands.TestBenchCommands#setCanvasSize(int,
     * int)
     */
    @Override
    public void setCanvasSize(int w, int h) {
        actualDriver.manage().window().setSize(new Dimension(w, h));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.testbench.commands.TestBenchCommands#getCanvasSize()
     */
    @Override
    public String getCanvasSize() {
        Dimension dim = actualDriver.manage().window().getSize();
        Point pos = actualDriver.manage().window().getPosition();
        return "0,0," + dim.getWidth() + "," + dim.getHeight() + ","
                + pos.getX() + "," + pos.getY();
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
     * com.vaadin.testbench.commands.TestBenchCommands#captureScreenshotToString
     * ()
     */
    @Override
    public String captureScreenshotToString() {
        if (actualDriver instanceof TakesScreenshot) {
            return ((TakesScreenshot) actualDriver)
                    .getScreenshotAs(OutputType.BASE64);
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
}
