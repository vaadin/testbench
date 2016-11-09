/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests.testbenchapi;

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
