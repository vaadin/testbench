package com.vaadin.testbench;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.WrapsDriver;

public class TestBenchDriver<WD extends WebDriver> implements
        InvocationHandler, WrapsDriver {

    @SuppressWarnings("unchecked")
    public static <WD extends WebDriver> WD create(WD driver) {
        Set<Class<?>> allInterfaces = extractInterfaces(driver);
        final Class<?>[] allInterfacesArray = allInterfaces
                .toArray(new Class<?>[allInterfaces.size()]);
        Object proxy = Proxy.newProxyInstance(driver.getClass()
                .getClassLoader(), allInterfacesArray,
                new TestBenchDriver<WebDriver>(driver));
        return (WD) proxy;
    }

    private static Set<Class<?>> extractInterfaces(final Object object) {
        final Set<Class<?>> allInterfaces = new HashSet<Class<?>>();
        extractInterfaces(allInterfaces, object.getClass());

        return allInterfaces;
    }

    private static void extractInterfaces(final Set<Class<?>> addTo,
            final Class<?> clazz) {
        if (clazz == null || Object.class.equals(clazz)) {
            return; // Done
        }

        final Class<?>[] classes = clazz.getInterfaces();
        for (final Class<?> interfaceClass : classes) {
            addTo.add(interfaceClass);
            for (final Class<?> superInterface : interfaceClass.getInterfaces()) {
                addTo.add(superInterface);
                extractInterfaces(addTo, superInterface);
            }
        }
        extractInterfaces(addTo, clazz.getSuperclass());
    }

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

    private HashMap<Method, Method> cachedMethodMap = new HashMap<Method, Method>();

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Method actualMethod = null;
        if (!cachedMethodMap.containsKey(method)) {
            actualMethod = actualDriver.getClass().getMethod(method.getName(),
                    method.getParameterTypes());
            cachedMethodMap.put(method, actualMethod);
        } else {
            actualMethod = cachedMethodMap.get(method);
        }
        return actualMethod.invoke(actualDriver, args);
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

}
