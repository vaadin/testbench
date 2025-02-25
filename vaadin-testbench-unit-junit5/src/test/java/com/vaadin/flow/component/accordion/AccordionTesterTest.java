/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.accordion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class AccordionTesterTest extends UIUnitTest {

    AccordionView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(AccordionView.class);
        view = navigate(AccordionView.class);
    }

    @Test
    void getPanelBySummary_returnsCorrectPanel() {
        final AccordionTester<Accordion> wrap = test(view.accordion);
        wrap.openDetails("Red");
        Assertions.assertSame(view.redPanel, wrap.getPanel("Red"));
        wrap.openDetails("Disabled");
        Assertions.assertSame(view.disabledPanel, wrap.getPanel("Disabled"));
    }

    @Test
    void closedPanel_getPanelThrows() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.accordion).getPanel("Green"));
    }

    @Test
    void isOpen_seesCorrectPanel() {
        view.accordion.open(view.redPanel);

        final AccordionTester<Accordion> wrap = test(view.accordion);
        Assertions.assertTrue(wrap.isOpen("Red"), "Red should be open");
        Assertions.assertFalse(wrap.isOpen("Green"), "Only red should be open");

        view.accordion.open(view.greenPanel);

        Assertions.assertFalse(wrap.isOpen("Red"),
                "Red should close after green is open");
    }

    @Test
    void hasPanel_returnsTrueForExistingPanel() {
        final AccordionTester<Accordion> wrap = test(view.accordion);
        Assertions.assertTrue(wrap.hasPanel("Green"),
                "Green panel should exist");
        Assertions.assertFalse(wrap.hasPanel("Orange"),
                "No Orange panel is added");
    }
}
