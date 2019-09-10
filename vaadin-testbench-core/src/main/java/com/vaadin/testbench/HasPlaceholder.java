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
