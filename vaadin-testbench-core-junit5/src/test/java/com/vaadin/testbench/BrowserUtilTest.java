/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.BrowserUtil;

public class BrowserUtilTest {

    @Test
    public void supportedPlatformFromEnum() {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setPlatform(Platform.WIN8);
        Assertions.assertEquals("Windows", BrowserUtil.getPlatform(dc));
    }

    @Test
    public void noPlatform() {
        DesiredCapabilities dc = new DesiredCapabilities();
        Assertions.assertEquals("Unknown", BrowserUtil.getPlatform(dc));
    }

    @Test
    public void platformNoCapabilities() {
        Assertions.assertEquals("Unknown", BrowserUtil.getPlatform(null));
    }

    @Test
    public void platformWithoutEnum() {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability("platformName", "foobar");
        Assertions.assertEquals("foobar", BrowserUtil.getPlatform(dc));
    }
}
