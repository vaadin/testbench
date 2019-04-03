package com.vaadin.testbench.screenshot;

import com.vaadin.frp.model.serial.Quad;

import java.util.function.Function;

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
    public static Function<TestcaseInfo, String> generateName() {
        return (info) -> {
            String platForm = (info.platformName() != null)
                    ? info.platformName().toLowerCase()
                    : PLATFORM_UNKNOWN;

            return String.format("%s_%s_%s_%s",
                    info.referenceId(),
                    platForm,
                    info.browserName(),
                    majorVersion().apply(info.version()));
        };
    }

    /**
     * Finds the major version by parsing the browser version string.
     *
     * @return the major version of the browser.
     */
    public static Function<String, String> majorVersion() {
        return (browserVersion) -> {
            if (browserVersion.contains(".")) {
                String major = browserVersion.substring(0,
                        browserVersion.indexOf('.'));
                if (major.contains("-")) {
                    major = major.substring(major.indexOf("-") + 1);
                }
                return major;
            }
            return browserVersion;
        };
    }

    public static class TestcaseInfo extends Quad<String, String, String, String> {
        /**
         * <p>Constructor for Quad.</p>
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
            super(referenceId, browserName, platformName, version);
        }

        public String referenceId() {
            return getT1();
        }

        public String browserName() {
            return getT2();
        }

        public String platformName() {
            return getT3();
        }

        public String version() {
            return getT4();
        }
    }
}
