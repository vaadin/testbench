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
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.WheelInput;

import com.vaadin.testbench.Dimension;
import com.vaadin.testbench.NoSuchElementException;
import com.vaadin.testbench.StaleElementReferenceException;
import com.vaadin.testbench.TimeoutException;
import com.vaadin.testbench.spi.BrowserBackend;
import com.vaadin.testbench.spi.ElementBackend;
import com.vaadin.testbench.spi.Input;

/**
 * The {@link BrowserBackend} on a Selenium {@link WebDriver}.
 */
public class SeleniumBackend implements BrowserBackend {

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
        try {
            return fromSelenium(((JavascriptExecutor) driver)
                    .executeScript(script, toSelenium(args)));
        } catch (WebDriverException e) {
            throw translate(e);
        }
    }

    @Override
    public @Nullable Object executeAsyncScript(String script,
            @Nullable Object... args) {
        try {
            return fromSelenium(((JavascriptExecutor) driver)
                    .executeAsyncScript(script, toSelenium(args)));
        } catch (WebDriverException e) {
            throw translate(e);
        }
    }

    @Override
    public @Nullable String getBrowserName() {
        if (driver instanceof HasCapabilities hasCapabilities) {
            return hasCapabilities.getCapabilities().getBrowserName();
        }
        return null;
    }

    @Override
    public void navigate(String url) {
        driver.get(url);
    }

    @Override
    public void back() {
        driver.navigate().back();
    }

    @Override
    public void forward() {
        driver.navigate().forward();
    }

    @Override
    public void refresh() {
        driver.navigate().refresh();
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public Dimension getWindowSize() {
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();
        return new Dimension(size.getWidth(), size.getHeight());
    }

    @Override
    public void setWindowSize(Dimension size) {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(
                size.getWidth(), size.getHeight()));
    }

    @Override
    public byte[] getScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public void switchToWindow(String handle) {
        try {
            driver.switchTo().window(handle);
        } catch (NoSuchWindowException e) {
            throw new NoSuchElementException(e.getRawMessage(), e);
        }
    }

    @Override
    public void close() {
        driver.close();
    }

    @Override
    public void quit() {
        driver.quit();
    }

    /**
     * Each step as Selenium's {@code Actions} sends it, with its durations: 100
     * ms to move to an element, 200 ms to move by an offset, 250 ms to a point.
     */
    @Override
    public void perform(List<Input> steps) {
        Actions actions = new Actions(driver);
        for (Input step : steps) {
            switch (step) {
            case Input.KeyDown down -> actions.keyDown(key(down.codePoint()));
            case Input.KeyUp up -> actions.keyUp(key(up.codePoint()));
            case Input.MoveToElement move ->
                actions.moveToElement(webElementOf(move.element()),
                        move.xOffset(), move.yOffset());
            case Input.MoveBy move ->
                actions.moveByOffset(move.xOffset(), move.yOffset());
            case Input.MoveTo move ->
                actions.moveToLocation(move.x(), move.y());
            case Input.MouseDown down -> actions.tick(actions.getActivePointer()
                    .createPointerDown(button(down.button()).asArg()));
            case Input.MouseUp up -> actions.tick(actions.getActivePointer()
                    .createPointerUp(button(up.button()).asArg()));
            case Input.Wheel wheel -> actions.scrollFromOrigin(
                    WheelInput.ScrollOrigin.fromViewport(wheel.x(), wheel.y()),
                    wheel.deltaX(), wheel.deltaY());
            case Input.Pause pause -> actions.pause(pause.duration());
            }
        }
        try {
            actions.perform();
        } catch (WebDriverException e) {
            throw translate(e);
        }
    }

    private static String key(int codePoint) {
        return new String(Character.toChars(codePoint));
    }

    private static PointerInput.MouseButton button(Input.Button button) {
        return switch (button) {
        case LEFT -> PointerInput.MouseButton.LEFT;
        case MIDDLE -> PointerInput.MouseButton.MIDDLE;
        case RIGHT -> PointerInput.MouseButton.RIGHT;
        };
    }

    /**
     * TestBench's own exception for Selenium's stale element, not found and
     * timeout ones; any other comes back as it is.
     */
    static RuntimeException translate(WebDriverException e) {
        if (e instanceof org.openqa.selenium.StaleElementReferenceException) {
            return new StaleElementReferenceException(e.getRawMessage(), e);
        }
        if (e instanceof org.openqa.selenium.NoSuchElementException) {
            return new NoSuchElementException(e.getRawMessage(), e);
        }
        if (e instanceof org.openqa.selenium.TimeoutException) {
            return new TimeoutException(e.getRawMessage(), e);
        }
        return e;
    }

    private static Object[] toSelenium(@Nullable Object[] args) {
        Object[] seleniumArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            seleniumArgs[i] = toSelenium(args[i]);
        }
        return seleniumArgs;
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
