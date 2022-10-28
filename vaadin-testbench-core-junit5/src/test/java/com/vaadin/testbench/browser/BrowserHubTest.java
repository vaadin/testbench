/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.browser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.testbench.annotations.RunOnHub;

@RunOnHub("hub-in-annotation")
public class BrowserHubTest extends BrowserExtension {

    private static final String HUB_HOSTNAME_PROPERTY = "com.vaadin.testbench.Parameters.hubHostname";

    public BrowserHubTest() {
        super(null);
    }

    @Test
    public void hubFromAnnotationOrSystemProperty() {
        String oldProperty = System.getProperty(HUB_HOSTNAME_PROPERTY);
        try {
            System.clearProperty(HUB_HOSTNAME_PROPERTY);

            Assertions.assertEquals("hub-in-annotation",
                    getHubHostname(getClass()));
            System.setProperty(HUB_HOSTNAME_PROPERTY, "hub-system-property");
            Assertions.assertEquals("hub-system-property",
                    getHubHostname(getClass()));
            System.clearProperty(HUB_HOSTNAME_PROPERTY);
            Assertions.assertEquals("hub-in-annotation",
                    getHubHostname(getClass()));
        } finally {
            if (oldProperty != null) {
                System.setProperty(HUB_HOSTNAME_PROPERTY, oldProperty);
            }
        }
    }

}
