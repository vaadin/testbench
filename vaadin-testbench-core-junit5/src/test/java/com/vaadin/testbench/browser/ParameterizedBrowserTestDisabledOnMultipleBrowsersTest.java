/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import java.util.List;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.BrowserUtil;

/**
 * This test should be disabled, since it will run
 * a @{@link com.vaadin.testbench.ParameterizedBrowserTest} annotated method but
 * requiring execution on multiple browsers. If it fails with a
 * {@link NullPointerException} it means the
 * {@link BrowserExtension#evaluateExecutionCondition(ExtensionContext)} is not
 * working correctly.
 */
@RunOnHub("remote.host")
class ParameterizedBrowserTestDisabledOnMultipleBrowsersTest
        extends ParameterizedBrowserTestTest.Support {

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        return List.of(BrowserUtil.firefox(), BrowserUtil.firefox(),
                BrowserUtil.chrome());
    }

    public ParameterizedBrowserTestDisabledOnMultipleBrowsersTest() {
        super((DesiredCapabilities) null);
    }
}
