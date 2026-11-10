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

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;

/**
 * A {@link TestBenchElement} seen as a Selenium {@link WebElement}, where a
 * Selenium signature demands one: the driver's {@code findElement},
 * {@link TestBenchElement#getContext()}, an element screenshot. Every call goes
 * to the element, so it waits for Vaadin as the element does.
 * <p>
 * {@link ElementQuery#context(SearchContext)} and
 * {@link TestBench#createElement(Class, WebElement, com.vaadin.testbench.commands.TestBenchCommandExecutor)}
 * see through it to the element.
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
        return element.getWrappedElement();
    }

    @Override
    public void click() {
        element.click();
    }

    @Override
    public void submit() {
        element.submit();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        element.sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        element.clear();
    }

    @Override
    public String getTagName() {
        return element.getTagName();
    }

    @Override
    public String getDomProperty(String name) {
        return element.getDomProperty(name);
    }

    @Override
    public String getDomAttribute(String name) {
        return element.getDomAttribute(name);
    }

    @Override
    public String getAttribute(String name) {
        return element.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return element.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return element.isEnabled();
    }

    @Override
    public String getText() {
        return element.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return element.findElements(by).stream()
                .map(TestBenchElement::asWebElement).toList();
    }

    @Override
    public WebElement findElement(By by) {
        return element.findElement(by).asWebElement();
    }

    @Override
    @SuppressWarnings("deprecation")
    public SearchContext getShadowRoot() {
        return element.getShadowRoot();
    }

    @Override
    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    @Override
    public Point getLocation() {
        return element.getLocation();
    }

    @Override
    public Dimension getSize() {
        return element.getSize();
    }

    @Override
    public Rectangle getRect() {
        return element.getRect();
    }

    @Override
    public String getCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target)
            throws WebDriverException {
        return element.getScreenshotAs(target);
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
