/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.annotations.RunOnHub;

@RunOnHub("hub-in-annotation")
public class ParallelHubTest extends ParallelTest {

    private static final String HUB_HOSTNAME_PROPERTY = "com.vaadin.testbench.Parameters.hubHostname";
    private static final String SAUCE_USER_PROPERTY = "sauce.user";
    private static final String SAUCE_ACCESS_KEY_PROPERTY = "sauce.sauceAccessKey";

    @Override
    public void setup() throws Exception {
        // Do not actually start a session, just test the class methods
    }

    @Test
    public void hubFromAnnotationOrSystemProperty() {
        String oldProperty = System.getProperty(HUB_HOSTNAME_PROPERTY);
        try {
            System.clearProperty(HUB_HOSTNAME_PROPERTY);

            Assert.assertEquals("hub-in-annotation", getHubHostname());
            System.setProperty(HUB_HOSTNAME_PROPERTY, "hub-system-property");
            Assert.assertEquals("hub-system-property", getHubHostname());
            System.clearProperty(HUB_HOSTNAME_PROPERTY);
            Assert.assertEquals("hub-in-annotation", getHubHostname());
        } finally {
            if (oldProperty != null) {
                System.setProperty(HUB_HOSTNAME_PROPERTY, oldProperty);
            }
        }
    }

}
