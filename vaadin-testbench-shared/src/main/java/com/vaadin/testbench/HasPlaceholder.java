/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

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
    default public String getPlaceholder() {
        String ret = getPropertyString("placeholder");
        if (ret == null) {
            return "";
        } else {
            return ret;
        }
    }
}
