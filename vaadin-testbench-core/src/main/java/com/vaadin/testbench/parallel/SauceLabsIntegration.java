package com.vaadin.testbench.parallel;

import java.util.logging.Logger;

import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Integration methods for Sauce Labs testing used by {@link ParallelTest}
 *
 */
public interface SauceLabsIntegration {

    /**
     * Sets needed desired capabilities, mainly tunnel identifier, based on the
     * given sauce.options String
     * 
     * @param desiredCapabilities
     *            DesiredCapabilities for RemoteWebDriver. Must not be null.
     * @param sauceOptions
     *            options to be parsed and added as capabilities to the given
     *            DesiredCapabilities object
     */
    void setDesiredCapabilities(DesiredCapabilities desiredCapabilities,
            String sauceOptions);

    /**
     * Returns the HubUrl for running tests in Sauce Labs tunnel. Reads required
     * credentials from sauce.user and sauce.sauceAccessKey or environment
     * variables SAUCE_USERNAME and SAUCE_ACCESS_KEY. If both system property
     * and environment variable are defined, the system property is used.
     * 
     * @return url String to be used in Sauce Labs test run
     */
    String getHubUrl();
}
