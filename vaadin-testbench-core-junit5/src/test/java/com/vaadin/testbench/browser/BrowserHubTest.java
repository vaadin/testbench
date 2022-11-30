/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
