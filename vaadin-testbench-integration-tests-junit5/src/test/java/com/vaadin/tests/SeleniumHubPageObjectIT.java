/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PageObjectView;

@EnabledIf("isConfiguredForSauceLabs")
public class SeleniumHubPageObjectIT extends AbstractSeleniumSauceTB9Test {
    static {
        System.err.println("Logging for SeleniumHubPageObjectIT started");
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.FINE);
        Map<Handler, Level> oldLevels = new HashMap<>();
        for (Handler h : logger.getHandlers()) {
            oldLevels.put(h, h.getLevel());
            h.setLevel(Level.FINE);
        }

    }

    @Override
    protected Class<? extends Component> getTestView() {
        return PageObjectView.class;
    }

    @Test
    public void findUsingValueAnnotation() {
        try {

            openTestURL();
            List<MyComponentWithIdElement> components = $(
                    MyComponentWithIdElement.class).all();

            Assertions.assertEquals(1, components.size());
            Assertions.assertEquals("MyComponentWithId",
                    components.get(0).getText());
        } finally {
            // for (Handler h : logger.getHandlers()) {
            // h.setLevel(oldLevels.get(h));
            // }
        }
    }

    @Test
    public void findUsingContainsAnnotation() {
        openTestURL();
        List<MyComponentWithClassesElement> components = $(
                MyComponentWithClassesElement.class).all();

        Assertions.assertEquals(1, components.size());
        Assertions.assertEquals("MyComponentWithClasses",
                components.get(0).getText());
    }

}
