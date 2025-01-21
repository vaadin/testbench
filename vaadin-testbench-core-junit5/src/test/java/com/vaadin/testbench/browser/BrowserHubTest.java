/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import com.vaadin.testbench.Parameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

@RunOnHub("hub-in-annotation")
public class BrowserHubTest extends BrowserExtension {

    private static final String HUB_HOSTNAME_PROPERTY = "com.vaadin.testbench.Parameters.hubHostname";
    private static final String HUB_PORT_PROPERTY = "com.vaadin.testbench.Parameters.hubPort";

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

    @Test
    public void hubPortFromDefaultValueOrParametersSetter() {
        int oldPortProperty = Parameters.getHubPort();

        try (MockedStatic<SauceLabsIntegration> sauceLabsMock =
                     Mockito.mockStatic(SauceLabsIntegration.class)) {
            sauceLabsMock.when(SauceLabsIntegration::isConfiguredForSauceLabs)
                    .thenReturn(false);
            // Default must be the "official" 4444 port for backwards
            // compatibility
            Assertions.assertEquals(getExpectedHubUrl(4444),
                    getHubURL(getClass()));

            // Modified at runtime
            Parameters.setHubPort(4445);
            Assertions.assertEquals(getExpectedHubUrl(4445),
                    getHubURL(getClass()));
        } finally {
            Parameters.setHubPort(oldPortProperty);
        }
    }

    private String getExpectedHubUrl(int port) {
        return "http://" + getHubHostname(getClass()) + ":" + port + "/wd/hub";
    }

}
