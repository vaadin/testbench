package com.vaadin.testbench;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;

import com.google.common.collect.ImmutableMap;
import com.vaadin.testbench.commands.TestBenchCommands;

public class TestBenchDriver<WD extends WebDriver> implements
        InvocationHandler, WrapsDriver, TestBenchCommands {
    private static final Logger LOGGER = Logger.getLogger(TestBenchDriver.class
            .getName());

    private WD actualDriver;

    /**
     * Constructs a TestBenchDriver using the provided web driver for the actual
     * driving.
     * 
     * @param webDriver
     */
    public TestBenchDriver(WD webDriver) {
        actualDriver = webDriver;
    }

    private HashMap<Method, Method> proxiedMethodCache = new HashMap<Method, Method>();
    private HashMap<Method, Method> implementedMethodCache = new HashMap<Method, Method>();
    private String testName;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        if (!isMethodCached(method)) {
            Method actualMethod = null;
            try {
                // Is it a method in the TestBenchCommands interface?
                actualMethod = TestBenchCommands.class.getMethod(
                        method.getName(), method.getParameterTypes());
                implementedMethodCache.put(method, actualMethod);
            } catch (Exception e) {
                // It's probably a method implemented by the actual driver.
                actualMethod = actualDriver.getClass().getMethod(
                        method.getName(), method.getParameterTypes());
                proxiedMethodCache.put(method, actualMethod);
            }
        }
        if (proxiedMethodCache.containsKey(method)) {
            return proxiedMethodCache.get(method).invoke(actualDriver, args);
        }
        return implementedMethodCache.get(method).invoke(this, args);
    }

    private boolean isMethodCached(Method method) {
        return proxiedMethodCache.containsKey(method)
                || implementedMethodCache.containsKey(method);
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
}
