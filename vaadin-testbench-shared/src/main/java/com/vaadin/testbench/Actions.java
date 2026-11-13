/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.testbench.spi.Input;

/**
 * Selenium's {@code Actions} on TestBench's types, so that a test using it
 * ports by changing the import:
 *
 * <pre>{@code
 * new Actions(getDriver()).keyDown(Keys.SHIFT).click(row).keyUp(Keys.SHIFT)
 *         .perform();
 * }</pre>
 *
 * Each method sends what Selenium's does, in the same order, quirks included:
 * {@link #sendKeys(CharSequence...)} presses and releases every key, modifiers
 * too, unlike an element's {@code sendKeys}. Nothing is sent before
 * {@link #perform()}, which waits for Vaadin first.
 */
public class Actions {

    private static final Input.Button LEFT = Input.Button.LEFT;

    /** The steps {@link #build()} has taken out, ready to perform. */
    public interface Action {
        void perform();
    }

    private final BrowserDriver driver;
    private List<Input> steps = new ArrayList<>();

    public Actions(BrowserDriver driver) {
        this.driver = driver;
    }

    /**
     * Presses a key and holds it, across {@code perform()}s, until
     * {@link #keyUp(CharSequence)} or {@code sendKeys(Keys.NULL)}.
     *
     * @throws IllegalStateException
     *             if {@code key} is more than one key
     */
    public Actions keyDown(CharSequence key) {
        steps.add(new Input.KeyDown(single(key)));
        return this;
    }

    /**
     * Clicks the element, then presses a key and holds it.
     */
    public Actions keyDown(TestBenchElement target, CharSequence key) {
        return click(target).keyDown(key);
    }

    public Actions keyUp(CharSequence key) {
        steps.add(new Input.KeyUp(single(key)));
        return this;
    }

    public Actions keyUp(TestBenchElement target, CharSequence key) {
        return click(target).keyUp(key);
    }

    /**
     * Presses and releases each key in turn, into whatever has the focus.
     */
    public Actions sendKeys(CharSequence... keys) {
        for (CharSequence key : keys) {
            key.codePoints().forEach(codePoint -> {
                steps.add(new Input.KeyDown(codePoint));
                steps.add(new Input.KeyUp(codePoint));
            });
        }
        return this;
    }

    /**
     * Clicks the element, then presses and releases each key in turn.
     */
    public Actions sendKeys(TestBenchElement target, CharSequence... keys) {
        return click(target).sendKeys(keys);
    }

    public Actions clickAndHold(TestBenchElement target) {
        return moveToElement(target).clickAndHold();
    }

    public Actions clickAndHold() {
        steps.add(new Input.MouseDown(LEFT));
        return this;
    }

    public Actions release(TestBenchElement target) {
        return moveToElement(target).release();
    }

    public Actions release() {
        steps.add(new Input.MouseUp(LEFT));
        return this;
    }

    /**
     * A native click in the element's centre.
     */
    public Actions click(TestBenchElement target) {
        return moveToElement(target).click();
    }

    public Actions click() {
        return clickWith(LEFT);
    }

    public Actions doubleClick(TestBenchElement target) {
        return moveToElement(target).doubleClick();
    }

    public Actions doubleClick() {
        return clickWith(LEFT).clickWith(LEFT);
    }

    public Actions contextClick(TestBenchElement target) {
        return moveToElement(target).contextClick();
    }

    public Actions contextClick() {
        return clickWith(Input.Button.RIGHT);
    }

    public Actions moveToElement(TestBenchElement target) {
        return moveToElement(target, 0, 0);
    }

    /**
     * Moves the mouse to an offset from the element's centre.
     */
    public Actions moveToElement(TestBenchElement target, int xOffset,
            int yOffset) {
        steps.add(new Input.MoveToElement(target.getElementBackend(), xOffset,
                yOffset));
        return this;
    }

    public Actions moveByOffset(int xOffset, int yOffset) {
        steps.add(new Input.MoveBy(xOffset, yOffset));
        return this;
    }

    public Actions moveToLocation(int x, int y) {
        steps.add(new Input.MoveTo(x, y));
        return this;
    }

    public Actions dragAndDrop(TestBenchElement source,
            TestBenchElement target) {
        return clickAndHold(source).release(target);
    }

    public Actions dragAndDropBy(TestBenchElement source, int xOffset,
            int yOffset) {
        return clickAndHold(source).moveByOffset(xOffset, yOffset).release();
    }

    public Actions pause(long millis) {
        return pause(Duration.ofMillis(millis));
    }

    public Actions pause(Duration duration) {
        steps.add(new Input.Pause(duration));
        return this;
    }

    /**
     * Turns the wheel with the mouse at the viewport's top left corner.
     */
    public Actions scrollByAmount(int deltaX, int deltaY) {
        steps.add(new Input.Wheel(0, 0, deltaX, deltaY));
        return this;
    }

    /**
     * Takes out the steps so far; the builder starts empty again.
     */
    public Action build() {
        List<Input> built = List.copyOf(steps);
        steps = new ArrayList<>();
        return () -> {
            driver.getCommandExecutor().waitForVaadin();
            driver.getCommandExecutor().getBackend().perform(built);
        };
    }

    public void perform() {
        build().perform();
    }

    private Actions clickWith(Input.Button button) {
        steps.add(new Input.MouseDown(button));
        steps.add(new Input.MouseUp(button));
        return this;
    }

    private static int single(CharSequence key) {
        if (key.codePoints().count() != 1) {
            throw new IllegalStateException(String.format(
                    "Only one code point is allowed at a time: %s", key));
        }
        return key.codePoints().findFirst().getAsInt();
    }
}
