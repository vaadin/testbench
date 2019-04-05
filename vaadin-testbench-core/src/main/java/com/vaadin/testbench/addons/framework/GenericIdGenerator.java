package com.vaadin.testbench.addons.framework;

import java.util.Locale;

public interface GenericIdGenerator {

    static String genericId(Class uiClass, Class componentClass, String label) {
        return (uiClass.getSimpleName()
                + "-" + componentClass.getSimpleName()
                + "-" + label.replace(" ", "-"))
                .toLowerCase(Locale.US);
    }
}
