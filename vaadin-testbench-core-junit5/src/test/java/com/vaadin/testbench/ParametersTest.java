/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.http.ClientConfig;

public class ParametersTest {

    @Test
    public void testDefaultValues() {
        Assertions.assertEquals(2, Parameters.getMaxScreenshotRetries());
        Assertions.assertEquals(0.01,
                Parameters.getScreenshotComparisonTolerance(), 0.0);
        Assertions.assertEquals(500, Parameters.getScreenshotRetryDelay());
        Assertions.assertEquals(50, Parameters.getTestsInParallel());
        Assertions.assertEquals(20, Parameters.getTestSuitesInParallel());
        Assertions.assertEquals("error-screenshots",
                Parameters.getScreenshotErrorDirectory());
        Assertions.assertEquals("reference-screenshots",
                Parameters.getScreenshotReferenceDirectory());
        Assertions.assertEquals(false, Parameters.isDebug());
        Assertions.assertEquals(false,
                Parameters.isScreenshotComparisonCursorDetection());
        Assertions.assertFalse(Parameters.isHeadless());
        Assertions.assertEquals(
                ClientConfig.defaultConfig().readTimeout().toSeconds(),
                Parameters.getReadTimeout());
    }

    @Test
    public void parseRunLocallySystemProperty() {
        Assertions.assertArrayEquals(new String[] { "", "" },
                Parameters.parseRunLocally(""));
        Assertions.assertArrayEquals(new String[] { "foo", "" },
                Parameters.parseRunLocally("foo"));
        Assertions.assertArrayEquals(new String[] { "foo1", "" },
                Parameters.parseRunLocally("foo1"));
        Assertions.assertArrayEquals(new String[] { "foo", "1" },
                Parameters.parseRunLocally("foo-1"));
        Assertions.assertArrayEquals(new String[] { "foo1", "1" },
                Parameters.parseRunLocally("foo1-1"));
        Assertions.assertArrayEquals(new String[] { "foo1", "1-1" },
                Parameters.parseRunLocally("foo1-1-1"));
    }

    @Test
    public void setChromeOptionsSystemProperty() {
            try {
                    Parameters.setChromeOptions("--foo");
                    Assertions.assertArrayEquals(new String[] { "--foo", },
                                    Parameters.getChromeOptions());
                    Parameters.setChromeOptions("--foo --bar --x");
                                    Assertions.assertArrayEquals(new String[] { "--foo", "--bar", "--x" },
                                    Parameters.getChromeOptions());
                    Parameters.setChromeOptions("--foo,--bar, --x");
                                    Assertions.assertArrayEquals(new String[] { "--foo", "--bar", "--x" },
                                    Parameters.getChromeOptions());
                    Parameters.setChromeOptions("--window-size=\"nnn,nnn\"");
                    Assertions.assertArrayEquals(new String[] { "--window-size=\"nnn,nnn\"", },
                                    Parameters.getChromeOptions());
                    Parameters.setChromeOptions("--window-size=nnn,nnn,--bar,--x");
                    Assertions.assertArrayEquals(
                                    new String[] { "--window-size=nnn,nnn", "--bar", "--x" },
                                                    Parameters.getChromeOptions());
                    Parameters.setChromeOptions(
                                    "--foo, --bar, --window-size=400,100\n--user-agent=\"Mozilla,5.0\"\t--proxy='1.1.1.1,8080'");
                    Assertions.assertArrayEquals(
                                    new String[] { "--foo", "--bar", "--window-size=400,100",
                                                    "--user-agent=\"Mozilla,5.0\"", "--proxy='1.1.1.1,8080'" },
                                    Parameters.getChromeOptions());
                    Parameters.setChromeOptions("");
                    Assertions.assertArrayEquals(new String[] {}, Parameters.getChromeOptions());
            } finally {
                    Parameters.setChromeOptions(null);
            }
    }

}
