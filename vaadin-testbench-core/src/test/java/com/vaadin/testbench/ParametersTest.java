/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.http.ClientConfig;

public class ParametersTest {

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(2, Parameters.getMaxScreenshotRetries());
        Assert.assertEquals(0.01, Parameters.getScreenshotComparisonTolerance(),
                0.0);
        Assert.assertEquals(500, Parameters.getScreenshotRetryDelay());
        Assert.assertEquals(50, Parameters.getTestsInParallel());
        Assert.assertEquals(20, Parameters.getTestSuitesInParallel());
        Assert.assertEquals("error-screenshots",
                Parameters.getScreenshotErrorDirectory());
        Assert.assertEquals("reference-screenshots",
                Parameters.getScreenshotReferenceDirectory());
        Assert.assertFalse(Parameters.isDebug());
        Assert.assertFalse(Parameters.isScreenshotComparisonCursorDetection());
        Assert.assertFalse(Parameters.isHeadless());
        Assert.assertEquals(
                ClientConfig.defaultConfig().readTimeout().toSeconds(),
                Parameters.getReadTimeout());
    }

    @Test
    public void parseRunLocallySystemProperty() {
        Assert.assertArrayEquals(new String[] { "", "" },
                Parameters.parseRunLocally(""));
        Assert.assertArrayEquals(new String[] { "foo", "" },
                Parameters.parseRunLocally("foo"));
        Assert.assertArrayEquals(new String[] { "foo1", "" },
                Parameters.parseRunLocally("foo1"));
        Assert.assertArrayEquals(new String[] { "foo", "1" },
                Parameters.parseRunLocally("foo-1"));
        Assert.assertArrayEquals(new String[] { "foo1", "1" },
                Parameters.parseRunLocally("foo1-1"));
        Assert.assertArrayEquals(new String[] { "foo1", "1-1" },
                Parameters.parseRunLocally("foo1-1-1"));
    }

    @Test
    public void setChromeOptionsSystemProperty() {
        try {
            Parameters.setChromeOptions("--foo");
            Assert.assertArrayEquals(new String[] { "--foo", },
                    Parameters.getChromeOptions());
            Parameters.setChromeOptions("--foo --bar --x");
            Assert.assertArrayEquals(new String[] { "--foo", "--bar", "--x" },
                    Parameters.getChromeOptions());
            Parameters.setChromeOptions("--foo,--bar, --x");
            Assert.assertArrayEquals(new String[] { "--foo", "--bar", "--x" },
                    Parameters.getChromeOptions());
            Parameters.setChromeOptions("--window-size=\"nnn,nnn\"");
            Assert.assertArrayEquals(
                    new String[] { "--window-size=\"nnn,nnn\"", },
                    Parameters.getChromeOptions());
            Parameters.setChromeOptions("--window-size=nnn,nnn,--bar,--x");
            Assert.assertArrayEquals(
                    new String[] { "--window-size=nnn,nnn", "--bar", "--x" },
                    Parameters.getChromeOptions());
            Parameters.setChromeOptions(
                    "--foo, --bar, --window-size=400,100\n--user-agent=\"Mozilla,5.0\"\t--proxy='1.1.1.1,8080'");
            Assert.assertArrayEquals(
                    new String[] { "--foo", "--bar", "--window-size=400,100",
                            "--user-agent=\"Mozilla,5.0\"",
                            "--proxy='1.1.1.1,8080'" },
                    Parameters.getChromeOptions());
            Parameters.setChromeOptions("");
            Assert.assertArrayEquals(new String[] {},
                    Parameters.getChromeOptions());
        } finally {
            Parameters.setChromeOptions(null);
        }
    }

}
