/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.playwright;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Keyboard;
import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.ViewportSize;

import com.vaadin.testbench.spi.Input;

/**
 * WebDriver's input state on Playwright's keyboard and mouse: where the mouse
 * is, which keys are down, and the click count, which Playwright leaves to the
 * caller. Kept across {@link #perform} calls, as a WebDriver session keeps it.
 */
final class PageInput {

    // chromedriver's: a press at the same point within this counts as the
    // next click of a double click
    private static final long MULTI_CLICK_MILLIS = 500;

    private double x;
    private double y;
    // The key each held code point was pressed as
    private final Map<Integer, String> pressed = new HashMap<>();
    private final List<String> heldModifiers = new ArrayList<>();
    // Where the last click was released; NaN once the mouse has moved since
    private Input.Button lastButton;
    private double lastClickX = Double.NaN;
    private double lastClickY = Double.NaN;
    private long lastClickTime;
    private int clickCount;

    void perform(Page page, List<Input> steps) {
        for (Input step : steps) {
            switch (step) {
            case Input.KeyDown down ->
                keyDown(page.keyboard(), down.codePoint());
            case Input.KeyUp up -> keyUp(page.keyboard(), up.codePoint());
            case Input.MoveToElement move -> {
                double[] centre = inViewCentre(page,
                        ((PlaywrightElementBackend) move.element())
                                .getElementHandle());
                moveTo(page, centre[0] + move.xOffset(),
                        centre[1] + move.yOffset(), 100);
            }
            case Input.MoveBy move ->
                moveTo(page, x + move.xOffset(), y + move.yOffset(), 200);
            case Input.MoveTo move -> moveTo(page, move.x(), move.y(), 250);
            case Input.MouseDown down -> mouseDown(page, down.button());
            case Input.MouseUp up -> mouseUp(page, up.button());
            case Input.Wheel wheel -> {
                moveTo(page, wheel.x(), wheel.y(), 0);
                page.mouse().wheel(wheel.deltaX(), wheel.deltaY());
            }
            case Input.Pause pause -> sleep(pause.duration().toMillis());
            }
        }
    }

    private void keyDown(Keyboard keyboard, int codePoint) {
        if (codePoint == KeyInput.NULL) {
            // chromedriver sends an "Unidentified" key, which Playwright
            // cannot; it lets go of nothing, unlike an element's sendKeys
            return;
        }
        String modifier = codePoint < 0x10000
                ? KeyInput.MODIFIERS.get((char) codePoint)
                : null;
        String key;
        if (modifier != null) {
            key = modifier;
            heldModifiers.add(modifier);
        } else if (codePoint >= 0xE000 && codePoint <= 0xE05D) {
            key = KeyInput.keyName((char) codePoint);
        } else if (!heldModifiers.isEmpty() && codePoint < 0x80) {
            // Under a modifier a character is its key, as KeyInput presses it
            key = KeyInput.keyOf((char) codePoint);
        } else if (codePoint >= 0x20 && codePoint < 0x7F) {
            key = new String(Character.toChars(codePoint));
        } else {
            // No key on Playwright's US layout: the character is inserted
            keyboard.insertText(new String(Character.toChars(codePoint)));
            return;
        }
        keyboard.down(key);
        pressed.put(codePoint, key);
    }

    private void keyUp(Keyboard keyboard, int codePoint) {
        String key = pressed.remove(codePoint);
        if (key == null) {
            return;
        }
        heldModifiers.remove(key);
        keyboard.up(key);
    }

    // As chromedriver counts: a press where the last click was released,
    // soon after and with no move between, is that click's next
    private void mouseDown(Page page, Input.Button button) {
        boolean again = button == lastButton && x == lastClickX
                && y == lastClickY && System.currentTimeMillis()
                        - lastClickTime < MULTI_CLICK_MILLIS;
        clickCount = again ? clickCount + 1 : 1;
        lastButton = button;
        page.mouse().down(new Mouse.DownOptions().setButton(button(button))
                .setClickCount(clickCount));
    }

    private void mouseUp(Page page, Input.Button button) {
        page.mouse().up(new Mouse.UpOptions().setButton(button(button))
                .setClickCount(clickCount));
        lastClickX = x;
        lastClickY = y;
        lastClickTime = System.currentTimeMillis();
    }

    // Playwright moves in one step unless told otherwise; WebDriver moves
    // over the action's duration, with mouse moves on the way
    private void moveTo(Page page, double toX, double toY, int millis) {
        page.mouse().move(toX, toY,
                new Mouse.MoveOptions().setSteps(Math.max(1, millis / 20)));
        if (toX != x || toY != y) {
            lastClickX = Double.NaN;
        }
        x = toX;
        y = toY;
    }

    // WebDriver's in-view centre point: the centre of the part of the box in
    // the viewport
    private static double[] inViewCentre(Page page, ElementHandle element) {
        element.scrollIntoViewIfNeeded();
        BoundingBox box = element.boundingBox();
        if (box == null) {
            throw new IllegalStateException(
                    "The element has no box to move the mouse to: it is not displayed");
        }
        ViewportSize viewport = page.viewportSize();
        double left = Math.max(box.x, 0);
        double top = Math.max(box.y, 0);
        double right = viewport == null ? box.x + box.width
                : Math.min(box.x + box.width, viewport.width);
        double bottom = viewport == null ? box.y + box.height
                : Math.min(box.y + box.height, viewport.height);
        return new double[] { Math.floor((left + right) / 2),
                Math.floor((top + bottom) / 2) };
    }

    private static MouseButton button(Input.Button button) {
        return switch (button) {
        case LEFT -> MouseButton.LEFT;
        case MIDDLE -> MouseButton.MIDDLE;
        case RIGHT -> MouseButton.RIGHT;
        };
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted in a pause", e);
        }
    }
}
