/*
 * Copyright 2000-2014 Vaadin Ltd.
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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.flow.component.Component;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.BrowserFactory;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.ParallelTestJUnit5;

/**
 * Base class for TestBench 6+ tests. All TB6+ tests in the project should
 * extend this class.
 * <p>
 * Sub classes can, but typically should not, restrict the browsers used by
 * overriding the {@link #getBrowserConfiguration()} method:
 *
 * <pre>
 * &#064;Override
 * &#064;BrowserConfiguration
 * public List&lt;DesiredCapabilities&gt; getBrowserConfiguration() {
 * }
 * </pre>
 *
 * @author Vaadin Ltd
 */
@BrowserFactory(TB6TestBrowserFactory.class)
public abstract class AbstractTB6TestJUnit5 extends ParallelTestJUnit5 {

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

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        return Arrays.asList(BrowserUtil.firefox(), BrowserUtil.chrome(),
                BrowserUtil.safari(), BrowserUtil.edge());
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
        driver.get(url);
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
}
