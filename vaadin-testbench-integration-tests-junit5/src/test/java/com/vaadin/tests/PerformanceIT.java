/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PerformanceView;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.tests.elements.NativeButtonElement;

public class PerformanceIT extends AbstractBrowserTB9Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return PerformanceView.class;
    }

    @BrowserTest
    public void serverTime() {
        openTestURL();

        $(NativeButtonElement.class).first().click();
        Assertions.assertEquals(1500.0,
                testBench().timeSpentServicingLastRequest(), 500.0);

        $(NativeButtonElement.class).first().click();
        Assertions.assertEquals(3000.0,
                testBench().totalTimeSpentServicingRequests(), 1000.0);
    }

    @BrowserTest
    public void renderingTime() {
        openTestURL();
        long initialRendering = testBench().timeSpentRenderingLastRequest();
        // Assert initial processing is takes 1-250 ms
        Assertions.assertTrue(initialRendering > 0 && initialRendering <= 250);
        // Assert total processing time is larger than initial processing time
        long totalRendering = testBench().totalTimeSpentRendering();
        Assertions.assertTrue(totalRendering > initialRendering,
                "totalTimeSpentRendering() > initialRendering");

        $(NativeButtonElement.class).first().click();
        $(NativeButtonElement.class).first().click();
        $(NativeButtonElement.class).first().click();

        // Assuming rendering three poll responses is done in 50ms
        Assertions.assertEquals(totalRendering,
                testBench().totalTimeSpentRendering(), 50);
    }

}
