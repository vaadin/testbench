/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.ScreenshotOnFailureExtension;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.BrowserFactory;
import com.vaadin.testbench.browser.BrowserTestInfo;
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
    public void setWebDriverAndCapabilities(BrowserTestInfo browserTestInfo) {
        setDriver(browserTestInfo.driver());
        this.capabilities = browserTestInfo.capabilities();
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        List<DesiredCapabilities> caps;
        if (getDriver() instanceof RemoteWebDriver) {
            caps = Arrays.asList(BrowserUtil.firefox(), BrowserUtil.chrome(),
                    BrowserUtil.safari(), BrowserUtil.edge());
        } else {
            caps = Collections.singletonList(BrowserUtil.chrome());
        }
        caps.forEach(des -> des.setPlatform(Platform.WIN10));
        return caps;
    }

}
