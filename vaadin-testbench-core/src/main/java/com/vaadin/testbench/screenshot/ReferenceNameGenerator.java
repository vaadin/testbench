package com.vaadin.testbench.screenshot;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

/**
 * Generates the name of a reference screen shot from a string ID and browser
 * information.
 */
public class ReferenceNameGenerator {

    public static final String PLATFORM_UNKNOWN = "unknown";

    /**
     * Generates the actual name of a reference screen shot from a reference ID
     * and browser information.
     *
     * @return The actual name.
     */
    public static String generateName(TestcaseInfo info) {
        String platForm = (info.platformName() != null)
                ? info.platformName().toLowerCase()
                : PLATFORM_UNKNOWN;

        return String.format("%s_%s_%s_%s",
                info.referenceId(),
                platForm,
                info.browserName(),
                majorVersion(info.version()));
    }

    /**
     * Finds the major version by parsing the browser version string.
     *
     * @return the major version of the browser.
     */
    public static String majorVersion(String browserVersion) {
        if (browserVersion.contains(".")) {
            String major = browserVersion.substring(0,
                    browserVersion.indexOf('.'));
            if (major.contains("-")) {
                major = major.substring(major.indexOf("-") + 1);
            }
            return major;
        }

        return browserVersion;
    }

    public static class TestcaseInfo {

        private final String referenceId;
        private final String browserName;
        private final String platformName;
        private final String version;

        /**
         * <p>Constructor for TestcaseInfo.</p>
         *
         * @param referenceId   ,
         * @param browserName,
         * @param platformName,
         * @param version,
         */
        public TestcaseInfo(String referenceId,
                            String browserName,
                            String platformName,
                            String version) {

            this.referenceId = referenceId;
            this.browserName = browserName;
            this.platformName = platformName;
            this.version = version;
        }

        public String referenceId() {
            return referenceId;
        }

        public String browserName() {
            return browserName;
        }

        public String platformName() {
            return platformName;
        }

        public String version() {
            return version;
        }
    }
}
