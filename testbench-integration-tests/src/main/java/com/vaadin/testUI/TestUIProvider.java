package com.vaadin.testUI;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 *
 */
@SuppressWarnings("serial")
public class TestUIProvider extends UIProvider {

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        String name = (event.getRequest()).getPathInfo();
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        try {
            String className = "com.vaadin.testUI." + name;
            return Class.forName(className).asSubclass(UI.class);
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

}