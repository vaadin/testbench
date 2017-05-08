package com.vaadin.tests.elements.ng;

import static com.vaadin.testbench.Parameters.setScreenshotComparisonCursorDetection;
import static com.vaadin.tests.elements.ng.tooling.BrowserDriverFunctions.ipSupplierLocalIP;
import static com.vaadin.tests.elements.ng.tooling.BrowserDriverFunctions.webDriverInstances;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.tests.elements.ng.tooling.HelperMethodsTrait;

/**
 *
 */

@RunWith(value = Parameterized.class)
public class BaseTest extends TestBenchTestCase implements HelperMethodsTrait {

    @Parameterized.Parameter
    public WebDriver nextWebDriver;

    @Parameterized.Parameters(name = "{index}: testPair - {0}")
    public static Object[] data() {
        return webDriverInstances.get().toArray();
    }

    @Test
    public void testIfNextWebDriverIsNotNull()
        throws Exception {
        Assert.assertNotNull(nextWebDriver);
    }

    @Before
    public void setUp()
        throws Exception {
        super.setDriver(nextWebDriver);
        setScreenshotComparisonCursorDetection(true);

        int w = SCREENSHOT_WIDTH;
        int h = SCREENSHOT_HEIGHT;

        //TODO deal wit IE8
        //        if (BrowserUtil.isIE8(nextWebDriver.)) {
        //            // IE8 gets size wrong, who would have guessed...
        //            w += 4;
        //            h += 4;
        //        }
        //        try {
        //            testBench().resizeViewPortTo(w, h);
        //        } catch (UnsupportedOperationException e) {
        //            // Opera does not support this...
        //        }

    }

    @After
    public void tearDown()
        throws Exception {
        Optional
            .ofNullable(nextWebDriver)
            .ifPresent(WebDriver::quit);
    }

    //    statefull parts

    private boolean debug = false;
    private boolean push = false;

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean isPush() {
        return push;
    }

    @Override
    public void setPush(boolean push) {
        this.push = push;
    }

    @Override
    public String getDeploymentHostname() {
        return ipSupplierLocalIP.get();
    }
}
