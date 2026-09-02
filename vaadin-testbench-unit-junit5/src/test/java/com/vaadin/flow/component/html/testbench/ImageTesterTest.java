/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class ImageTesterTest extends UIUnitTest {

    ImageView view;
    ImageTester tester;
    Image component;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ImageView.class);
        view = navigate(ImageView.class);
        component = view.image;
        tester = test(component);
    }

    @Test
    void click_listenerNotified() {
        tester.click();
        Assertions.assertEquals(1, view.clicks);
    }

    @Test
    void click_unusable_throws() {
        component.setVisible(false);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tester.click());
    }

    @Test
    void getTitle_returnsTitle() {
        Assertions.assertEquals("Vaadin logo", tester.getTitle());
    }

}
