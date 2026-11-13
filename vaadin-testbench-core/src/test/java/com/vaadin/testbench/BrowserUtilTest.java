/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.parallel.BrowserUtil;

public class BrowserUtilTest {

    @Test
    public void supportedPlatformFromEnum() {
        Capabilities dc = new Capabilities();
        dc.setCapability(Capabilities.PLATFORM_NAME, Platform.WIN8);
        Assert.assertEquals("Windows", BrowserUtil.getPlatform(dc));
    }

    @Test
    public void noPlatform() {
        Capabilities dc = new Capabilities();
        Assert.assertEquals("Unknown", BrowserUtil.getPlatform(dc));
    }

    @Test
    public void platformNoCapabilities() {
        Assert.assertEquals("Unknown", BrowserUtil.getPlatform(null));
    }

    @Test
    public void platformWithoutEnum() {
        Capabilities dc = new Capabilities();
        dc.setCapability("platformName", "foobar");
        Assert.assertEquals("foobar", BrowserUtil.getPlatform(dc));
    }
}
