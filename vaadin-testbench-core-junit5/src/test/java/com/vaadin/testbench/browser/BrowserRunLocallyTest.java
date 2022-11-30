/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.parallel.Browser;

@RunLocally(value = Browser.CHROME, version = "34")
public class BrowserRunLocallyTest extends BrowserExtension {

    public BrowserRunLocallyTest() {
        super(null);
    }

    @Test
    public void runLocallyFromAnnotationOrSystemProperty() {
        Assertions.assertEquals(Browser.CHROME,
                getRunLocallyBrowser(getClass()));
        Assertions.assertEquals("34", getRunLocallyBrowserVersion(getClass()));

        System.setProperty("com.vaadin.testbench.Parameters.runLocally",
                "firefox");
        Assertions.assertEquals(Browser.FIREFOX,
                getRunLocallyBrowser(getClass()));
        Assertions.assertEquals("", getRunLocallyBrowserVersion(getClass()));

        System.setProperty("com.vaadin.testbench.Parameters.runLocally",
                "edge-14");
        Assertions.assertEquals(Browser.EDGE, getRunLocallyBrowser(getClass()));
        Assertions.assertEquals("14", getRunLocallyBrowserVersion(getClass()));

        System.clearProperty("com.vaadin.testbench.Parameters.runLocally");
        Assertions.assertEquals(Browser.CHROME,
                getRunLocallyBrowser(getClass()));
        Assertions.assertEquals("34", getRunLocallyBrowserVersion(getClass()));
    }

}
