package com.vaadin.testbench.screenshot;

import org.openqa.selenium.Capabilities;

/**
 * Generates the name of a reference screen shot from a string ID and browser
 * information.
 * 
 * @author Jonatan Kronqvist / Vaadin Ltd
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
        return String.format("%s_%s_%s_%s", referenceId, browserCapabilities
                .getPlatform().toString().toLowerCase(),
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
        String versionString = browserCapabilities.getVersion();
        if (versionString.contains(".")) {
            return versionString.substring(0, versionString.indexOf('.'));
        }
        return versionString;
    }

}
