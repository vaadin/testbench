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
package com.vaadin.tests.elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.BrowserFactory;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.Browser;

/**
 * Provides values for parameters which depend on where the test is run.
 * Parameters should be configured in work/eclipse-run-selected-test.properties.
 * A template is available in uitest/.
 *
 * @author Vaadin Ltd
 */

@BrowserFactory(VaadinBrowserFactory.class)
@RunOnHub("tb3-hub.intra.itmill.com")
public abstract class PrivateTB3Configuration extends AbstractTB3Test {
    private static final String HOSTNAME_PROPERTY = "deployment.hostname";
    private static final String RUN_LOCALLY_PROPERTY = "com.vaadin.testbench.runLocally";
    private static final String ALLOW_RUN_LOCALLY_PROPERTY = "com.vaadin.testbench.allowRunLocally";
    private static final String PORT_PROPERTY = "deployment.port";
    public static final String CHROME_PATH_PROPERTY = "chrome.path";
    public static final String FIREFOX_PATH_PROPERTY = "firefox.path";
    public static final String FIREFOX_PROFILE_PATH_PROPERTY = "firefox.profile.path";
    private static final Properties properties = new Properties();
    private static final File propertiesFile = new File("config",
            "testbench.properties");
    static {
        if (propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(propertiesFile));
                Enumeration e = properties.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    System.setProperty(key, properties.getProperty(key));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (properties.containsKey(RUN_LOCALLY_PROPERTY)) {
            System.setProperty("useLocalWebDriver", "true");
            DesiredCapabilities localBrowser = getRunLocallyCapabilities();
            System.setProperty("browsers.include",
                    localBrowser.getBrowserName() + localBrowser.getVersion());
        }
    }

    protected static DesiredCapabilities getRunLocallyCapabilities() {
        VaadinBrowserFactory factory = new VaadinBrowserFactory();

        try {
            if (properties.containsKey(RUN_LOCALLY_PROPERTY)) {
                // RunLocally defined in propeties file
                return factory.create(Browser
                        .valueOf(properties.getProperty(RUN_LOCALLY_PROPERTY)
                                .toUpperCase(Locale.ROOT)));
            } else if (System.getProperties().containsKey("browsers.include")) {
                // Use first included browser as the run locally browser.
                String property = System.getProperty("browsers.include");
                String firstBrowser = property.split(",")[0];

                return factory.create(Browser.valueOf(firstBrowser
                        .replaceAll("[0-9]+$", "").toUpperCase(Locale.ROOT)));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Falling back to FireFox");
        }
        return factory.create(Browser.FIREFOX);
    }

    public static String getProperty(String name) {
        String property = properties.getProperty(name);
        if (property == null) {
            property = System.getProperty(name);
        }
        return property;
    }

    @Override
    protected String getDeploymentHostname() {
        if (getRunLocallyBrowser() != null) {
            return "localhost";
        }
        return getConfiguredDeploymentHostname();
    }

    protected boolean isRunLocally() {
        if (properties.containsKey(RUN_LOCALLY_PROPERTY)) {
            return true;
        }

        if (properties.containsKey(ALLOW_RUN_LOCALLY_PROPERTY)
                && properties.get(ALLOW_RUN_LOCALLY_PROPERTY).equals("true")
                && getClass().getAnnotation(RunLocally.class) != null) {
            return true;
        }

        return false;
    }

    /**
     * Gets the hostname that tests are configured to use.
     *
     * @return the host name configuration value
     */
    public static String getConfiguredDeploymentHostname() {
        String hostName = getProperty(HOSTNAME_PROPERTY);

        if (hostName == null || "".equals(hostName)) {
            hostName = findAutoHostname();
        }

        return hostName;
    }

    @Override
    protected String getBaseURL() {
        if (isRunLocally()) {
            return "http://localhost:8080";
        }
        return super.getBaseURL();
    }

    @Override
    protected int getDeploymentPort() {
        return getConfiguredDeploymentPort();
    }

    /**
     * Gets the port that tests are configured to use.
     *
     * @return the port configuration value
     */
    public static int getConfiguredDeploymentPort() {
        String portString = getProperty(PORT_PROPERTY);

        int port = 8080;
        if (portString != null && !"".equals(portString)) {
            port = Integer.parseInt(portString);
        }

        return port;
    }

    /**
     * Tries to automatically determine the IP address of the machine the test
     * is running on.
     *
     * @return An IP address of one of the network interfaces in the machine.
     * @throws RuntimeException
     *             if there was an error or no IP was found
     */
    private static String findAutoHostname() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nwInterface = interfaces.nextElement();
                if (!nwInterface.isUp() || nwInterface.isLoopback()
                        || nwInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = nwInterface
                        .getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isLoopbackAddress()) {
                        continue;
                    }
                    if (address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Could not enumerate ");
        }

        throw new RuntimeException(
                "No compatible (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16) ip address found.");
    }
}
