package com.vaadin.testbench;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 */
public class TestBench {
    @SuppressWarnings("unchecked")
    public static <WD extends WebDriver> WD createDriver(WD driver) {
        Set<Class<?>> allInterfaces = extractInterfaces(driver);
        allInterfaces.addAll(extractInterfaces(TestBenchDriver.class));
        final Class<?>[] allInterfacesArray = allInterfaces
                .toArray(new Class<?>[allInterfaces.size()]);
        Object proxy = Proxy.newProxyInstance(driver.getClass()
                .getClassLoader(), allInterfacesArray,
                new CachedInvocationHandler(new TestBenchDriver<WD>(driver),
                        driver));
        return (WD) proxy;
    }

    @SuppressWarnings("unchecked")
    public static <WE extends WebElement> WE createElement(WE webElement) {
        Set<Class<?>> allInterfaces = extractInterfaces(webElement);
        allInterfaces.addAll(extractInterfaces(TestBenchElement.class));
        final Class<?>[] allInterfacesArray = allInterfaces
                .toArray(new Class<?>[allInterfaces.size()]);
        Object proxy = Proxy.newProxyInstance(webElement.getClass()
                .getClassLoader(), allInterfacesArray,
                new CachedInvocationHandler(
                        new TestBenchElement<WE>(webElement), webElement));
        return (WE) proxy;
    }

    private static Set<Class<?>> extractInterfaces(final Object object) {
        return extractInterfaces(object.getClass());
    }

    private static Set<Class<?>> extractInterfaces(final Class<?> clazz) {
        final Set<Class<?>> allInterfaces = new HashSet<Class<?>>();
        extractInterfaces(allInterfaces, clazz);

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
}
