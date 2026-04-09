/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.vaadin.testbench.TestBench;

/**
 * Static helper for load test integration tests.
 * <p>
 * Provides proxy configuration for k6 recording and deployment URL helpers.
 * When the system property {@code k6.proxy.host} is set (e.g.,
 * {@code k6.proxy.host=localhost:6000}), the browser is configured to route
 * traffic through a recording proxy, enabling automatic conversion of TestBench
 * tests to k6 load tests.
 */
public final class LoadTestItHelper {

    private static final String PROXY_HOST_PROPERTY = "k6.proxy.host";

    private LoadTestItHelper() {
    }

    /**
     * If proxy recording is enabled via the {@code k6.proxy.host} system
     * property, quits the given driver and returns a new proxy-configured
     * driver. Otherwise navigates the existing driver to the given URL.
     * <p>
     * Typical usage in a {@code @BeforeEach} method:
     *
     * <pre>
     * setDriver(LoadTestItHelper.openWithProxy(getDriver(), viewUrl));
     * </pre>
     *
     * @param currentDriver
     *            the current WebDriver instance
     * @param viewUrl
     *            the full URL to navigate to
     * @return the driver to use (either the original or a new proxy-configured
     *         one)
     */
    public static WebDriver openWithProxy(WebDriver currentDriver,
            String viewUrl) {
        String proxyHost = System.getProperty(PROXY_HOST_PROPERTY);
        WebDriver driver = currentDriver;
        if (proxyHost != null && !proxyHost.isEmpty()) {
            if (currentDriver != null) {
                currentDriver.quit();
            }
            driver = createProxyDriver(proxyHost);
        }
        driver.get(viewUrl);
        return driver;
    }

    /**
     * Creates a ChromeDriver configured with proxy settings for k6 recording.
     */
    private static WebDriver createProxyDriver(String proxyHost) {
        ChromeOptions options = new ChromeOptions();

        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyHost);
        proxy.setSslProxy(proxyHost);
        options.setProxy(proxy);

        // Required for MITM proxy to work with HTTPS
        options.addArguments("--ignore-certificate-errors");
        // Force localhost traffic through proxy (don't bypass loopback)
        options.addArguments("--proxy-bypass-list=<-loopback>");
        options.setAcceptInsecureCerts(true);

        return TestBench.createDriver(new ChromeDriver(options));
    }

    /**
     * Returns the URL to the root of the server, e.g. "http://localhost:8888"
     *
     * @return the URL to the root
     */
    public static String getRootURL() {
        return "http://" + getDeploymentHostname() + ":" + getDeploymentPort();
    }

    /**
     * Returns the hostname of the deployment under test. If the environment
     * variable {@code HOSTNAME} is set (e.g., on CI), that value is used;
     * otherwise defaults to {@code localhost}.
     *
     * @return the host name
     */
    public static String getDeploymentHostname() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname != null && !hostname.isEmpty()) {
            return hostname;
        }
        return "localhost";
    }

    /**
     * Returns the port of the deployment under test. Configurable via the
     * system property {@code server.port}; defaults to {@code 8080}.
     *
     * @return the port number
     */
    public static int getDeploymentPort() {
        String port = System.getProperty("server.port");
        if (port != null && !port.isEmpty()) {
            return Integer.parseInt(port);
        }
        return 8080;
    }
}
