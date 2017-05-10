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
package com.vaadin.tests.elements;

import static org.rapidpm.frp.matcher.Case.match;
import static org.rapidpm.frp.matcher.Case.matchCase;
import static org.rapidpm.frp.model.Result.success;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.rapidpm.frp.matcher.Case;
import org.rapidpm.frp.model.Result;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;

/**
 * Specifies default browser configuration for {@link PrivateTB3Configuration}
 * tests.
 */
public class VaadinBrowserFactory extends DefaultBrowserFactory {

    private static Map<Browser, String> defaultBrowserVersion = new HashMap<>();
    static {
        defaultBrowserVersion.put(Browser.CHROME, "40");
        defaultBrowserVersion.put(Browser.PHANTOMJS, "1");
        defaultBrowserVersion.put(Browser.SAFARI, "7");
        defaultBrowserVersion.put(Browser.IE11, "11");
        defaultBrowserVersion.put(Browser.FIREFOX, "45");
    }

    private static Map<Browser, Platform> defaultBrowserPlatform = new HashMap<>();
    static {
        defaultBrowserPlatform.put(Browser.CHROME, Platform.VISTA);
        defaultBrowserPlatform.put(Browser.PHANTOMJS, Platform.LINUX);
        defaultBrowserPlatform.put(Browser.SAFARI, Platform.MAC);
        defaultBrowserPlatform.put(Browser.IE11, Platform.WINDOWS);
        defaultBrowserPlatform.put(Browser.FIREFOX, Platform.WINDOWS);
    }

    @Override
    public DesiredCapabilities create(Browser browser, String version,
            Platform platform) {
        final String PHANTOMJS_PATH_PROPERTY = "phantomjs.binary.path";
        final String PHANTOMJS_PATH_VALUE = "/usr/bin/phantomjs2";

        Result<DesiredCapabilities> result =
            match(
                matchCase(() -> success((super.create(browser,version, platform)))),

                matchCase(() -> browser == Browser.PHANTOMJS, () -> {
                    DesiredCapabilities c = super.create(browser, "2",Platform.LINUX);
                    // Hack for the test cluster
                    c.setCapability(PHANTOMJS_PATH_PROPERTY, PHANTOMJS_PATH_VALUE);
                    return success(c);
                }),

                matchCase(() -> browser == Browser.FIREFOX, () -> {
                    final DesiredCapabilities c = super.create(browser, version, platform);
                    c.setCapability(FirefoxDriver.MARIONETTE, false);
                    return success(c);
                }),

                matchCase(() -> platform == Platform.ANY
                                     && defaultBrowserPlatform.containsKey(browser), () -> {
                    final DesiredCapabilities c = super.create(browser, version, platform);
                    c.setPlatform(defaultBrowserPlatform.get(browser));
                    return success(c);
                }),

                matchCase(() -> "".equals(version) && defaultBrowserVersion.containsKey(browser), () -> {
                    final DesiredCapabilities c = super.create(browser, version, platform);
                    c.setVersion(defaultBrowserVersion.get(browser));
                    return success(c);
                })
            );
        return result.get();
    }
}
