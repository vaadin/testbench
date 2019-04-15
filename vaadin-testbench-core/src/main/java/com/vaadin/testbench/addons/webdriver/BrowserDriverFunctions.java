package com.vaadin.testbench.addons.webdriver;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.github.webdriverextensions.internal.junitrunner.DriverPathLoader;
import com.vaadin.testbench.addons.webdriver.conf.WebdriversConfig;
import com.vaadin.testbench.addons.webdriver.conf.WebdriversConfigFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static com.vaadin.testbench.PropertiesResolver.readProperties;
import static java.util.stream.Collectors.toSet;

public interface BrowserDriverFunctions {

    String BROWSER_NAME = "browserName";
    String PLATFORM = "platform";
    String UNITTESTING = "unittesting";
    String ENABLE_VNC = "enableVNC";
    String VERSION = "version";
    String ENABLE_VIDEO = "enableVideo";
    String PROJECT = "project";
    String TAGS = "tags";

    String SELENIUM_GRID_PROPERTIES_LOCAL_IP = "local-ip";
    String SELENIUM_GRID_PROPERTIES_LOCAL_BROWSER = "local";
    String SELENIUM_GRID_PROPERTIES_NO_GRID = "nogrid";

    String CONFIG_FOLDER = ".testbenchextensions/";

    static Optional<WebDriver> localWebDriverInstance(DesiredCapabilities dc) {
        String browserType = dc.getBrowserName();
        DriverPathLoader.loadDriverPaths(null);

        if (browserType == null || browserType.isEmpty()) {
            return Optional.empty();
        }

        switch (browserType) {
            case BrowserType.FIREFOX: return Optional.of(new FirefoxDriver());
            case BrowserType.CHROME: return Optional.of(new ChromeDriver(new ChromeOptions().merge(dc)));
            case BrowserType.SAFARI: return Optional.of(new SafariDriver());
            case BrowserType.OPERA: return Optional.of(new OperaDriver());
            case BrowserType.OPERA_BLINK: return Optional.of(new OperaDriver());
            case BrowserType.IE: return Optional.of(new InternetExplorerDriver());
        }

        return Optional.empty();
    }

    static WebDriver remoteWebDriverInstance(DesiredCapabilities desiredCapability,
                                                              final String ip) {
        //      Logger
//          .getLogger(BrowserDriverFunctions.class)
//          .info("Create RemoteWebdriver to " + ip + " for browser: " + desiredCapability);
        try {
            return new RemoteWebDriver(new URL(ip), desiredCapability);
        } catch (MalformedURLException e) {
            throw new RuntimeException("No webdriver was created...", e);
        }
    }

    static Stream<WebDriver> webDriverInstances(List<BrowserTypes> disabledBrowserTypes) {
        return readConfig()
                .getGridConfigs()
                .stream()
                .flatMap(gridConfig -> gridConfig
                        .getDesiredCapabilities()
                        .stream()
                        .map(dc -> new WebDriverSpec(
                                gridConfig.getTarget().equals(SELENIUM_GRID_PROPERTIES_LOCAL_BROWSER),
                                dc,
                                gridConfig.getTarget()
                        ))
                )
                .filter(spec -> {
                    final DesiredCapabilities desiredCapabilities = spec.getCapabilities();
                    final String browserName = desiredCapabilities.getBrowserName();
                    return disabledBrowserTypes
                            .stream()
                            .filter((type) -> type.browserName().equals(browserName))
                            .collect(toSet())
                            .isEmpty();
                })
                .map(BrowserDriverFunctions::createWebDriverInstance);
    }

    static WebDriver createWebDriverInstance(WebDriverSpec spec) {
        return spec.isLocal()
                ? localWebDriverInstance(spec.getCapabilities()).get()
                : remoteWebDriverInstance(spec.getCapabilities(), spec.getDriver());
    }

    static WebdriversConfig readConfig() {
        final Properties configProperties =
                readProperties(CONFIG_FOLDER + "config");
        return new WebdriversConfigFactory().createFromProperies(configProperties);
    }

    class WebDriverSpec {

        private final Boolean local;
        private final DesiredCapabilities capabilities;
        private final String driver;

        public WebDriverSpec(Boolean local, DesiredCapabilities capabilities, String browser) {

            this.local = local;
            this.capabilities = capabilities;
            this.driver = browser;
        }

        public Boolean isLocal() {
            return local;
        }

        public DesiredCapabilities getCapabilities() {
            return capabilities;
        }

        public String getDriver() {
            return driver;
        }
    }
}
