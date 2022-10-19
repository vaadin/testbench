/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.capabilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.parallel.Browser;

@RunLocally(value = Browser.CHROME, version = "34")
public class CapabilitiesRunLocallyTest extends CapabilitiesExtension {

    public CapabilitiesRunLocallyTest() {
        super(null);
    }

    @Test
    public void runLocallyFromAnnotationOrSystemProperty() {
        Assertions.assertEquals(Browser.CHROME, getRunLocallyBrowser());
        Assertions.assertEquals("34", getRunLocallyBrowserVersion());

        System.setProperty("com.vaadin.testbench.Parameters.runLocally",
                "firefox");
        Assertions.assertEquals(Browser.FIREFOX, getRunLocallyBrowser());
        Assertions.assertEquals("", getRunLocallyBrowserVersion());

        System.setProperty("com.vaadin.testbench.Parameters.runLocally",
                "edge-14");
        Assertions.assertEquals(Browser.EDGE, getRunLocallyBrowser());
        Assertions.assertEquals("14", getRunLocallyBrowserVersion());

        System.clearProperty("com.vaadin.testbench.Parameters.runLocally");
        Assertions.assertEquals(Browser.CHROME, getRunLocallyBrowser());
        Assertions.assertEquals("34", getRunLocallyBrowserVersion());
    }

}
