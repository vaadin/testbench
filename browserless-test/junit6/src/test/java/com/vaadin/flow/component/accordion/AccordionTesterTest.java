/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
