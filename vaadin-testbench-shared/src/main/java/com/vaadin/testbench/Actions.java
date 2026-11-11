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
 *
 * @deprecated use {@link BrowserDriver#getKeyboard()} for keys, and the
 *             element's own {@code click()}, {@code click(x, y, Keys...)},
 *             {@code doubleClick()}, {@code contextClick()}, {@code hover()},
 *             {@code dragTo(…)} and {@code dragBy(…)}
 */
@Deprecated
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
     * @deprecated use {@code getDriver().getKeyboard().keyDown(key)}
     * @throws IllegalStateException
     *             if {@code key} is more than one key
     */
    @Deprecated
    public Actions keyDown(CharSequence key) {
        steps.add(new Input.KeyDown(single(key)));
        return this;
    }

    /**
     * Clicks the element, then presses a key and holds it.
     *
     * @deprecated use {@code element.click()}, then
     *             {@code getDriver().getKeyboard().keyDown(key)}
     */
    @Deprecated
    public Actions keyDown(TestBenchElement target, CharSequence key) {
        return click(target).keyDown(key);
    }

    /** @deprecated use {@code getDriver().getKeyboard().keyUp(key)} */
    @Deprecated
    public Actions keyUp(CharSequence key) {
        steps.add(new Input.KeyUp(single(key)));
        return this;
    }

    /**
     * @deprecated use {@code element.click()}, then
     *             {@code getDriver().getKeyboard().keyUp(key)}
     */
    @Deprecated
    public Actions keyUp(TestBenchElement target, CharSequence key) {
        return click(target).keyUp(key);
    }

    /**
     * Presses and releases each key in turn, into whatever has the focus.
     *
     * @deprecated use {@code getDriver().getKeyboard().sendKeys(keys)}, whose
     *             modifiers stay down to the end of the call
     */
    @Deprecated
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
     *
     * @deprecated use {@code element.sendKeys(keys)}
     */
    @Deprecated
    public Actions sendKeys(TestBenchElement target, CharSequence... keys) {
        return click(target).sendKeys(keys);
    }

    /** @deprecated use {@code element.dragTo(…)} or {@code dragBy(…)} */
    @Deprecated
    public Actions clickAndHold(TestBenchElement target) {
        return moveToElement(target).clickAndHold();
    }

    /** @deprecated use {@code element.dragTo(…)} or {@code dragBy(…)} */
    @Deprecated
    public Actions clickAndHold() {
        steps.add(new Input.MouseDown(LEFT));
        return this;
    }

    /** @deprecated use {@code element.dragTo(…)} or {@code dragBy(…)} */
    @Deprecated
    public Actions release(TestBenchElement target) {
        return moveToElement(target).release();
    }

    /** @deprecated use {@code element.dragTo(…)} or {@code dragBy(…)} */
    @Deprecated
    public Actions release() {
        steps.add(new Input.MouseUp(LEFT));
        return this;
    }

    /**
     * A native click in the element's centre.
     *
     * @deprecated use {@code element.click()}
     */
    @Deprecated
    public Actions click(TestBenchElement target) {
        return moveToElement(target).click();
    }

    /** @deprecated use {@code element.click(x, y)} */
    @Deprecated
    public Actions click() {
        return clickWith(LEFT);
    }

    /** @deprecated use {@code element.doubleClick()} */
    @Deprecated
    public Actions doubleClick(TestBenchElement target) {
        return moveToElement(target).doubleClick();
    }

    /** @deprecated use {@code element.doubleClick()} */
    @Deprecated
    public Actions doubleClick() {
        return clickWith(LEFT).clickWith(LEFT);
    }

    /** @deprecated use {@code element.contextClick()} */
    @Deprecated
    public Actions contextClick(TestBenchElement target) {
        return moveToElement(target).contextClick();
    }

    /** @deprecated use {@code element.contextClick()} */
    @Deprecated
    public Actions contextClick() {
        return clickWith(Input.Button.RIGHT);
    }

    /** @deprecated use {@code element.hover()} */
    @Deprecated
    public Actions moveToElement(TestBenchElement target) {
        return moveToElement(target, 0, 0);
    }

    /**
     * Moves the mouse to an offset from the element's centre.
     *
     * @deprecated use {@code element.click(x, y)} to click there
     */
    @Deprecated
    public Actions moveToElement(TestBenchElement target, int xOffset,
            int yOffset) {
        steps.add(new Input.MoveToElement(target.getElementBackend(), xOffset,
                yOffset));
        return this;
    }

    /** @deprecated use {@code element.dragBy(x, y)} */
    @Deprecated
    public Actions moveByOffset(int xOffset, int yOffset) {
        steps.add(new Input.MoveBy(xOffset, yOffset));
        return this;
    }

    /** @deprecated use {@code element.click(x, y)} */
    @Deprecated
    public Actions moveToLocation(int x, int y) {
        steps.add(new Input.MoveTo(x, y));
        return this;
    }

    /** @deprecated use {@code source.dragTo(target)} */
    @Deprecated
    public Actions dragAndDrop(TestBenchElement source,
            TestBenchElement target) {
        return clickAndHold(source).release(target);
    }

    /** @deprecated use {@code source.dragBy(x, y)} */
    @Deprecated
    public Actions dragAndDropBy(TestBenchElement source, int xOffset,
            int yOffset) {
        return clickAndHold(source).moveByOffset(xOffset, yOffset).release();
    }

    /** @deprecated use {@code Thread.sleep}, or better, a wait */
    @Deprecated
    public Actions pause(long millis) {
        return pause(Duration.ofMillis(millis));
    }

    /** @deprecated use {@code Thread.sleep}, or better, a wait */
    @Deprecated
    public Actions pause(Duration duration) {
        steps.add(new Input.Pause(duration));
        return this;
    }

    /**
     * Turns the wheel with the mouse at the viewport's top left corner.
     *
     * @deprecated use {@code element.scroll(…)} or {@code scrollIntoView()}
     */
    @Deprecated
    public Actions scrollByAmount(int deltaX, int deltaY) {
        steps.add(new Input.Wheel(0, 0, deltaX, deltaY));
        return this;
    }

    /**
     * Takes out the steps so far; the builder starts empty again.
     *
     * @deprecated use {@link #perform()}, or the replacements of the methods
     */
    @Deprecated
    public Action build() {
        List<Input> built = List.copyOf(steps);
        steps = new ArrayList<>();
        return () -> {
            driver.getCommandExecutor().waitForVaadin();
            driver.getCommandExecutor().getBackend().perform(built);
        };
    }

    /** @deprecated use the replacements of the methods */
    @Deprecated
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
