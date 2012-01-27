package com.vaadin.testbench;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.WrapsDriver;

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#setTestName(java.lang
     * .String)
     */
    @Override
    public void setTestName(String testName) {
        LOGGER.info("Test name: " + testName);
        this.testName = testName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.testbench.commands.TestBenchCommands#getTestName()
     */
    @Override
    public String getTestName() {
        return testName;
    }

}
