/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.Objects;

/**
 * Implement by elements which support a placeholder, i.e. text shown when the
 * field is empty.
 */
public interface HasPlaceholder extends HasPropertySettersGetters {

    /**
     * Gets the placeholder for the element.
     *
     * @return the placeholder or an empty string if there is no placeholder
     */
    default String getPlaceholder() {
        return Objects.requireNonNullElse(getPropertyString("placeholder"), "");
    }
}
