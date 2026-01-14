/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.annotations.RunLocally;

@RunLocally(value = Browser.CHROME, version = "34")
public class ParallelRunLocallyTest extends ParallelTest {

    @Override
    public void setup() throws Exception {
        // Do not actually start a session, just test the class methods
    }

    @Test
    public void runLocallyFromAnnotationOrSystemProperty() {
        Assert.assertEquals(Browser.CHROME, getRunLocallyBrowser());
        Assert.assertEquals("34", getRunLocallyBrowserVersion());

        System.setProperty("com.vaadin.testbench.Parameters.runLocally",
                "firefox");
        Assert.assertEquals(Browser.FIREFOX, getRunLocallyBrowser());
        Assert.assertEquals("", getRunLocallyBrowserVersion());

        System.setProperty("com.vaadin.testbench.Parameters.runLocally",
                "edge-14");
        Assert.assertEquals(Browser.EDGE, getRunLocallyBrowser());
        Assert.assertEquals("14", getRunLocallyBrowserVersion());

        System.clearProperty("com.vaadin.testbench.Parameters.runLocally");
        Assert.assertEquals(Browser.CHROME, getRunLocallyBrowser());
        Assert.assertEquals("34", getRunLocallyBrowserVersion());
    }

}
