package com.vaadin.tests.elements.ng.tooling;

import static java.lang.System.setProperty;
import static org.rapidpm.frp.StringFunctions.notEmpty;
import static org.rapidpm.frp.StringFunctions.notStartsWith;
import static org.rapidpm.frp.Transformations.not;
import static org.rapidpm.frp.matcher.Case.match;
import static org.rapidpm.frp.matcher.Case.matchCase;
import static org.rapidpm.frp.model.Result.success;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.rapidpm.frp.functions.CheckedExecutor;
import org.rapidpm.frp.functions.CheckedFunction;
import org.rapidpm.frp.functions.CheckedPredicate;
import org.rapidpm.frp.functions.CheckedSupplier;
import org.rapidpm.frp.functions.QuadFunction;
import org.rapidpm.frp.matcher.Case;
import org.rapidpm.frp.model.Result;

import com.google.gson.stream.JsonReader;
import com.vaadin.testbench.TestBench;

/**
 *
 *
 */
public interface BrowserDriverFunctions {
    Supplier<String> ipSupplierLocalIP = () -> {
        final CheckedSupplier<Enumeration<NetworkInterface>> checkedSupplier = NetworkInterface::getNetworkInterfaces;

        return Transformations.<NetworkInterface>enumToStream()
            .apply(checkedSupplier.getOrElse(Collections::emptyEnumeration))
            .filter((CheckedPredicate<NetworkInterface>) NetworkInterface::isUp)
            .map(NetworkInterface::getInetAddresses)
            .flatMap(iaEnum -> Transformations.<InetAddress>enumToStream().apply(iaEnum))
            .filter(inetAddress -> inetAddress instanceof Inet4Address)
            .filter(not(InetAddress::isMulticastAddress))
            .filter(not(InetAddress::isLoopbackAddress))
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

    CheckedFunction<String, Properties> readProperties = (filename) -> {
        try (
            final FileInputStream fis = new FileInputStream(new File(filename));
            final BufferedInputStream bis = new BufferedInputStream(fis)) {
            final Properties properties = new Properties();
            properties.load(bis);

            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    };

    CheckedExecutor readTestbenchProperties = () -> readProperties
        .apply("config/testbench.properties")
        .ifPresent(p -> p.forEach((key, value) -> setProperty((String) key, (String) value))
        );

    Function<String, Result<WebDriver>> localWebDriverInstance = browserType -> match(
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
            matchCase(() -> browsertype.equals(BrowserType.FIREFOX), () -> success(DesiredCapabilities.firefox())),
            matchCase(() -> browsertype.equals(BrowserType.CHROME), () -> success(DesiredCapabilities.chrome())),
            matchCase(() -> browsertype.equals(BrowserType.SAFARI), () -> success(DesiredCapabilities.safari())),
            matchCase(() -> browsertype.equals(BrowserType.IE), () -> success(DesiredCapabilities.internetExplorer())));

    Supplier<Result<List<DesiredCapabilities>>> readBrowserCombinations = () -> {
        final List<DesiredCapabilities> result = new ArrayList<>();
        final File file = new File("config/browser_combinations.json");
        try (
            final FileReader fr = new FileReader(file);
            final JsonReader reader = new JsonReader(fr)) {

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("browsers")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        String browser = "";
                        String version = "";
                        String platform = "";
                        final Map<String, String> noNameProps = new HashMap<>();
                        while (reader.hasNext()) {
                            String property = reader.nextName();
                            switch (property) {
                            case "browserName":
                                browser = reader.nextString();
                                break;
                            case "platform":
                                platform = reader.nextString();
                                break;
                            case "version":
                                version = reader.nextString();
                                break;
                            default:
                                noNameProps.put(property, reader.nextString());
                                break;
                            }
                        }

                        //TODO remove this hack, remove while -> functional
                        final String platformFinal = platform;
                        final String versionFinal = version;

                        final Result<DesiredCapabilities> capabilitiesResult = desiredCapabilities.apply(browser);
                        capabilitiesResult.bind(
                            sucess -> {
                                sucess.setPlatform(Platform.fromString(platformFinal));
                                sucess.setVersion(versionFinal);
                                noNameProps.forEach(sucess::setCapability);
                                ((CheckedExecutor) reader::endObject).execute();
                            },
                            failed -> {}
                        );

                        capabilitiesResult.ifPresent(result::add);
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }

        return Result.success(result);
    };

    Supplier<List<WebDriver>> webDriverInstances = () -> {

        readTestbenchProperties.execute();

        final Properties properties = readProperties
            .apply("config/selenium-grids.properties")
            .getOrElse(Properties::new);

        return readBrowserCombinations
            .get()
            .getOrElse(Collections::emptyList) //TODO check if needed
            .stream()
            .map(desiredCapability -> {
                final String browserName = desiredCapability.getBrowserName();
                //for all selenium ips
                return properties
                    .entrySet()
                    .stream()
                    .map(e -> {
                        final String key = (String) e.getKey();
                        final String targetAddress = (String) e.getValue();

                        final String ip = (targetAddress.endsWith("locale-ip"))
                            ? ipSupplierLocalIP.get()
                            : targetAddress;

                        return Case
                            .match(
                                Case.matchCase(() -> ((CheckedSupplier<WebDriver>)()-> {
                                    final URL url = new URL("http://" + ip + ":4444/wd/hub");
                                    final RemoteWebDriver remoteWebDriver = new RemoteWebDriver(url, desiredCapability);
                                    return TestBench.createDriver(remoteWebDriver);
                                }).get()),
                                Case.matchCase(() -> key.equals("nogrid"), () -> localWebDriverInstance.apply(browserName))
                            );
                    })
                    .filter(Result::isPresent)
                    .collect(Collectors.toList());
            })
            .flatMap(Collection::stream)
            .map(Result::get)
            .collect(Collectors.toList());
    };
}
