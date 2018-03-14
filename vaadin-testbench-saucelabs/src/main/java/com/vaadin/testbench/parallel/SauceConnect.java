package com.vaadin.testbench.parallel;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.saucelabs.ci.sauceconnect.SauceConnectFourManager;
import com.saucelabs.ci.sauceconnect.SauceTunnelManager;

/**
 * SauceConnect handling. Originally from
 * https://github.com/saucelabs/sauce-java/tree/master/sauce-connect-plugin
 */
public class SauceConnect {

	private static final Logger logger = Logger.getLogger(SauceConnect.class.getName());

	/**
	 * @parameter property="{sauce.user}"
	 */
	private String sauceUsername;

	/**
	 * @parameter property="{sauce.sauceAccessKey}"
	 */
	private String sauceAccessKey;

	private int port = 4445;

	/**
	 * @parameter property="{sauce.options}
	 */
	private String options;

	private SauceTunnelManager sauceTunnelManager;

	void openConnection() {
		sauceUsername = DefaultSauceLabsIntegration.getSauceUser();
		sauceAccessKey = DefaultSauceLabsIntegration.getSauceAccessKey();
		options = DefaultSauceLabsIntegration.getSauceOptions();

		logger.info("Starting Sauce Connect");
		if (sauceUsername == null || sauceUsername.equals("")) {
			logger.log(Level.SEVERE, "Sauce username not specified");
			return;
		}
		if (sauceAccessKey == null || sauceAccessKey.equals("")) {
			logger.log(Level.SEVERE, "Sauce access key not specified");
			return;
		}
		sauceTunnelManager = new SauceConnectFourManager(false);
		try {
			sauceTunnelManager.openConnection(sauceUsername, sauceAccessKey, port, null, options, null, true, null);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error generated when launching Sauce Connect", e);
		}
	}

	void closeConnection() {
		logger.info("Stopping Sauce Connect");

		if (sauceTunnelManager == null) {
			// no process available
			logger.log(Level.WARNING, "Unable to find Sauce Connect Manager instance");
		} else {
			// close running process
			sauceTunnelManager.closeTunnelsForPlan(this.sauceUsername, options, null);
			logger.info("Sauce Connect stopped");
		}

	}

}