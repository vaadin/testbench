/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel.setup;

import com.vaadin.testbench.Capabilities;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.backend.BackendChoice;
import com.vaadin.testbench.backend.BrowserSetup;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.spi.BrowserBackend;

/**
 * Starts a test's browser, on a hub or on this machine, on the backend
 * {@link BackendChoice} picks. Holds the capabilities the next browser gets;
 * the JUnit integration sets them from {@code @BrowserConfiguration}.
 */
public class SetupDriver {

    private Capabilities desiredCapabilities = Browser.FIREFOX
            .getDesiredCapabilities();

    /**
     * Starts a browser with {@link #getDesiredCapabilities()} on the hub.
     *
     * @param hubURL
     *            URL of the Hub to run the tests on
     */
    public BrowserBackend setupRemoteDriver(String hubURL) throws Exception {
        return BackendChoice
                .launch(new BrowserSetup(desiredCapabilities, hubURL));
    }

    /** Starts a browser with {@link #getDesiredCapabilities()} locally. */
    public BrowserBackend setupLocalDriver() throws Exception {
        return BackendChoice
                .launch(new BrowserSetup(desiredCapabilities, null));
    }

    /**
     * Starts the given browser locally, as {@link RunLocally} asks, and makes
     * its capabilities the desired ones.
     *
     * @param version
     *            version of the browser, or {@code ""} for any
     */
    public BrowserBackend setupLocalDriver(Browser runLocallyBrowser,
            String version) throws Exception {
        assert (runLocallyBrowser != null);
        setDesiredCapabilities(BrowserUtil.getBrowserFactory()
                .create(runLocallyBrowser, version));
        return setupLocalDriver();
    }

    /** {@link #setupLocalDriver(Browser, String)} for any version. */
    public BrowserBackend setupLocalDriver(Browser runLocallyBrowser)
            throws Exception {
        return setupLocalDriver(runLocallyBrowser, "");
    }

    public Capabilities getDesiredCapabilities() {
        return desiredCapabilities;
    }

    public void setDesiredCapabilities(Capabilities desiredCapabilities) {
        this.desiredCapabilities = desiredCapabilities;
    }
}
