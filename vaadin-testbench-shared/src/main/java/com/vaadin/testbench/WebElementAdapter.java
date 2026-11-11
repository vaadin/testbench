/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.List;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;

import com.vaadin.testbench.backend.selenium.SeleniumBackend;

/**
 * A {@link TestBenchElement} seen as a Selenium {@link WebElement}, where a
 * Selenium signature demands one: the driver's {@code findElement},
 * {@link TestBenchElement#getContext()}, an element screenshot. Every call goes
 * to the element, so it waits for Vaadin as the element does.
 * <p>
 * It answers as a Selenium element would: TestBench's own exceptions and
 * geometry come back as Selenium's, so that Selenium code catching them, such
 * as {@code ExpectedConditions.stalenessOf}, keeps working.
 * <p>
 * {@link TestBench#createElement(Class, WebElement, com.vaadin.testbench.commands.TestBenchCommandExecutor)}
 * sees through it to the element.
 */
final class WebElementAdapter implements WebElement, WrapsElement {

    private final TestBenchElement element;

    WebElementAdapter(TestBenchElement element) {
        this.element = element;
    }

    TestBenchElement getElement() {
        return element;
    }

    @Override
    public WebElement getWrappedElement() {
        return SeleniumBackend.webElementOf(element.getElementBackend());
    }

    @Override
    public void click() {
        seleniumRun(element::click);
    }

    @Override
    public void submit() {
        seleniumRun(element::submit);
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        seleniumRun(() -> element.sendKeys(keysToSend));
    }

    @Override
    public void clear() {
        seleniumRun(element::clear);
    }

    @Override
    public String getTagName() {
        return selenium(element::getTagName);
    }

    @Override
    public String getDomProperty(String name) {
        return selenium(() -> element.getDomProperty(name));
    }

    @Override
    public String getDomAttribute(String name) {
        return selenium(() -> element.getDomAttribute(name));
    }

    @Override
    public String getAttribute(String name) {
        return selenium(() -> element.getAttribute(name));
    }

    @Override
    public boolean isSelected() {
        return selenium(element::isSelected);
    }

    @Override
    public boolean isEnabled() {
        return selenium(element::isEnabled);
    }

    @Override
    public String getText() {
        return selenium(element::getText);
    }

    @Override
    public List<WebElement> findElements(By by) {
        element.waitForVaadin();
        return getWrappedElement().findElements(by).stream()
                .map(found -> TestBench
                        .createElement(found, element.getCommandExecutor())
                        .asWebElement())
                .toList();
    }

    @Override
    public WebElement findElement(By by) {
        element.waitForVaadin();
        return TestBench.createElement(getWrappedElement().findElement(by),
                element.getCommandExecutor()).asWebElement();
    }

    /** Not supported, as it never was: {@code $} searches the shadow root. */
    @Override
    public SearchContext getShadowRoot() {
        throw new UnsupportedOperationException("getShadowRoot");
    }

    @Override
    public boolean isDisplayed() {
        return selenium(element::isDisplayed);
    }

    @Override
    public Point getLocation() {
        com.vaadin.testbench.Point location = selenium(element::getLocation);
        return new Point(location.getX(), location.getY());
    }

    @Override
    public Dimension getSize() {
        com.vaadin.testbench.Dimension size = selenium(element::getSize);
        return new Dimension(size.getWidth(), size.getHeight());
    }

    @Override
    public Rectangle getRect() {
        com.vaadin.testbench.Rectangle rect = selenium(element::getRect);
        return new Rectangle(rect.getX(), rect.getY(), rect.getHeight(),
                rect.getWidth());
    }

    @Override
    public String getCssValue(String propertyName) {
        return selenium(() -> element.getCssValue(propertyName));
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target)
            throws WebDriverException {
        return selenium(() -> {
            element.waitForVaadin();
            return target.convertFromPngBytes(
                    element.getElementBackend().getScreenshot());
        });
    }

    private static <T> T selenium(Supplier<T> call) {
        try {
            return call.get();
        } catch (StaleElementReferenceException e) {
            throw new org.openqa.selenium.StaleElementReferenceException(
                    e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new org.openqa.selenium.NoSuchElementException(e.getMessage(),
                    e);
        } catch (TimeoutException e) {
            throw new org.openqa.selenium.TimeoutException(e.getMessage(), e);
        }
    }

    private static void seleniumRun(Runnable call) {
        selenium(() -> {
            call.run();
            return null;
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WebElementAdapter other) {
            return element.equals(other.element);
        }
        return element.equals(obj);
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }

    @Override
    public String toString() {
        return element.toString();
    }
}
