/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractTB6Test;

public class ExecuteJavascriptIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return ElementQueryView.class;
    }

    @Test
    public void getProperty() throws Exception {
        openTestURL();

        TestBenchElement button = $(NativeButtonElement.class).first();
        Long offsetTop = button.getPropertyDouble("offsetTop").longValue();
        Assert.assertEquals(Long.valueOf(0), offsetTop);
    }
}
