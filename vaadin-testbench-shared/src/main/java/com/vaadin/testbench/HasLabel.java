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
 * Implement by elements which support a label, i.e. text shown typically inside
 * (when field is empty) or above the field (when the field has a value).
 */
public interface HasLabel extends HasPropertySettersGetters {

    /**
     * Gets the label for the element.
     *
     * @return the label or an empty string if there is no label
     */
    default String getLabel() {
        return Objects.requireNonNullElse(getPropertyString("label"), "");
    }
}
