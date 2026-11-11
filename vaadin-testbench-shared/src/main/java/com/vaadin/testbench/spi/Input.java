/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.spi;

import java.time.Duration;

/**
 * One step of the page's keyboard or mouse, for
 * {@link BrowserBackend#perform(java.util.List)}. A Shift-drag:
 *
 * <pre>{@code
 * backend.perform(List.of(new Input.KeyDown(0xE008),
 *         new Input.MoveToElement(handle, 0, 0),
 *         new Input.MouseDown(Input.Button.LEFT), new Input.MoveBy(40, 0),
 *         new Input.MouseUp(Input.Button.LEFT), new Input.KeyUp(0xE008)));
 * }</pre>
 *
 * Each step is the WebDriver action of the same name, as Selenium's
 * {@code Actions} sends it. A key is a code point: a character, or one of
 * U+E000 to U+E05D for a special key, as {@code Keys} holds them. A key or
 * button left down stays down after the call, until a later step releases it.
 */
public sealed interface Input {

    record KeyDown(int codePoint) implements Input {
    }

    record KeyUp(int codePoint) implements Input {
    }

    /**
     * Moves the mouse to an offset from the element's in-view centre, scrolling
     * it into view first.
     */
    record MoveToElement(ElementBackend element, int xOffset,
            int yOffset) implements Input {
    }

    /** Moves the mouse by an offset from where it is. */
    record MoveBy(int xOffset, int yOffset) implements Input {
    }

    /** Moves the mouse to a point of the viewport. */
    record MoveTo(int x, int y) implements Input {
    }

    record MouseDown(Button button) implements Input {
    }

    record MouseUp(Button button) implements Input {
    }

    /** Turns the wheel with the mouse at a point of the viewport. */
    record Wheel(int x, int y, int deltaX, int deltaY) implements Input {
    }

    record Pause(Duration duration) implements Input {
    }

    enum Button {
        LEFT, MIDDLE, RIGHT
    }
}
