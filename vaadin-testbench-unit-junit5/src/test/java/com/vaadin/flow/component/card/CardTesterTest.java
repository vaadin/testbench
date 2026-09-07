/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.card;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class CardTesterTest extends UIUnitTest {

    CardView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(CardView.class);
        view = navigate(CardView.class);
    }

    @Test
    void slottedComponentsAreFoundByQuery() {
        Assertions.assertEquals(view.contentButton,
                $(Button.class).withText("Content").single(),
                "Card content component should be found by query");
        Assertions.assertEquals(view.headerButton,
                $(Button.class).withText("Header").single(),
                "Card header component should be found by query");
        Assertions.assertEquals(view.headerPrefixButton,
                $(Button.class).withText("Header prefix").single(),
                "Card header prefix component should be found by query");
        Assertions.assertEquals(view.headerSuffixButton,
                $(Button.class).withText("Header suffix").single(),
                "Card header suffix component should be found by query");
        Assertions.assertEquals(view.footerButton,
                $(Button.class).withText("Footer").single(),
                "Card footer component should be found by query");
    }
}
