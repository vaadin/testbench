/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class TestUIProvider extends UIProvider {
    private static Logger logger = Logger.getLogger(TestUIProvider.class
            .getName());

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
            logger.log(Level.SEVERE, "Could not find UI " + name, e);
        }
        return null;
    }

}