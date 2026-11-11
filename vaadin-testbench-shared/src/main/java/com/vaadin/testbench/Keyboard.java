/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.spi.Input;

/**
 * The page's keyboard: it types into whatever has the focus, where
 * {@link TestBenchElement#sendKeys(CharSequence...)} first moves the focus to
 * its element.
 *
 * <pre>{@code
 * Keyboard keyboard = getDriver().getKeyboard();
 * // types into the focused field, then tabs on
 * keyboard.sendKeys("Jo", Keys.TAB);
 * // Shift is held across calls, so the grid sees it in the selection
 * keyboard.keyDown(Keys.SHIFT);
 * grid.select(0);
 * keyboard.keyUp(Keys.SHIFT);
 * }</pre>
 *
 * Each call waits for Vaadin first.
 */
public final class Keyboard {

    private static final Set<Integer> MODIFIERS = Set.of(
            (int) Keys.SHIFT.charAt(0), (int) Keys.CONTROL.charAt(0),
            (int) Keys.ALT.charAt(0), (int) Keys.META.charAt(0));

    private final TestBenchCommandExecutor commandExecutor;

    Keyboard(TestBenchCommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    /**
     * Types the keys, as {@link TestBenchElement#sendKeys(CharSequence...)}
     * does: a modifier stays down until pressed again, until {@link Keys#NULL}
     * or until the call ends, so {@code sendKeys(Keys.SHIFT,
     * "ab")} types {@code AB}.
     */
    public void sendKeys(CharSequence... keys) {
        perform(typing(keys));
    }

    /**
     * Presses a key and holds it, across calls, until {@link #keyUp(Keys)}.
     */
    public void keyDown(Keys key) {
        perform(List.of(new Input.KeyDown(key.charAt(0))));
    }

    public void keyUp(Keys key) {
        perform(List.of(new Input.KeyUp(key.charAt(0))));
    }

    private void perform(List<Input> steps) {
        commandExecutor.waitForVaadin();
        commandExecutor.getBackend().perform(steps);
    }

    // WebDriver's element send keys: modifiers toggle and are let go at the
    // end, every other key is pressed and released
    static List<Input> typing(CharSequence... keys) {
        List<Input> steps = new ArrayList<>();
        List<Integer> held = new ArrayList<>();
        for (CharSequence sequence : keys) {
            sequence.codePoints().forEach(codePoint -> {
                if (codePoint == Keys.NULL.charAt(0)) {
                    release(held, steps);
                } else if (MODIFIERS.contains(codePoint)) {
                    if (held.remove((Integer) codePoint)) {
                        steps.add(new Input.KeyUp(codePoint));
                    } else {
                        held.add(codePoint);
                        steps.add(new Input.KeyDown(codePoint));
                    }
                } else {
                    steps.add(new Input.KeyDown(codePoint));
                    steps.add(new Input.KeyUp(codePoint));
                }
            });
        }
        release(held, steps);
        return steps;
    }

    private static void release(List<Integer> held, List<Input> steps) {
        for (int i = held.size() - 1; i >= 0; i--) {
            steps.add(new Input.KeyUp(held.get(i)));
        }
        held.clear();
    }
}
