/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

public interface HasStringValueProperty extends HasPropertySettersGetters {

    default public String getValue() {
        return getPropertyString("value");
    }

    default public void setValue(String string) {
        setProperty("value", string);
    }

    @Override
    default public void clear() {
        setValue("");
    }

}
