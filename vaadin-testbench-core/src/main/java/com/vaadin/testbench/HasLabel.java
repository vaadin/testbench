/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
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
