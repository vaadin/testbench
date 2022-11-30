/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ViewPackages
class AnchorTesterTest extends UIUnitTest {
    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(AnchorView.class);
    }

    @Test
    void anchorClick_navigatesCorrectly() {
        Anchor anchor = new Anchor("anchor", "Home");
        UI.getCurrent().add(anchor);

        Assertions.assertEquals("anchor", test(anchor).getHref());
        Assertions.assertTrue(test(anchor).click() instanceof AnchorView,
                "Click anchor did not navigate to AnchorView");
    }

    @Test
    void anchorClick_notARegisteredRoute_throws() {
        Anchor anchor = new Anchor("error", "Home");
        UI.getCurrent().add(anchor);

        final IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> test(anchor).click());

        Assertions.assertEquals("Anchor is not for an application route",
                exception.getMessage());
    }

    @Test
    void anchorClick_streamRegistration_throws() {
        StreamResource resource = new StreamResource("filename",
                () -> new ByteArrayInputStream(
                        "Hello world".getBytes(StandardCharsets.UTF_8)));
        Anchor anchor = new Anchor(resource, "Home");
        UI.getCurrent().add(anchor);

        final IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> test(anchor).click());

        Assertions.assertEquals("Anchor target seems to be a resource",
                exception.getMessage());
    }

}
