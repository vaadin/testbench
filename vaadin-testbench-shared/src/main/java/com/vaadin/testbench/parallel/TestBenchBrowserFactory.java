/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import com.vaadin.testbench.Capabilities;
import com.vaadin.testbench.Platform;

import com.vaadin.testbench.annotations.BrowserFactory;

/**
 * <p>
 * Interface used to create {@link Capabilities} configurations suitable for
 * test cases.
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
     * @return {@link Capabilities} with given browser, and default version and
     *         platform
     */
    Capabilities create(Browser browser);

    /**
     * @param browser
     *            generic browser to run the test on
     * @param version
     *            browser version
     * @return {@link Capabilities} with given browser and version, and default
     *         platform
     */
    Capabilities create(Browser browser, String version);

    /**
     * @param browser
     *            generic browser to run the test on
     * @param version
     *            browser version
     * @param platform
     *            platform in which to run the test
     * @return {@link Capabilities} with given browser, version and platform
     */
    Capabilities create(Browser browser, String version, Platform platform);
}
