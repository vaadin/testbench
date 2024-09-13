/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import static org.junit.Assert.assertEquals;

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
        assertEquals(Browser.CHROME, getRunLocallyBrowser());
        assertEquals("34", getRunLocallyBrowserVersion());

        System.setProperty("com.vaadin.testbench.Parameters.runLocally",
                "phantomjs");
        assertEquals(Browser.PHANTOMJS, getRunLocallyBrowser());
        assertEquals("", getRunLocallyBrowserVersion());

        System.setProperty("com.vaadin.testbench.Parameters.runLocally",
                "ie11");
        assertEquals(Browser.IE11, getRunLocallyBrowser());
        assertEquals("", getRunLocallyBrowserVersion());

        System.setProperty("com.vaadin.testbench.Parameters.runLocally",
                "edge-14");
        assertEquals(Browser.EDGE, getRunLocallyBrowser());
        assertEquals("14", getRunLocallyBrowserVersion());

        System.clearProperty("com.vaadin.testbench.Parameters.runLocally");
        assertEquals(Browser.CHROME, getRunLocallyBrowser());
        assertEquals("34", getRunLocallyBrowserVersion());
    }

}
