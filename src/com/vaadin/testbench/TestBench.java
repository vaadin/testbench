package com.vaadin.testbench;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.commands.TestBenchCommands;

/**
 */
public class TestBench {
    @SuppressWarnings("unchecked")
    public static <WD extends WebDriver> WD create(WD driver) {
        Set<Class<?>> allInterfaces = extractInterfaces(driver);
        allInterfaces.add(TestBenchCommands.class);
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
}
