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

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.ScreenshotOnFailureExtension;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.BrowserFactory;
import com.vaadin.testbench.parallel.BrowserUtil;

/**
 * Base class for TestBench 9+ tests. All TB9+ tests in the project should
 * extend this class.
 * <p>
 * Sub classes can, but typically should not, restrict the browsers used by
 * overriding the {@link #getBrowserConfiguration()} method:
 *
 * <pre>
 * &#064;Override
 * &#064;BrowserConfiguration
 * public List&lt;DesiredCapabilities&gt; getBrowserConfiguration() {
 * }
 * </pre>
 *
 * @author Vaadin Ltd
 */
@BrowserFactory(TB9TestBrowserFactory.class)
public abstract class AbstractBrowserTB9Test extends AbstractTB9Test {

    @RegisterExtension
    public ScreenshotOnFailureExtension screenshotOnFailureExtension = new ScreenshotOnFailureExtension(
            this, true);

    private Capabilities capabilities;

    @BeforeEach
    public void setWebDriverAndCapabilities(WebDriver driver,
            Capabilities capabilities) {
        setDriver(driver);
        this.capabilities = capabilities;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        return Arrays.asList(BrowserUtil.firefox(), BrowserUtil.chrome(),
                BrowserUtil.safari(), BrowserUtil.edge());
    }

}
