/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.parallel;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.BrowserFactory;

/**
 * <p>
 * Interface used to create {@link DesiredCapabilities} configurations suitable
 * for test cases.
 * </p>
 * <p>
 * This interface should be implemented and used in test cases through the
 * {@link BrowserFactory} annotation. Classes that implement this interface must
 * have a constructor with zero arguments.
 * </p>
 */
public interface TestBenchBrowserFactory {

    /**
     * @param browser
     *            generic browser to run the test on
     * @return {@link DesiredCapabilities} with given browser, and default
     *         version and platform
     */
    DesiredCapabilities create(Browser browser);

    /**
     * @param browser
     *            generic browser to run the test on
     * @param version
     *            browser version
     * @return {@link DesiredCapabilities} with given browser and version, and
     *         default platform
     */
    DesiredCapabilities create(Browser browser, String version);

    /**
     * @param browser
     *            generic browser to run the test on
     * @param version
     *            browser version
     * @param platform
     *            platform in which to run the test
     * @return {@link DesiredCapabilities} with given browser, version and
     *         platform
     */
    DesiredCapabilities create(Browser browser, String version,
            Platform platform);
}
