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
package com.vaadin.testbench.screenshot;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;

/**
 * Generates the name of a reference screen shot from a string ID and browser
 * information.
 */
public class ReferenceNameGenerator {

    /**
     * Generates the actual name of a reference screen shot from a reference ID
     * and browser information.
     *
     * @param referenceId
     *            the reference ID
     * @param browserCapabilities
     *            a {@link Capabilities} instance containing information on the
     *            browser.
     * @return The actual name.
     */
    public String generateName(String referenceId,
            Capabilities browserCapabilities) {
        String platformString;

        Platform platform = browserCapabilities.getPlatformName();
        if (platform != null) {
            platformString = platform.name().toLowerCase();
        } else {
            platformString = "unknown";
        }

        return String.format("%s_%s_%s_%s", referenceId, platformString,
                browserCapabilities.getBrowserName(),
                getMajorVersion(browserCapabilities));
    }

    /**
     * Finds the major version by parsing the browser version string.
     *
     * @param browserCapabilities
     *            the capabilities object holding the version information
     * @return the major version of the browser.
     */
    public static String getMajorVersion(Capabilities browserCapabilities) {
        String versionString = browserCapabilities.getBrowserVersion();
        if (versionString.equals("")) {
            Object browserVersion = browserCapabilities
                    .getCapability("browserVersion");
            if (browserVersion != null) {
                versionString = browserVersion.toString();
            }
        }
        if (versionString.contains(".")) {
            String major = versionString.substring(0,
                    versionString.indexOf('.'));
            if (major.contains("-")) {
                major = major.substring(major.indexOf("-") + 1);
            }
            return major;
        }
        return versionString;
    }

}
