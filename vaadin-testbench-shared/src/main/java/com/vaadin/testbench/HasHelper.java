/**
 * Copyright (C) 2000-2022 Vaadin Ltd
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
public interface HasHelper extends HasPropertySettersGetters, HasElementQuery {

    /**
     * Gets the helper text for the element.
     *
     * @return the label or an empty string if there is no helper text
     */
    default String getHelperText() {
        String ret = getPropertyString("helperText");
        return Objects.requireNonNullElse(ret, "");
    }

    /**
     * Gets the slotted helper component for the element.
     *
     * @return the slotted component or {@code null} if there is no component
     */
    default TestBenchElement getHelperComponent() {
        final ElementQuery<TestBenchElement> query = $(TestBenchElement.class)
                .withAttribute("slot", "helper");
        if (query.exists()) {
            TestBenchElement last = query.last();
            // To avoid getting the "slot" element, for components with slotted
            // slots
            if (!"slot".equals(last.getTagName())) {
                return last;
            }
        }
        return null;
    }
}
