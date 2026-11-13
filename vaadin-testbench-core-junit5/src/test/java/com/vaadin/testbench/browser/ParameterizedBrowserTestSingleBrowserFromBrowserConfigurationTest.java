/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.testbench.Capabilities;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;

@RunOnHub("remote.host")
class ParameterizedBrowserTestSingleBrowserFromBrowserConfigurationTest
        extends ParameterizedBrowserTestTest.Support {

    @BrowserConfiguration
    public List<Capabilities> getBrowserConfiguration() {
        ArrayList<Capabilities> capabilities = new ArrayList<>();
        capabilities.add(BrowserUtil.chrome());
        return capabilities;
    }

    public ParameterizedBrowserTestSingleBrowserFromBrowserConfigurationTest() {
        super(Browser.CHROME);
    }
}
