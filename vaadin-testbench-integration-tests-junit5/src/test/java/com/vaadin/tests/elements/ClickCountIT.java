/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Isolated;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.ClickCountView;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractBrowserTB9Test;

// Parameters.useActionsClick is global, so no other test may click meanwhile
@Isolated
public class ClickCountIT extends AbstractBrowserTB9Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return ClickCountView.class;
    }

    @BeforeEach
    public void open() {
        openTestURL();
    }

    @BrowserTest
    public void click_usesJavascriptByDefault() {
        $(NativeButtonElement.class).id("click-target").click();
        Assertions.assertEquals("0", $("div").id("click-count").getText());
    }

    @BrowserTest
    public void click_actionsClickEnabled_hasClickCountOne() {
        TestBenchElement button = $(NativeButtonElement.class)
                .id("click-target");
        Parameters.setUseActionsClick(true);
        try {
            button.click();
        } finally {
            Parameters.setUseActionsClick(false);
        }
        Assertions.assertEquals("1", $("div").id("click-count").getText());
    }
}
