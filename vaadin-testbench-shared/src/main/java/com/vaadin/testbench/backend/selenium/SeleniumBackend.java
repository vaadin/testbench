/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.selenium;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.spi.Backend;
import com.vaadin.testbench.spi.ElementBackend;

/**
 * The {@link Backend} on a Selenium {@link WebDriver}.
 */
public class SeleniumBackend implements Backend {

    private final WebDriver driver;

    /**
     * @param driver
     *            the browser's own driver, not TestBench's proxy around it,
     *            which would wait for Vaadin before every script
     */
    public SeleniumBackend(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    /**
     * The Selenium element behind {@code element}, for the code paths that
     * still need one; {@code grep webElementOf} counts them.
     *
     * @return {@code null} for a {@code null} element
     * @throws UnsupportedOperationException
     *             if {@code element} comes from another backend
     */
    public static @Nullable WebElement webElementOf(
            @Nullable ElementBackend element) {
        if (element == null) {
            return null;
        }
        if (element instanceof SeleniumElementBackend selenium) {
            return selenium.getWebElement();
        }
        throw new UnsupportedOperationException(
                "This needs the Selenium backend, not "
                        + element.getClass().getName());
    }

    @Override
    public @Nullable Object executeScript(String script,
            @Nullable Object... args) {
        Object[] seleniumArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            seleniumArgs[i] = toSelenium(args[i]);
        }
        return fromSelenium(
                ((JavascriptExecutor) driver).executeScript(script,
                        seleniumArgs));
    }

    private static @Nullable Object toSelenium(@Nullable Object value) {
        if (value instanceof ElementBackend element) {
            return webElementOf(element);
        }
        if (value instanceof Collection<?> collection) {
            List<Object> list = new ArrayList<>(collection.size());
            for (Object item : collection) {
                list.add(toSelenium(item));
            }
            return list;
        }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> copy = new LinkedHashMap<>();
            map.forEach((key, item) -> copy.put(key, toSelenium(item)));
            return copy;
        }
        return value;
    }

    // Maps are left alone: TestBench has never wrapped the elements in them.
    private @Nullable Object fromSelenium(@Nullable Object value) {
        if (value instanceof WebElement element) {
            return new SeleniumElementBackend(this, element);
        }
        if (value instanceof List<?> list) {
            List<Object> converted = new ArrayList<>(list.size());
            for (Object item : list) {
                converted.add(fromSelenium(item));
            }
            return converted;
        }
        return value;
    }
}
