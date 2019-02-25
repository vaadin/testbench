package com.vaadin.testbench;

public interface HasStringValueProperty extends HasPropertySettersGetters {

    default String getValue() {
        return getPropertyString("value");
    }

    default void setValue(String string) {
        setProperty("value", string);
    }

    @Override
    default void clear() {
        setValue("");
    }

}
