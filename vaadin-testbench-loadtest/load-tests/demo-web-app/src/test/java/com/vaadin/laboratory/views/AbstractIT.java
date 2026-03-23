package com.vaadin.laboratory.views;

import com.vaadin.testbench.BrowserTestBase;
import com.vaadin.testbench.TestBench;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Base class for all integration tests, allowing us to change the applicable
 * driver, test URL or other configurations in one place.
 * <p>
 * For k6 recording, set the system property {@code k6.proxy.host=localhost:6000}.
 * This will configure the browser to route traffic through a recording proxy.
 * <p>
 * TODO: The k6 proxy configuration code in this class is a temporary solution.
 * In the final implementation, this boilerplate will not be needed - either:
 * <ul>
 *   <li>AI coding assistants (like Claude Code) will learn to add it automatically, or</li>
 *   <li>This functionality will be built directly into TestBench itself</li>
 * </ul>
 * The challenge is that BrowserTestBase's @BeforeEach runs after JUnit extension
 * callbacks, so we cannot configure the proxy driver in an extension. The proxy
 * configuration must happen in a @BeforeEach method that runs after BrowserTestBase
 * sets its driver but before navigation occurs.
 */
public abstract class AbstractIT extends BrowserTestBase {

    private static final String PROXY_HOST_PROPERTY = "k6.proxy.host";

    /**
     * If running on CI, get the host name from environment variable HOSTNAME
     *
     * @return the host name
     */
    protected static String getDeploymentHostname() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname != null && !hostname.isEmpty()) {
            return hostname;
        }
        return "localhost";
    }

    /**
     * Get the deployment port, configurable via system property 'server.port'
     *
     * @return the port number
     */
    protected static int getDeploymentPort() {
        String port = System.getProperty("server.port");
        if (port != null && !port.isEmpty()) {
            return Integer.parseInt(port);
        }
        return 8080;
    }

    @BeforeEach
    public void open() {
        // Check if proxy recording is enabled
        String proxyHost = System.getProperty(PROXY_HOST_PROPERTY);
        if (proxyHost != null && !proxyHost.isEmpty()) {
            // Replace the default driver with a proxy-configured one
            WebDriver defaultDriver = getDriver();
            if (defaultDriver != null) {
                defaultDriver.quit();
            }
            setDriver(createProxyDriver(proxyHost));
        }

        getDriver().get("http://" + getDeploymentHostname() + ":" + getDeploymentPort() + "/" + getViewName());
    }

    /**
     * Creates a ChromeDriver configured with proxy settings for k6 recording.
     */
    private WebDriver createProxyDriver(String proxyHost) {
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

    abstract public String getViewName();
}
