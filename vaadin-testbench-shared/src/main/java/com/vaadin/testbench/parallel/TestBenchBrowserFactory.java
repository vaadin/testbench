/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
