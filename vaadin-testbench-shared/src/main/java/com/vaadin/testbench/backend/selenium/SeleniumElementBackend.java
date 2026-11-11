/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.selenium;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.spi.ElementBackend;

/**
 * The {@link ElementBackend} on a Selenium {@link WebElement}.
 */
public class SeleniumElementBackend implements ElementBackend {

    private final @Nullable SeleniumBackend backend;
    private final WebElement element;

    /**
     * @param backend
     *            the browser the element lives in; {@code null} only in unit
     *            tests, where the mouse actions fail
     */
    public SeleniumElementBackend(@Nullable SeleniumBackend backend,
            WebElement element) {
        this.backend = backend;
        this.element = Objects.requireNonNull(element);
    }

    public WebElement getWebElement() {
        return element;
    }

    private Actions actions() {
        return new Actions(
                Objects.requireNonNull(backend, "No browser for this element")
                        .getDriver());
    }

    @Override
    public void click() {
        run(element::click);
    }

    @Override
    public void click(int xOffsetFromCenter, int yOffsetFromCenter,
            CharSequence... modifiers) {
        run(() -> {
            Actions actions = actions().moveToElement(element,
                    xOffsetFromCenter, yOffsetFromCenter);
            for (CharSequence modifier : modifiers) {
                actions.keyDown(modifier);
            }
            actions.click();
            for (CharSequence modifier : modifiers) {
                actions.keyUp(modifier);
            }
            actions.build().perform();
        });
    }

    @Override
    public void doubleClick() {
        run(() -> actions().doubleClick(element).build().perform());
    }

    @Override
    public void contextClick() {
        run(() -> actions().contextClick(element).build().perform());
    }

    @Override
    public void hover() {
        run(() -> actions().moveToElement(element).perform());
    }

    @Override
    public void sendKeys(CharSequence... keys) {
        run(() -> element.sendKeys(keys));
    }

    @Override
    public void clear() {
        run(element::clear);
    }

    @Override
    public List<ElementBackend> findElements(String cssSelector) {
        return call(() -> element.findElements(By.cssSelector(cssSelector))
                .stream()
                .<ElementBackend> map(
                        found -> new SeleniumElementBackend(backend, found))
                .toList());
    }

    @Override
    public String getTagName() {
        return call(element::getTagName);
    }

    @Override
    public @Nullable String getAttribute(String name) {
        return call(() -> element.getAttribute(name));
    }

    @Override
    public @Nullable String getDomAttribute(String name) {
        return call(() -> element.getDomAttribute(name));
    }

    @Override
    public @Nullable String getDomProperty(String name) {
        return call(() -> element.getDomProperty(name));
    }

    @Override
    public String getText() {
        return call(element::getText);
    }

    @Override
    public String getCssValue(String propertyName) {
        return call(() -> element.getCssValue(propertyName));
    }

    @Override
    public boolean isDisplayed() {
        return call(element::isDisplayed);
    }

    @Override
    public boolean isEnabled() {
        return call(element::isEnabled);
    }

    @Override
    public boolean isSelected() {
        return call(element::isSelected);
    }

    @Override
    public Rect getRect() {
        Rectangle rect = call(element::getRect);
        return new Rect(rect.getX(), rect.getY(), rect.getWidth(),
                rect.getHeight());
    }

    @Override
    public byte[] getScreenshot() {
        return call(() -> element.getScreenshotAs(OutputType.BYTES));
    }

    private static <T> T call(Supplier<T> seleniumCall) {
        try {
            return seleniumCall.get();
        } catch (WebDriverException e) {
            throw SeleniumBackend.translate(e);
        }
    }

    private static void run(Runnable seleniumCall) {
        call(() -> {
            seleniumCall.run();
            return null;
        });
    }

    /**
     * Also equal to the {@link WebElement} itself, or anything wrapping it,
     * since {@link WebElement#equals} is.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SeleniumElementBackend other) {
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
