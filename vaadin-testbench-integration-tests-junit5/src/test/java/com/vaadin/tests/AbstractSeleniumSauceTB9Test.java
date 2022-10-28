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

import io.github.bonigarcia.seljup.DriverCapabilities;
import io.github.bonigarcia.seljup.DriverUrl;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

/**
 * Example of how to use SeleniumJupiter together with TestBench 9+ features.
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractSeleniumSauceTB9Test
        extends AbstractSeleniumTB9Test {

    @DriverUrl
    String url = SauceLabsIntegration.getHubUrl();

    @DriverCapabilities
    DesiredCapabilities capabilities = new DesiredCapabilities();
    {
        capabilities.merge(BrowserUtil.chrome());
        SauceLabsIntegration.setDesiredCapabilities(capabilities);
    }

    @BeforeEach
    public void setDriver(RemoteWebDriver driver) {
        super.setDriver(driver);
    }

}
