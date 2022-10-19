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
package com.vaadin.tests;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

/**
 * Specifies default browser configuration for {@link AbstractTB9Test} tests.
 */
public class TB9TestBrowserFactory extends DefaultBrowserFactory {

    @Override
    public DesiredCapabilities create(Browser browser, String version,
            Platform platform) {
        if (browser != Browser.SAFARI) {
            platform = Platform.WIN10;
        }
        DesiredCapabilities desiredCapabilities = super.create(browser, version,
                platform);

        SauceLabsIntegration.setSauceLabsOption(desiredCapabilities,
                "screenResolution", "1600x1200");
        return desiredCapabilities;
    }
}
