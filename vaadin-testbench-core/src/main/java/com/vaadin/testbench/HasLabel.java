/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

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
    default public String getLabel() {
        String ret = getPropertyString("label");
        if (ret == null) {
            return "";
        } else {
            return ret;
        }
    }
}
