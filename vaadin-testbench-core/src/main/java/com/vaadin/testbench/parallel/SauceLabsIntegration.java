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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration methods for Sauce Labs testing used by {@link ParallelTest}
 *
 */
public class SauceLabsIntegration {
    private static Logger getLogger() {
        return LoggerFactory.getLogger(SauceLabsIntegration.class);
    }

    private static final String SAUCE_USERNAME_ENV = "SAUCE_USERNAME";
    private static final String SAUCE_USERNAME_PROP = "sauce.user";
    private static final String SAUCE_ACCESS_KEY_ENV = "SAUCE_ACCESS_KEY";
    private static final String SAUCE_ACCESS_KEY_PROP = "sauce.sauceAccessKey";
    private static final String SAUCE_TUNNELID_PROP = "sauce.tunnelId";
    private static final String SAUCE_TUNNELID_ENV = "SAUCE_TUNNEL_ID";

    /**
     * Sets needed desired capabilities for authentication and using the correct
     * sauce tunnel (if in use).
     * <p>
     * Reads required credentials from sauce.user and sauce.sauceAccessKey or
     * environment variables SAUCE_USERNAME and SAUCE_ACCESS_KEY.
     * <p>
     * Reads the tunnel identifier from a sauce.tunnelId system property or
     * SAUCE_TUNNEL_ID environment vairable.
     * <p>
     * If both system property and environment variable are defined, the system
     * property is used.
     *
     * @param desiredCapabilities
     *            DesiredCapabilities for RemoteWebDriver. Must not be null.
     * @param sauceOptions
     *            options to be parsed and added as capabilities to the given
     *            DesiredCapabilities object
     */
    static void setDesiredCapabilities(
            DesiredCapabilities desiredCapabilities) {

        String username = getSauceUser();
        String accessKey = getSauceAccessKey();
        String tunnelId = getTunnelIdentifier();

        if (username != null) {
            setSauceLabsOption(desiredCapabilities, "username", username);
        } else {
            getLogger().debug("You can give a Sauce Labs user name using -D"
                    + SAUCE_USERNAME_PROP + "=<username> or by "
                    + SAUCE_USERNAME_ENV + " environment variable.");
        }
        if (accessKey != null) {
            setSauceLabsOption(desiredCapabilities, "access_key", accessKey);
        } else {
            getLogger().debug("You can give a Sauce Labs access key using -D"
                    + SAUCE_ACCESS_KEY_PROP + "=<accesskey> or by "
                    + SAUCE_ACCESS_KEY_ENV + " environment variable.");
        }

        if (tunnelId != null) {
            setSauceLabsOption(desiredCapabilities, "tunnelIdentifier",
                    tunnelId);
        }
    }

    /**
     * Sets the given SauceLabs option to the given value.
     *
     * The available SauceLabs options are listed at
     * https://docs.saucelabs.com/dev/test-configuration-options/.
     *
     * @param desiredCapabilities
     *            the desired capabilities object
     * @param key
     *            the option key
     * @param value
     *            the option value
     */
    public static void setSauceLabsOption(
            DesiredCapabilities desiredCapabilities, String key, Object value) {
        MutableCapabilities sauceOptions = getSauceLabsCapabilities(
                desiredCapabilities);
        if (sauceOptions == null) {
            sauceOptions = new MutableCapabilities();
            desiredCapabilities.setCapability("sauce:options", sauceOptions);
        }
        sauceOptions.setCapability(key, value);
    }

    private static MutableCapabilities getSauceLabsCapabilities(
            DesiredCapabilities desiredCapabilities) {
        return (MutableCapabilities) desiredCapabilities
                .getCapability("sauce:options");
    }

    /**
     * Gets the given SauceLabs option.
     *
     * The available SauceLabs options are listed at
     * https://docs.saucelabs.com/dev/test-configuration-options/.
     *
     * @param desiredCapabilities
     *            the desired capabilities object
     * @param key
     *            the option key
     * @return the option value that was set or null
     */
    public static Object getSauceLabsOption(
            DesiredCapabilities desiredCapabilities, String key) {
        MutableCapabilities sauceOptions = getSauceLabsCapabilities(
                desiredCapabilities);
        if (sauceOptions == null) {
            return null;
        }
        return sauceOptions.getCapability(key);
    }

    static String getTunnelIdentifier() {
        String tunnelId = getSystemPropertyOrEnv(SAUCE_TUNNELID_PROP,
                SAUCE_TUNNELID_ENV);
        if (tunnelId == null) {
            // For backwards compatibility only
            String sauceOptions = System.getProperty("sauce.options");
            tunnelId = getTunnelIdentifierFromOptions(sauceOptions);
        }

        return tunnelId;
    }

    private static String getTunnelIdentifierFromOptions(String options) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        Iterator<String> tokensIterator = Arrays.asList(options.split(" "))
                .iterator();
        while (tokensIterator.hasNext()) {
            String currentToken = tokensIterator.next();
            if (tokensIterator.hasNext() && (currentToken.equals("-i")
                    || currentToken.equals("--tunnel-identifier"))) {
                // next token is identifier
                return tokensIterator.next();
            }
        }
        return null;
    }

    /**
     * Returns the HubUrl for running tests in Sauce Labs.
     *
     * @return url String to be used in Sauce Labs test run
     */
    static String getHubUrl() {
        return "https://ondemand.saucelabs.com/wd/hub";
    }

    static boolean isConfiguredForSauceLabs() {
        String user = getSauceUser();
        String accessKey = getSauceAccessKey();
        return user != null && !user.isEmpty() && accessKey != null
                && !accessKey.isEmpty();
    }

    static String getSauceUser() {
        return getSystemPropertyOrEnv(SAUCE_USERNAME_PROP, SAUCE_USERNAME_ENV);
    }

    static String getSauceAccessKey() {
        return getSystemPropertyOrEnv(SAUCE_ACCESS_KEY_PROP,
                SAUCE_ACCESS_KEY_ENV);
    }

    private static String getSystemPropertyOrEnv(String propertyKey,
            String envName) {
        String env = System.getenv(envName);
        String prop = System.getProperty(propertyKey);
        return (prop != null) ? prop : env;
    }

    /**
     * Sauce Labs specific remote webdriver capabilities
     *
     */
    public interface CapabilityType {
        String NAME = "name";

    }
}
