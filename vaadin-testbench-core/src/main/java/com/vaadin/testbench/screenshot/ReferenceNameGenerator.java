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
package com.vaadin.testbench.screenshot;

import org.openqa.selenium.Capabilities;

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
