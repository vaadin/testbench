/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import org.junit.Assert;

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

        Assert.assertEquals(125, Parameters.getMaxScreenshotRetries());
        Assert.assertEquals(126.12,
                Parameters.getScreenshotComparisonTolerance(), 0.0);

        Assert.assertEquals(12, Parameters.getScreenshotRetryDelay());
        Assert.assertEquals(13, Parameters.getTestsInParallel());
        Assert.assertEquals(14, Parameters.getTestSuitesInParallel());
        Assert.assertEquals("errors-yo",
                Parameters.getScreenshotErrorDirectory());
        Assert.assertEquals("screenshots-yo",
                Parameters.getScreenshotReferenceDirectory());
        Assert.assertEquals(true, Parameters.isDebug());
        Assert.assertEquals(true,
                Parameters.isScreenshotComparisonCursorDetection());
    }

}
