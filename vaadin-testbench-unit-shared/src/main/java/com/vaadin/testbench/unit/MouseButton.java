/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

/**
 * Enums for mouse button values in click events.
 * <p/>
 * Default button values are as follows:
 * <dl>
 * <dt>-1: No button</dt>
 * <dt>0: The primary button, typically the left mouse button</dt>
 * <dt>1: The middle button,</dt>
 * <dt>2: The secondary button, typically the right mouse button</dt>
 * <dt>3: The first additional button, typically the back button</dt>
 * <dt>4: The second additional button, typically the forward button</dt>
 * <dt>5+: More additional buttons without any typical meanings</dt>
 * </dl>
 */
public enum MouseButton {

    NO_BUTTON(-1), LEFT(0), MIDDLE(1), RIGHT(2), BACK(3), FORWARD(4);

    private int button;

    MouseButton(int button) {
        this.button = button;
    }

    /**
     * Get value associated with the button by default.
     *
     * @return button value
     */
    public int getButton() {
        return button;
    }
}
