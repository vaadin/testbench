package com.vaadin.testbench.parallel;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Integration methods for Sauce Labs testing used by {@link ParallelTest}
 *
 */
public class SauceLabsIntegration {
    private static final Logger logger = Logger
            .getLogger(SauceLabsIntegration.class.getName());

    private static final String SAUCE_USERNAME_ENV = "SAUCE_USERNAME";
    private static final String SAUCE_USERNAME_PROP = "sauce.user";
    private static final String SAUCE_ACCESS_KEY_ENV = "SAUCE_ACCESS_KEY";
    private static final String SAUCE_ACCESS_KEY_PROP = "sauce.sauceAccessKey";

    /**
     * Sets needed desired capabilities, mainly tunnel identifier, based on the
     * given sauce.options String.
     * 
     * @param desiredCapabilities
     *            DesiredCapabilities for RemoteWebDriver. Must not be null.
     * @param sauceOptions
     *            options to be parsed and added as capabilities to the given
     *            DesiredCapabilities object
     */
    static void setDesiredCapabilities(
            DesiredCapabilities desiredCapabilities) {
        String sauceOptions = System.getProperty("sauce.options");
        if (sauceOptions == null || sauceOptions.isEmpty()) {
            logger.log(Level.WARNING,
                    "Null or empty sauce.options given. Ignoring.");
            return;
        }
        String tunnelId = getTunnelIdentifier(sauceOptions, null);
        if (tunnelId != null) {
            desiredCapabilities.setCapability("tunnelIdentifier", tunnelId);
        }
    }

    /**
     * @param options
     *            the command line options used to launch Sauce Connect
     * @param defaultValue
     *            the default value to use for the identifier if none specified
     *            in the options
     * @return String representing the tunnel identifier
     */
    static String getTunnelIdentifier(String options, String defaultValue) {
        if (options == null || options.isEmpty()) {
            return defaultValue;
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
        return defaultValue;
    }

    /**
     * Returns the HubUrl for running tests in Sauce Labs tunnel. Reads required
     * credentials from sauce.user and sauce.sauceAccessKey or environment
     * variables SAUCE_USERNAME and SAUCE_ACCESS_KEY. If both system property
     * and environment variable are defined, the system property is used.
     * 
     * @return url String to be used in Sauce Labs test run
     */
    static String getHubUrl() {
        String username = getSauceUser();
        String accessKey = getSauceAccessKey();

        if (username == null) {
            logger.log(Level.FINE,
                    "You can give a Sauce Labs user name using -D"
                            + SAUCE_USERNAME_PROP + "=<username> or by "
                            + SAUCE_USERNAME_ENV + " environment variable.");
        }
        if (accessKey == null) {
            logger.log(Level.FINE,
                    "You can give a Sauce Labs access key using -D"
                            + SAUCE_ACCESS_KEY_PROP + "=<accesskey> or by "
                            + SAUCE_ACCESS_KEY_ENV + " environment variable.");
        }
        return "http://" + username + ":" + accessKey
                + "@localhost:4445/wd/hub";
    }

    static boolean isConfiguredForSauceLabs() {
        return getSauceUser() != null && getSauceAccessKey() != null;
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
