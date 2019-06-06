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

import com.vaadin.testbench.configuration.LocalTarget;
import com.vaadin.testbench.configuration.RemoteTarget;
import com.vaadin.testbench.configuration.Target;
import com.vaadin.testbench.configuration.TestConfiguration;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static com.vaadin.testbench.TestBenchLogger.logger;
import static java.util.stream.Collectors.toSet;
import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY;
import static org.openqa.selenium.firefox.GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY;
import static org.openqa.selenium.ie.InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY;
import static org.openqa.selenium.opera.OperaDriverService.OPERA_DRIVER_EXE_PROPERTY;

public interface BrowserDriverFunctions {

    String ENABLE_VNC = "enableVNC";
    String ENABLE_VIDEO = "enableVideo";
    String PROJECT = "project";

    static WebDriver createDriver(Target target) {
        if (target.isLocal()) {
            final LocalTarget localTarget = (LocalTarget) target;
            return createLocalDriver(target.getDesiredCapabilities(),
                    localTarget.getBrowserOptions(), localTarget.getDriverPath());
        } else {
            return createRemoteDriver(target.getDesiredCapabilities(), ((RemoteTarget) target).getHubUrl());
        }
    }

    static WebDriver createLocalDriver(DesiredCapabilities dc, MutableCapabilities options, String binaryPath) {
        final String browserType = dc.getBrowserName();
        if (browserType == null || browserType.isEmpty()) {
            return null;
        }

        switch (browserType) {
            case BrowserType.FIREFOX:
                System.setProperty(GECKO_DRIVER_EXE_PROPERTY, binaryPath);
                return new FirefoxDriver((FirefoxOptions) options.merge(dc));
            case BrowserType.CHROME:
                System.setProperty(CHROME_DRIVER_EXE_PROPERTY, binaryPath);
                return new ChromeDriver((ChromeOptions) options.merge(dc));
            case BrowserType.SAFARI:
                return new SafariDriver((SafariOptions) options.merge(dc));
            case BrowserType.OPERA_BLINK:
                System.setProperty(OPERA_DRIVER_EXE_PROPERTY, binaryPath);
                return new OperaDriver((OperaOptions) options.merge(dc));
            case BrowserType.IE:
                System.setProperty(IE_DRIVER_EXE_PROPERTY, binaryPath);
                return new InternetExplorerDriver((InternetExplorerOptions) options.merge(dc));
            default:
                return null;
        }
    }

    static WebDriver createRemoteDriver(DesiredCapabilities desiredCapability,
                                        String ip) {
        logger().debug("Creating RemoteWebdriver to " + ip + " for browser: " + desiredCapability);

        try {
            return new RemoteWebDriver(new URL(ip), desiredCapability);
        } catch (MalformedURLException e) {
            throw new RuntimeException("No webdriver was created", e);
        }
    }

    static Stream<WebDriver> createDrivers(TestConfiguration testConfiguration,
                                           Collection<com.vaadin.testbench.addons.webdriver.BrowserType> disabledBrowsers) {
        final Set<String> skippedBrowsers = disabledBrowsers.stream()
                .map(com.vaadin.testbench.addons.webdriver.BrowserType::browserName)
                .collect(toSet());

        return testConfiguration.getBrowserTargets()
                .stream()
                .filter(spec -> !skippedBrowsers.contains(spec.getDesiredCapabilities().getBrowserName()))
                .map(BrowserDriverFunctions::createDriver);
    }
}
