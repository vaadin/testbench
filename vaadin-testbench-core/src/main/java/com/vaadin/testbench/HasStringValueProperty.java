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
