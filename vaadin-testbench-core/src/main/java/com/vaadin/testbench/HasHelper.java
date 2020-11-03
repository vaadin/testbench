/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;
/**
 * Implement by elements which support a label, i.e. text shown typically inside
 * (when field is empty) or above the field (when the field has a value).
 */
public interface HasHelper extends HasPropertySettersGetters, HasElementQuery {

    /**
     * Gets the helper text for the element.
     *
     * @return the label or an empty string if there is no helper text
     */
    default public String getHelperText() {
        String ret = getPropertyString("helperText");
        if (ret == null) {
            return "";
        } else {
            return ret;
        }
    }

    /**
     * Gets the slotted helper component for the element.
     * 
     * @return the slotted component or {@code null} if there is no component
     */
    default public TestBenchElement getHelperComponent() {
        final ElementQuery<TestBenchElement> query = $(TestBenchElement.class).attribute("slot", "helper");
        if (query.exists()) {
            TestBenchElement last = query.last();
            // To avoid getting the "slot" element, for components with slotted slots
            if (!"slot".equals(last.getTagName())) {
                return last;
            }
        }
        return null;
    }
}
