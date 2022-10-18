/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.capabilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;

import com.vaadin.testbench.annotations.RunOnHub;

@RunOnHub("hub-in-annotation")
public class CapabilitiesHubTest extends DesiredCapabilitiesExtension {

    private static final String HUB_HOSTNAME_PROPERTY = "com.vaadin.testbench.Parameters.hubHostname";

    public CapabilitiesHubTest() {
        super(null);
    }

    @Test
    public void hubFromAnnotationOrSystemProperty() {
        ExtensionContext context = Mockito.mock(ExtensionContext.class);
        Class clazz = CapabilitiesHubTest.class;
        Mockito.when(context.getRequiredTestClass()).thenReturn(clazz);

        String oldProperty = System.getProperty(HUB_HOSTNAME_PROPERTY);
        try {
            System.clearProperty(HUB_HOSTNAME_PROPERTY);

            Assertions.assertEquals("hub-in-annotation",
                    getHubHostname(context));
            System.setProperty(HUB_HOSTNAME_PROPERTY, "hub-system-property");
            Assertions.assertEquals("hub-system-property",
                    getHubHostname(context));
            System.clearProperty(HUB_HOSTNAME_PROPERTY);
            Assertions.assertEquals("hub-in-annotation",
                    getHubHostname(context));
        } finally {
            if (oldProperty != null) {
                System.setProperty(HUB_HOSTNAME_PROPERTY, oldProperty);
            }
        }
    }

}
