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
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;


import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Integration methods for Sauce Labs testing.
 *
 */
public class SauceLabsIntegration {
    private static final Logger logger = Logger
            .getLogger(SauceLabsIntegration.class.getName());

    private static final String SAUCE_DEFAULT_HUB_URL = "https://ondemand.us-west-1.saucelabs.com/wd/hub";
    private static final String SAUCE_USERNAME_ENV = "SAUCE_USERNAME";
    private static final String SAUCE_USERNAME_PROP = "sauce.user";
    private static final String SAUCE_ACCESS_KEY_ENV = "SAUCE_ACCESS_KEY";
    private static final String SAUCE_ACCESS_KEY_PROP = "sauce.sauceAccessKey";
    private static final String SAUCE_TUNNELID_PROP = "sauce.tunnelId";
    private static final String SAUCE_TUNNELID_ENV = "SAUCE_TUNNEL_ID";
    private static final String SAUCE_HUB_URL_PROP = "sauce.hubUrl";
    private static final String SAUCE_HUB_URL_ENV = "SAUCE_HUB_URL";

    /**
     * Sets needed desired capabilities for authentication and using the correct
     * sauce tunnel (if in use).
     * <p>
     *
     * @see #getSauceUser()
     * @see #getSauceAccessKey()
     * @see #getSauceTunnelIdentifier()
     *
     * @param desiredCapabilities
     *            the capabilities object to populate, not null
     */
    public static void setDesiredCapabilities(
            DesiredCapabilities desiredCapabilities) {

        String username = getSauceUser();
        String accessKey = getSauceAccessKey();
        String tunnelId = getSauceTunnelIdentifier();

        if (username != null) {
            setSauceLabsOption(desiredCapabilities, "username", username);
        } else {
            logger.log(Level.FINE,"You can give a Sauce Labs user name using -D"
                    + SAUCE_USERNAME_PROP + "=<username> or by "
                    + SAUCE_USERNAME_ENV + " environment variable.");
        }
        if (accessKey != null) {
            setSauceLabsOption(desiredCapabilities, "access_key", accessKey);
        } else {
            logger.log(Level.FINE,"You can give a Sauce Labs access key using -D"
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
     * <p>
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
        // We always make a copy of the options because all clone/merge
        // operations in
        // DesiredCapability do a shallow clone
        Map<String, Object> sauceOptions = new HashMap<>();
        Map<String, Object> currentOptions = getSauceLabsOptions(
                desiredCapabilities);
        if (currentOptions != null) {
            sauceOptions.putAll(currentOptions);
        }
        sauceOptions.put(key, value);

        desiredCapabilities.setCapability("sauce:options", sauceOptions);
    }

    private static Map<String, Object> getSauceLabsOptions(
            DesiredCapabilities desiredCapabilities) {
        return (Map<String, Object>) desiredCapabilities
                .getCapability("sauce:options");
    }

    /**
     * Gets the given SauceLabs option.
     * <p>
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
        Map<String, Object> sauceOptions = getSauceLabsOptions(
                desiredCapabilities);
        if (sauceOptions == null) {
            return null;
        }
        return sauceOptions.get(key);
    }

    /**
     * Gets the configured Saucelabs tunnel identifier.
     * <p>
     * Reads from the {@value #SAUCE_TUNNELID_PROP} system property or the
     * {@value #SAUCE_TUNNELID_ENV} environment variable.
     * <p>
     * If both system property and environment variable are defined, the system
     * property is used.
     *
     * @return the configured Saucelabs tunnel identifier or null
     */
    public static String getSauceTunnelIdentifier() {
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
     * <p>
     * The available SauceLabs URLs are listed at
     * https://docs.saucelabs.com/basics/data-center-endpoints/#data-center-endpoints.
     *
     * @return url String to be used in Sauce Labs test run
     */
    public static String getHubUrl() {
        String hubUrl = getSystemPropertyOrEnv(SAUCE_HUB_URL_PROP,
                SAUCE_HUB_URL_ENV);
        if (hubUrl == null) {
            hubUrl = SAUCE_DEFAULT_HUB_URL;
        }
        return hubUrl;
    }

    /**
     * Checks if parameters needed to run in Saucelabs have been set.
     *
     * @return true if the Saucelabs configuration was found
     */
    public static boolean isConfiguredForSauceLabs() {
        String user = getSauceUser();
        String accessKey = getSauceAccessKey();
        return user != null && !user.isEmpty() && accessKey != null
                && !accessKey.isEmpty();
    }

    /**
     * Gets the configured Saucelabs user name.
     * <p>
     * Reads from the {@value #SAUCE_USERNAME_PROP} system property or the
     * {@value #SAUCE_USERNAME_ENV} environment variable.
     * <p>
     * If both system property and environment variable are defined, the system
     * property is used.
     *
     * @return the configured Saucelabs user name or null
     */
    public static String getSauceUser() {
        return getSystemPropertyOrEnv(SAUCE_USERNAME_PROP, SAUCE_USERNAME_ENV);
    }

    /**
     * Gets the configured Saucelabs access key.
     * <p>
     * Reads from the {@value #SAUCE_ACCESS_KEY_PROP} system property or the
     * {@value #SAUCE_ACCESS_KEY_ENV} environment variable.
     * <p>
     * If both system property and environment variable are defined, the system
     * property is used.
     *
     * @return the configured Saucelabs access key or null
     */
    public static String getSauceAccessKey() {
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
