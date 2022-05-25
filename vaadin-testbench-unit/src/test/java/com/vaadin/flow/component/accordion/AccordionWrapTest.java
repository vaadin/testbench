/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.accordion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;

class AccordionWrapTest extends UIUnitTest {

    AccordionView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(AccordionView.class);
        view = navigate(AccordionView.class);
    }

    @Test
    void getPanelBySummary_returnsCorrectPanel() {
        final AccordionWrap<Accordion> wrap = wrap(view.accordion);
        wrap.openDetails("Red");
        Assertions.assertSame(view.redPanel, wrap.getPanel("Red"));
        wrap.openDetails("Disabled");
        Assertions.assertSame(view.disabledPanel, wrap.getPanel("Disabled"));
    }

    @Test
    void closedPanel_getPanelThrows() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> ((AccordionWrap<Accordion>) wrap(view.accordion))
                        .getPanel("Green"));
    }

    @Test
    void isOpen_seesCorrectPanel() {
        view.accordion.open(view.redPanel);

        final AccordionWrap<Accordion> wrap = wrap(view.accordion);
        Assertions.assertTrue(wrap.isOpen("Red"), "Red should be open");
        Assertions.assertFalse(wrap.isOpen("Green"), "Only red should be open");

        view.accordion.open(view.greenPanel);

        Assertions.assertFalse(wrap.isOpen("Red"),
                "Red should close after green is open");
    }

    @Test
    void hasPanel_returnsTrueForExistingPanel() {
        final AccordionWrap<Accordion> wrap = wrap(view.accordion);
        Assertions.assertTrue(wrap.hasPanel("Green"),
                "Green panel should exist");
        Assertions.assertFalse(wrap.hasPanel("Orange"),
                "No Orange panel is added");
    }
}
