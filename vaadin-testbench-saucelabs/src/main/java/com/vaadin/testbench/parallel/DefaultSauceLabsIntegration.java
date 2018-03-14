package com.vaadin.testbench.parallel;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.saucelabs.ci.sauceconnect.AbstractSauceTunnelManager;

/**
 * Integration methods for Sauce Labs testing used by {@link ParallelTest}
 *
 */
public class DefaultSauceLabsIntegration implements SauceLabsIntegration {
	private static final Logger logger = Logger.getLogger(DefaultSauceLabsIntegration.class.getName());

	private static final String SAUCE_USERNAME_ENV = "SAUCE_USERNAME";
	private static final String SAUCE_USERNAME_PROP = "sauce.user";
	private static final String SAUCE_ACCESS_KEY_ENV = "SAUCE_ACCESS_KEY";
	private static final String SAUCE_ACCESS_KEY_PROP = "sauce.sauceAccessKey";
	private static final String SAUCE_OPTIONS_ENV = "SAUCE_OPTIONS";
	private static final String SAUCE_OPTIONS_PROP = "sauce.options";

	/**
	 * Sets needed desired capabilities, mainly tunnel identifier, based on the
	 * given sauce.options String
	 * 
	 * @param desiredCapabilities
	 *            DesiredCapabilities for RemoteWebDriver. Must not be null.
	 * @param sauceOptions
	 *            options to be parsed and added as capabilities to the given
	 *            DesiredCapabilities object. If null, tries to parse from
	 *            sauce.options system property
	 */
	public void setDesiredCapabilities(DesiredCapabilities desiredCapabilities, String sauceOptions) {
		String options = sauceOptions;

		if (options == null || options.isEmpty()) {
			options = System.getProperty("sauce.options");
			if (options == null) {
				logger.log(Level.WARNING, "Null or empty sauce.options given. Ignoring.");
				return;
			}
		}
		String tunnelId = AbstractSauceTunnelManager.getTunnelIdentifier(options, null);
		if (tunnelId != null) {
			desiredCapabilities.setCapability("tunnelIdentifier", tunnelId);
		}
	}

	/**
	 * Returns the HubUrl for running tests in Sauce Labs tunnel. Reads required
	 * credentials from sauce.user and sauce.sauceAccessKey or environment variables
	 * SAUCE_USERNAME and SAUCE_ACCESS_KEY. If both system property and environment
	 * variable are defined, the system property is used.
	 * 
	 * @return url String to be used in Sauce Labs test run
	 */
	public String getHubUrl() {
		String username = getSauceUser();
		String accessKey = getSauceAccessKey();

		if (username == null) {
			logger.log(Level.FINE, "You can give a Sauce Labs user name using -D" + SAUCE_USERNAME_PROP
					+ "=<username> or by " + SAUCE_USERNAME_ENV + " environment variable.");
		}
		if (accessKey == null) {
			logger.log(Level.FINE, "You can give a Sauce Labs access key using -D" + SAUCE_ACCESS_KEY_PROP
					+ "=<accesskey> or by " + SAUCE_ACCESS_KEY_ENV + " environment variable.");
		}
		return "http://" + username + ":" + accessKey + "@localhost:4445/wd/hub";
	}

	static String getSauceUser() {
		return getSystemPropertyOrEnv(SAUCE_USERNAME_PROP, SAUCE_USERNAME_ENV);
	}

	static String getSauceAccessKey() {
		return getSystemPropertyOrEnv(SAUCE_ACCESS_KEY_PROP, SAUCE_ACCESS_KEY_ENV);
	}
	
	static String getSauceOptions() {
		return getSystemPropertyOrEnv(SAUCE_OPTIONS_PROP, SAUCE_OPTIONS_ENV);
	}

	private static String getSystemPropertyOrEnv(String propertyKey, String envName) {
		String env = System.getenv(envName);
		String prop = System.getProperty(propertyKey);
		return (prop != null) ? prop : env;
	}
}
