package com.vaadin.testbench;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.BrowserUtil;

public class BrowserUtilTest {

    @Test
    public void platformFromEnum() {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setPlatform(Platform.WIN8_1);
        Assert.assertEquals(Platform.WIN8_1.name(),
                BrowserUtil.getPlatform(dc));
    }

    @Test
    public void supportedPlatformFromEnum() {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setPlatform(Platform.WIN8);
        Assert.assertEquals("Windows", BrowserUtil.getPlatform(dc));
    }

    @Test
    public void noPlatform() {
        DesiredCapabilities dc = new DesiredCapabilities();
        Assert.assertEquals("Unknown", BrowserUtil.getPlatform(dc));
    }

    @Test
    public void platformNoCapabilities() {
        Assert.assertEquals("Unknown", BrowserUtil.getPlatform(null));
    }

    @Test
    public void platformWithoutEnum() {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability("platform", "foobar");
        Assert.assertEquals("foobar", BrowserUtil.getPlatform(dc));
    }
}
