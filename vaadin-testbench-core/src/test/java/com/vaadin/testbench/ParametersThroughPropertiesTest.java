/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import static org.junit.Assert.assertEquals;

public class ParametersThroughPropertiesTest {

    // Can't be run as part of the suite as system properties must be set before
    // loading the Parameters class. If this is done by another class before
    // this, the test will fail.
    // Run manually only this test to verify that setting parameters using
    // properties works
    // @Test
    public void testSettingValuesUsingProperties() {
        System.setProperty(Parameters.class.getName() + ".maxScreenshotRetries",
                125 + "");
        System.setProperty(
                Parameters.class.getName() + ".screenshotComparisonTolerance",
                126.12 + "");
        System.setProperty(Parameters.class.getName() + ".screenshotRetryDelay",
                12 + "");
        System.setProperty(Parameters.class.getName() + ".testsInParallel",
                13 + "");
        System.setProperty(Parameters.class.getName() + ".testSuitesInParallel",
                14 + "");
        System.setProperty(
                Parameters.class.getName() + ".screenshotErrorDirectory",
                "errors-yo");
        System.setProperty(
                Parameters.class.getName() + ".screenshotReferenceDirectory",
                "screenshots-yo");
        System.setProperty(Parameters.class.getName() + ".debug", "True");
        System.setProperty(Parameters.class.getName()
                + ".screenshotComparisonCursorDetection", "true");

        assertEquals(125, Parameters.getMaxScreenshotRetries());
        assertEquals(126.12, Parameters.getScreenshotComparisonTolerance(),
                0.0);

        assertEquals(12, Parameters.getScreenshotRetryDelay());
        assertEquals(13, Parameters.getTestsInParallel());
        assertEquals(14, Parameters.getTestSuitesInParallel());
        assertEquals("errors-yo", Parameters.getScreenshotErrorDirectory());
        assertEquals("screenshots-yo",
                Parameters.getScreenshotReferenceDirectory());
        assertEquals(true, Parameters.isDebug());
        assertEquals(true, Parameters.isScreenshotComparisonCursorDetection());
    }

}
