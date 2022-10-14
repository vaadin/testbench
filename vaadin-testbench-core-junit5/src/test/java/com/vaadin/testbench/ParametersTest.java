/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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

}
