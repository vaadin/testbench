/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.BrowserUtil;

/**
 * Base class for tests which should be run on all supported browsers. The test
 * is automatically launched for multiple browsers in parallel by the test
 * runner.
 *
 * Sub classes can, but typically should not, restrict the browsers used by
 * implementing a
 *
 * <pre>
 * &#064;Parameters
 * public static Collection&lt;DesiredCapabilities&gt; getBrowsersForTest() {
 * }
 * </pre>
 *
 * @author Vaadin Ltd
 */

public abstract class MultiBrowserTest extends PrivateTB3Configuration {

    protected List<DesiredCapabilities> getBrowsersExcludingIE() {
        List<DesiredCapabilities> browsers = new ArrayList<DesiredCapabilities>(
                getAllBrowsers());
        browsers.remove(BrowserUtil.ie11());
        return browsers;
    }

    protected List<DesiredCapabilities> allBrowsers = null;

    /**
     * @return all supported browsers which are actively tested
     */
    public List<DesiredCapabilities> getAllBrowsers() {
        if (allBrowsers == null) {
            allBrowsers = new ArrayList<DesiredCapabilities>();
            allBrowsers.add(BrowserUtil.ie11());
            allBrowsers.add(BrowserUtil.firefox());
            allBrowsers.add(BrowserUtil.chrome());
            allBrowsers.add(BrowserUtil.phantomJS());
        }
        return Collections.unmodifiableList(allBrowsers);
    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        return getAllBrowsers();
    }

}
