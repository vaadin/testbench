/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3.0>.
 */
package com.vaadin.testbench.commands;

import org.openqa.selenium.Keys;

public interface TestBenchElementCommands {

    /**
     * Set focus to this element.
     */
    void focus();

    /**
     * Moved to NotificationElement. API will be removed in the future.
     */
    @Deprecated
    boolean closeNotification();

    /**
     * Shows the tool tip of the specified element.
     */
    void showTooltip();

    /**
     * Scrolls the element down to the specified top value.
     * 
     * @param scrollTop
     *            the new value for scrollTop.
     */
    void scroll(int scrollTop);

    /**
     * Scrolls the element left to the specified left value.
     * 
     * @param scrollLeft
     *            the new value for scrollLeft.
     */
    void scrollLeft(int scrollLeft);

    /**
     * Clicks at the specified coordinates on an element while pressing possible
     * modifier keys. The coordinates are relative to top left on the element.
     * 
     * @param x
     *            the offset from the left position of the element
     * @param y
     *            the offset from the top position of the element
     * @param modifiers
     *            any modifier keys to press while clicking the element
     *            (optional).
     */
    void click(int x, int y, Keys... modifiers);
}
