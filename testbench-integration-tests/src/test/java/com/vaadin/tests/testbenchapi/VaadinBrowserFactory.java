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

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;

/**
 * Specifies default browser configuration for {@link PrivateTB3Configuration}
 * tests.
 */
public class VaadinBrowserFactory extends DefaultBrowserFactory {

    private static Map<Browser, String> defaultBrowserVersion = new HashMap<Browser, String>();
    static {
        defaultBrowserVersion.put(Browser.CHROME, "40");
        defaultBrowserVersion.put(Browser.PHANTOMJS, "1");
        defaultBrowserVersion.put(Browser.SAFARI, "7");
        defaultBrowserVersion.put(Browser.IE8, "8");
        defaultBrowserVersion.put(Browser.IE9, "9");
        defaultBrowserVersion.put(Browser.IE10, "10");
        defaultBrowserVersion.put(Browser.IE11, "11");
        defaultBrowserVersion.put(Browser.FIREFOX, "24");
    }

    private static Map<Browser, Platform> defaultBrowserPlatform = new HashMap<Browser, Platform>();
    static {
        defaultBrowserPlatform.put(Browser.CHROME, Platform.VISTA);
        defaultBrowserPlatform.put(Browser.PHANTOMJS, Platform.LINUX);
        defaultBrowserPlatform.put(Browser.SAFARI, Platform.MAC);
        defaultBrowserPlatform.put(Browser.IE8, Platform.WINDOWS);
        defaultBrowserPlatform.put(Browser.IE9, Platform.WINDOWS);
        defaultBrowserPlatform.put(Browser.IE10, Platform.WINDOWS);
        defaultBrowserPlatform.put(Browser.IE11, Platform.WINDOWS);
        defaultBrowserPlatform.put(Browser.FIREFOX, Platform.XP);
    }

    @Override
    public DesiredCapabilities create(Browser browser, String version,
            Platform platform) {
        if (browser == Browser.PHANTOMJS) {
            DesiredCapabilities phantom2 = super.create(browser, "2",
                    Platform.LINUX);
            // Hack for the test cluster
            phantom2.setCapability("phantomjs.binary.path",
                    "/usr/bin/phantomjs2");
            return phantom2;
        }

        DesiredCapabilities desiredCapabilities = super.create(browser,
                version, platform);

        if (platform == Platform.ANY
                && defaultBrowserPlatform.containsKey(browser)) {
            desiredCapabilities
                    .setPlatform(defaultBrowserPlatform.get(browser));
        }

        if ("".equals(version) && defaultBrowserVersion.containsKey(browser)) {
            desiredCapabilities.setVersion(defaultBrowserVersion.get(browser));
        }
        return desiredCapabilities;
    }
}
