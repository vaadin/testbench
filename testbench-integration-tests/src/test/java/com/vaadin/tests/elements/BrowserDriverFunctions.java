package com.vaadin.tests.elements;

import static org.rapidpm.frp.StringFunctions.notEmpty;
import static org.rapidpm.frp.StringFunctions.notStartsWith;
import static org.rapidpm.frp.Transformations.not;
import static org.rapidpm.frp.matcher.Case.match;
import static org.rapidpm.frp.matcher.Case.matchCase;
import static org.rapidpm.frp.model.Result.success;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.rapidpm.frp.Transformations;
import org.rapidpm.frp.functions.CheckedSupplier;
import org.rapidpm.frp.functions.QuadFunction;
import org.rapidpm.frp.model.Result;

import com.vaadin.testbench.TestBench;

/**
 * l
 * Created by svenruppert on 25.04.17.
 */
public interface BrowserDriverFunctions {

    Function<String, Result<WebDriver>> webdriver = browserType -> match(
        matchCase(() -> success(new PhantomJSDriver())),
        matchCase(browserType::isEmpty, () -> Result.failure("browserTape should not be emtpy")),
        matchCase(() -> browserType.equals(BrowserType.PHANTOMJS), () -> success(new PhantomJSDriver())),
        matchCase(() -> browserType.equals(BrowserType.FIREFOX), () -> success(new FirefoxDriver())),
        matchCase(() -> browserType.equals(BrowserType.CHROME), () -> success(new ChromeDriver())),
        matchCase(() -> browserType.equals(BrowserType.SAFARI), () -> success(new SafariDriver())),
        matchCase(() -> browserType.equals(BrowserType.IE), () -> success(new InternetExplorerDriver())));

    Function<String, Result<DesiredCapabilities>> desiredCapabilities = (browsertype) ->
        match(
            matchCase(() -> success(DesiredCapabilities.phantomjs())),
            matchCase(browsertype::isEmpty, () -> Result.failure("browsertype should not be empty")),
            matchCase(() -> browsertype.equals(BrowserType.PHANTOMJS), () -> success(DesiredCapabilities.phantomjs())),
            matchCase(() -> browsertype.equals(BrowserType.FIREFOX), () -> {
                          DesiredCapabilities firefox = DesiredCapabilities.firefox();
                          firefox.setCapability(FirefoxDriver.MARIONETTE, false);
                          return success(firefox);
                      }
            ),
            matchCase(() -> browsertype.equals(BrowserType.CHROME), () -> success(DesiredCapabilities.chrome())),
            matchCase(() -> browsertype.equals(BrowserType.SAFARI), () -> success(DesiredCapabilities.safari())),
            matchCase(() -> browsertype.equals(BrowserType.IE), () -> success(DesiredCapabilities.internetExplorer())));

    QuadFunction<Supplier<String>, Supplier<Platform>, Supplier<Boolean>, Supplier<String>, Result<WebDriver>> webDriver
        = (browserType, platform, runningLocal, seleniumHubIP) ->
        match(
            matchCase(() -> {
                // initSystemProperties(); //TODO right time to set the path to the driver
                return webdriver.apply(browserType.get());
            }),
            matchCase(() -> !runningLocal.get(), () -> {
                final Result<DesiredCapabilities> capabilities = desiredCapabilities.apply(browserType.get());
                capabilities.bind(
                    success -> success.setPlatform(platform.get()),
                    error -> System.out.println("error to log = " + error));

                return match(
                    matchCase(() -> {
                        final DesiredCapabilities desiredCapabilities = capabilities.get();
                        try {
                            final URL url = new URL("http://" + seleniumHubIP.get() + ":4444/wd/hub");
                            final RemoteWebDriver remoteWebDriver = new RemoteWebDriver(url, desiredCapabilities);
                            final WebDriver webDriver = TestBench.createDriver(remoteWebDriver);
                            return success(webDriver);
                        } catch (MalformedURLException e) {
                            return Result.failure("url not correct " + e.getMessage());
                        }
                    }),
                    matchCase(() -> !capabilities.isPresent(), () -> Result.failure("capabilities are absent")));
            }));

    static void initSystemProperties() {
        final String pointToStartFrom = new File("").getAbsolutePath();
        final String DATA_DRIVER_BASE_FOLDER = "/_data/driver/";
        final String OS = "osx";
        String basePath = pointToStartFrom + DATA_DRIVER_BASE_FOLDER + OS;
        System.setProperty("webdriver.chrome.driver", basePath + "/chrome/chromedriver");
        System.setProperty("webdriver.gecko.driver", basePath + "/gecko/geckodriver");
        System.setProperty("phantomjs.binary.path", basePath + "/phantomjs/phantomjs");
    }

    Supplier<String> ipSupplierLocalIP = () -> {
        final CheckedSupplier<Enumeration<NetworkInterface>> checkedSupplier = NetworkInterface::getNetworkInterfaces;

        return Transformations.<NetworkInterface>enumToStream()
            .apply(checkedSupplier.getOrElse(Collections::emptyEnumeration))
            .map(NetworkInterface::getInetAddresses)
            .flatMap(iaEnum -> Transformations.<InetAddress>enumToStream().apply(iaEnum))
            .filter(inetAddress -> inetAddress instanceof Inet4Address)
            .filter(not(InetAddress::isMulticastAddress))
            .map(InetAddress::getHostAddress)
            .filter(notEmpty())
            .filter(adr -> notStartsWith().apply(adr, "127"))
            .filter(adr -> notStartsWith().apply(adr, "169.254"))
            .filter(adr -> notStartsWith().apply(adr, "255.255.255.255"))
            .filter(adr -> notStartsWith().apply(adr, "255.255.255.255"))
            .filter(adr -> notStartsWith().apply(adr, "0.0.0.0"))
            //            .filter(adr -> range(224, 240).noneMatch(nr -> adr.startsWith(valueOf(nr))))
            .findFirst().orElse("localhost");
    };

}
