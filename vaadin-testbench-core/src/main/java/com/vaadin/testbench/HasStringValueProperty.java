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
