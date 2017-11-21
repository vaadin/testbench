/*
 * Copyright 2000-2013 Vaadind Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

import com.saucelabs.ci.sauceconnect.AbstractSauceTunnelManager;
import com.vaadin.testbench.annotations.BrowserFactory;
import com.vaadin.testbench.annotations.RunOnHub;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Provides values for parameters which depend on where the test is run.
 * Parameters should be configured in work/eclipse-run-selected-test.properties.
 * A template is available in uitest/.
 *
 * @author Vaadin Ltd
 */

@BrowserFactory(VaadinBrowserFactory.class)
@RunOnHub
public abstract class PrivateTB3Configuration extends AbstractTB3Test {
    private static final String HOSTNAME_PROPERTY = "deployment.hostname";
    private static final String PORT_PROPERTY = "deployment.port";
    public static final String CHROME_PATH_PROPERTY = "chrome.path";
    public static final String FIREFOX_PATH_PROPERTY = "firefox.path";
    public static final String FIREFOX_PROFILE_PATH_PROPERTY = "firefox.profile.path";
    private static final Properties properties = new Properties();
    private static final File propertiesFile = new File("local.properties");
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
    }

    @Override
    protected String getHubURL() {
        String username = System.getProperty("sauce.user");
        String accessKey = System.getProperty("sauce.sauceAccessKey");

        if (username == null) {
            throw new IllegalArgumentException(
                    "You must give a Sauce Labs user name using -Dsauce.user=<username> or by adding sauce.user=<username> to local.properties");
        }
        if (accessKey == null) {
            throw new IllegalArgumentException(
                    "You must give a Sauce Labs access key using -Dsauce.sauceAccessKey=<accesskey> or by adding sauce.sauceAccessKey=<accesskey> to local.properties");
        }
        return "http://" + username + ":" + accessKey
                + "@localhost:4445/wd/hub";
    }

    @Override
    public void setDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        String tunnelId = AbstractSauceTunnelManager.getTunnelIdentifier(
                System.getProperty("sauce.options"), null);
        if (tunnelId != null) {
            desiredCapabilities.setCapability("tunnelIdentifier", tunnelId);
        }
        super.setDesiredCapabilities(desiredCapabilities);
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

    /**
     * Gets the hostname that tests are configured to use.
     *
     * @return the host name configuration value
     */
    public static String getConfiguredDeploymentHostname() {
        String hostName = getProperty(HOSTNAME_PROPERTY);

        if (hostName == null || "".equals(hostName)) {
            hostName = "localhost";
        }

        return hostName;
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
}
