/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.pro.licensechecker.Capabilities;
import com.vaadin.pro.licensechecker.Capability;
import com.vaadin.pro.licensechecker.LicenseChecker;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;
import com.vaadin.testbench.spi.ElementBackend;

/**
 */
public class TestBench {

    static {
        LicenseChecker.checkLicenseFromStaticBlock("vaadin-testbench",
                TestBenchVersion.testbenchVersion, null,
                Capabilities.of(Capability.PRE_TRIAL));
        // Enable the Java 11+ HTTP client
        System.setProperty("webdriver.http.factory", "jdk-http-client");
    }

    public static void ensureLoaded() {
        // This just ensures that the static initializers have been run so we
        // can set global properties there for all tests
    }

    public static TestBenchDriverProxy createDriver(WebDriver driver) {
        TestBenchCommandExecutor commandExecutor = new TestBenchCommandExecutor(
                new ImageComparison(), new ReferenceNameGenerator());
        return createDriver(driver, commandExecutor);
    }

    public static TestBenchDriverProxy createDriver(WebDriver driver,
            TestBenchCommandExecutor commandExecutor) {
        TestBenchDriverProxy proxy = new TestBenchDriverProxy(driver,
                commandExecutor);
        commandExecutor.setDriver(proxy);
        return proxy;
    }

    public static <T extends TestBenchElement> T wrap(TestBenchElement element,
            Class<T> elementType) {
        return createElement(elementType, element.getElementBackend(),
                element.getCommandExecutor());
    }

    public static TestBenchElement createElement(WebElement webElement,
            TestBenchCommandExecutor tbCommandExecutor) {
        return createElement(TestBenchElement.class, webElement,
                tbCommandExecutor);
    }

    /**
     * Creates an element of the given type around a Selenium element.
     *
     * @return {@code null} if {@code webElement} is {@code null}
     */
    public static <T extends TestBenchElement> T createElement(Class<T> clazz,
            WebElement webElement, TestBenchCommandExecutor tbCommandExecutor) {
        if (webElement == null) {
            return null;
        }
        if (webElement instanceof WebElementAdapter adapter) {
            return wrap(adapter.getElement(), clazz);
        }
        T element = instantiate(clazz);
        element.init(webElement, tbCommandExecutor);
        return element;
    }

    /**
     * Creates an element of the given type for a node a backend has found.
     *
     * @return {@code null} if {@code element} is {@code null}
     */
    public static <T extends TestBenchElement> T createElement(Class<T> clazz,
            ElementBackend element,
            TestBenchCommandExecutor tbCommandExecutor) {
        if (element == null) {
            return null;
        }
        T testBenchElement = instantiate(clazz);
        testBenchElement.init(element, tbCommandExecutor);
        return testBenchElement;
    }

    private static final ClassValue<Constructor<?>> constructors = new ClassValue<>() {
        @Override
        protected Constructor<?> computeValue(Class<?> clazz) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor;
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(clazz.getName()
                        + " needs a constructor without arguments", e);
            }
        }
    };

    // The one reflective step in building an element: the class is only
    // known from $(X.class) or wrap(X.class).
    private static <T extends TestBenchElement> T instantiate(Class<T> clazz) {
        try {
            return clazz.cast(constructors.get(clazz).newInstance());
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(
                    "Unable to create an element of type " + clazz.getName(),
                    e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Unable to create an element of type " + clazz.getName(),
                    e);
        }
    }
}
