/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PerformanceView;
import com.vaadin.tests.elements.NativeButtonElement;

public class PerformanceIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return PerformanceView.class;
    }

    @Test
    @Ignore("timeSpentServicingLastRequest test is unstable")
    public void serverTime() {
        openTestURL();
        $(NativeButtonElement.class).first().click();

        Assert.assertEquals(1250.0, testBench().timeSpentServicingLastRequest(),
                250.0);
        $(NativeButtonElement.class).first().click();
        Assert.assertEquals(2500, testBench().totalTimeSpentServicingRequests(),
                500.0);
    }

    @Test
    @Ignore("timeSpentServicingLastRequest does not work: https://github.com/vaadin/testbench/issues/1316")
    public void renderingTime() {
        openTestURL();
        long initialRendering = testBench().timeSpentRenderingLastRequest();
        // Assuming initial rendering is done in 1-299ms
        Assert.assertEquals(150, initialRendering, 149);
        Assert.assertEquals(initialRendering,
                testBench().totalTimeSpentRendering());
        $(NativeButtonElement.class).first().click();
        $(NativeButtonElement.class).first().click();
        $(NativeButtonElement.class).first().click();

        // Assuming rendering three poll responses is done in 50ms
        Assert.assertTrue("totalTimeSpentRendering() > initialRendering",
                testBench().totalTimeSpentRendering() > initialRendering);
        Assert.assertEquals(initialRendering,
                testBench().totalTimeSpentRendering(), 50);
    }

}
