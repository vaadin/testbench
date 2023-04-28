/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.testbench.AbstractBrowserDriverTestBase;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

/**
 * Base class for TestBench 9+ tests. All TB9+ tests in the project should
 * extend this class.
 *
 * @author Vaadin Ltd
 */
@Execution(ExecutionMode.CONCURRENT)
public abstract class AbstractTB9Test extends AbstractBrowserDriverTestBase {

    /**
     * Height of the screenshots we want to capture
     */
    public static final int SCREENSHOT_HEIGHT = 850;

    /**
     * Width of the screenshots we want to capture
     */
    public static final int SCREENSHOT_WIDTH = 1500;
    private static final String HOSTNAME_PROPERTY = "deployment.hostname";
    private static final String PORT_PROPERTY = "deployment.port";
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

    /**
     * Opens the given test (defined by {@link #getTestUrl()}.
     */
    protected void openTestURL() {
        openTestURL("");
    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()} by adding the
     * given parameters.
     */
    protected void openTestURL(String extraParameters) {
        String url = getTestUrl();
        if (url.contains("?")) {
            url = url + "&" + extraParameters;
        } else {
            url = url + "?" + extraParameters;
        }
        getDriver().get(url);
    }

    /**
     * Returns the full URL to be used for the test
     *
     * @return the full URL for the test
     */
    protected String getTestUrl() {
        String baseUrl = getBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        return baseUrl + getDeploymentPath();
    }

    /**
     * Returns the path that should be used for the test. The path contains the
     * full path (appended to hostname+port) and must start with a slash.
     *
     * @return The URL path to the UI class to test
     */
    protected String getDeploymentPath() {
        return "/" + getTestView().getSimpleName();
    }

    /**
     * Used to determine what URL to initially open for the test
     *
     * @return The base URL for the test. Does not include a trailing slash.
     */
    protected String getBaseURL() {
        return "http://" + getDeploymentHostname() + ":" + getDeploymentPort();
    }

    protected String getDeploymentHostname() {
        String hostName = "localhost";
        String hostNameProperty = System.getProperty(HOSTNAME_PROPERTY);
        if (hostNameProperty != null && !"".equals(hostNameProperty)) {
            hostName = hostNameProperty;
        }
        return hostName;
    }

    protected int getDeploymentPort() {
        int port = 8080;
        String portProperty = System.getProperty(PORT_PROPERTY);
        if (portProperty != null && !"".equals(portProperty)) {
            port = Integer.parseInt(portProperty);
        }
        return port;
    }

    protected abstract Class<? extends Component> getTestView();

    public static boolean isConfiguredForSauceLabs() {
        return SauceLabsIntegration.isConfiguredForSauceLabs();
    }

    @BeforeEach
    public void before(TestInfo testInfo) {
        Logger logger = LoggerFactory.getLogger(AbstractTB9Test.class);

        String name = testInfo.getTestClass().get() + " / "
                + testInfo.getTestMethod().get();

        logger.info("Starting test " + name);
    }

    @AfterEach
    public void after(TestInfo testInfo) {
        Logger logger = LoggerFactory.getLogger(AbstractTB9Test.class);

        String name = testInfo.getTestClass().get() + " / "
                + testInfo.getTestMethod().get();

        logger.info("Ending test " + name);

    }
}
