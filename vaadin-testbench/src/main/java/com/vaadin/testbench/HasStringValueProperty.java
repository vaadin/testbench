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
