/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests.elements;

import static com.vaadin.tests.elements.ng.tooling.BrowserDriverFunctions.ipSupplierLocalIP;

import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.UIProvider;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.ParallelTest;
import com.vaadin.testbench.parallel.setup.SetupDriver;
import com.vaadin.tests.elements.ng.tooling.AssertsTrait;
import com.vaadin.tests.elements.ng.tooling.HelperMethodsTrait;
import com.vaadin.ui.UI;

/**
 * Base class for TestBench 3+ tests. All TB3+ tests in the project should
 * extend this class.
 * <p>
 * Provides:
 * <ul>
 * <li>Helpers for browser selection</li>
 * <li>Hub connection setup and teardown</li>
 * <li>Automatic generation of URL for a given test on the development server
 * using {@link #getUIClass()} or by automatically finding an enclosing UI class
 * and based on requested features, e.g. {@link #isDebug()}, {@link #isPush()}</li>
 * <li>Generic helpers for creating TB3+ tests</li>
 * </ul>
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractTB3Test extends ParallelTest implements HelperMethodsTrait, AssertsTrait {

    static {
        com.vaadin.testbench.Parameters
            .setScreenshotComparisonCursorDetection(true);
    }

    /**
     * Connect to the hub using a remote web driver, set the canvas size and
     * opens the initial URL as specified by {@link #getTestUrl()}
     *
     * @throws Exception
     */
    @Override
    @Before
    public void setup()
        throws Exception {
        // override local driver behaviour, so we can easily specify local
        // PhantomJS
        // with a system property
        if (getBooleanProperty("localPhantom")) {
            WebDriver driver = new SetupDriver()
                .setupLocalDriver(Browser.PHANTOMJS);
            setDriver(driver);
        } else {
            super.setup();
        }

        int w = SCREENSHOT_WIDTH;
        int h = SCREENSHOT_HEIGHT;

        if (BrowserUtil.isIE8(getDesiredCapabilities())) {
            // IE8 gets size wrong, who would have guessed...
            w += 4;
            h += 4;
        }
        try {
            testBench().resizeViewPortTo(w, h);
        } catch (UnsupportedOperationException e) {
            // Opera does not support this...
        }
    }

    protected boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(System.getProperty(key));
    }


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
