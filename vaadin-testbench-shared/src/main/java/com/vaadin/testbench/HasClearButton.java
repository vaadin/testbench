/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

/**
 * The {@code HasClearButton} interface provides methods to interact with an
 * element that may have a clear button. It includes functionality to check the
 * visibility of the clear button and to click it if it is visible.
 */
public interface HasClearButton extends HasElementQuery, HasCallFunction {

    /**
     * Checks if the current element has a clear button.
     *
     * @return {@code true} if the element has a clear button, {@code false}
     *         otherwise.
     */
    default boolean isClearButtonVisible() {
        return (boolean) callFunction("hasAttribute", "clear-button-visible");
    }

    /**
     * Clicks the clear button if it is visible.
     * <p>
     * This method checks if the clear button is visible before attempting to
     * click it. If the clear button is not visible, an
     * {@link IllegalStateException} is thrown.
     * </p>
     *
     * @throws IllegalStateException
     *             if the clear button is not visible
     */
    default void clickClearButton() {
        if (!isClearButtonVisible()) {
            throw new IllegalStateException("Clear button is not visible");
        }
        this.$(TestBenchElement.class).id("clearButton").click();
    }
}
