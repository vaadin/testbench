/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.Objects;

/**
 * Implement by elements which support a error message, required indicator and
 * invalid state.
 */
public interface HasValidation
        extends HasPropertySettersGetters, HasElementQuery, HasCallFunction {

    /**
     * Checks if the current element is marked as invalid.
     *
     * @return {@code true} if the "invalid" attribute exists otherwise
     *         {@code false}
     */
    default boolean isInValid() {
        return (boolean) callFunction("hasAttribute", "invalid");
    }

    /**
     * Checks if the current element has the "required" attribute.
     *
     * @return {@code true} if the element has the "required" attribute,
     *         {@code false} otherwise.
     */
    default boolean isRequired() {
        return (boolean) callFunction("hasAttribute", "required");
    }

    /**
     * Gets the error message for the element.
     *
     * @return the error message or an empty string if there is no message
     */
    default String getErrorMessage() {
        String ret = getPropertyString("errorMessage");
        return Objects.requireNonNullElse(ret, "");
    }

}
