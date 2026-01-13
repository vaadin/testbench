/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ParametersTest {

    @Test
    public void testDefaultValues() {
        assertEquals(2, Parameters.getMaxScreenshotRetries());
        assertEquals(0.01, Parameters.getScreenshotComparisonTolerance(), 0.0);
        assertEquals(500, Parameters.getScreenshotRetryDelay());
        assertEquals(50, Parameters.getTestsInParallel());
        assertEquals(20, Parameters.getTestSuitesInParallel());
        assertEquals("error-screenshots",
                Parameters.getScreenshotErrorDirectory());
        assertEquals("reference-screenshots",
                Parameters.getScreenshotReferenceDirectory());
        assertEquals(false, Parameters.isDebug());
        assertEquals(false, Parameters.isScreenshotComparisonCursorDetection());
    }

    @Test
    public void parseRunLocallySystemProperty() {
        assertArrayEquals(new String[] { "", "" },
                Parameters.parseRunLocally(""));
        assertArrayEquals(new String[] { "foo", "" },
                Parameters.parseRunLocally("foo"));
        assertArrayEquals(new String[] { "foo1", "" },
                Parameters.parseRunLocally("foo1"));
        assertArrayEquals(new String[] { "foo", "1" },
                Parameters.parseRunLocally("foo-1"));
        assertArrayEquals(new String[] { "foo1", "1" },
                Parameters.parseRunLocally("foo1-1"));
        assertArrayEquals(new String[] { "foo1", "1-1" },
                Parameters.parseRunLocally("foo1-1-1"));
    }

}
