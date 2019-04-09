package com.vaadin.testbench.addons.webdriver.conf;

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

import com.vaadin.testbench.addons.webdriver.BrowserDriverFunctions;
import com.vaadin.testbench.addons.webdriver.conf.GridConfig.Type;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import static com.github.webdriverextensions.WebDriverProperties.CHROME_BINARY_PROPERTY_NAME;
import static com.github.webdriverextensions.WebDriverProperties.CHROME_DRIVER_PROPERTY_NAME;
import static com.github.webdriverextensions.WebDriverProperties.EDGE_DRIVER_PROPERTY_NAME;
import static com.github.webdriverextensions.WebDriverProperties.FIREFOX_DRIVER_PROPERTY_NAME;
import static com.github.webdriverextensions.WebDriverProperties.IE_DRIVER_PROPERTY_NAME;
import static com.github.webdriverextensions.WebDriverProperties.OPERA_DRIVER_PROPERTY_NAME;
import static com.vaadin.testbench.addons.webdriver.conf.WebdriversConfig.COMPATTESTING_GRID;
import static java.lang.System.setProperty;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toSet;

public class WebdriversConfigFactory {

    public static final String DEFAULT_UNITTESTING_BROWSER = "chrome";
    public static final String PROTO = "proto";
    public static final String PORT = "port";
    public static final String PATH = "path";
    public static final String DEFAULT_PORT = "4444";
    public static final String DEFAULT_PATH = "wd/hub";
    public static final String DEFAULT_PROTO = "http";

    private static String GECKO_BINARY_PROPERTY_NAME = "gecko.binary.path";
    private static String IE_BINARY_PROPERTY_NAME = "ie.binary.path";
    private static String OPERA_BINARY_PROPERTY_NAME = "opera.binary.path";
    private static String EDGE_BINARY_PROPERTY_NAME = "edge.binary.path";

    public WebdriversConfig createFromProperies(Properties configProperties) {
        Optional.ofNullable(configProperties.getProperty(CHROME_BINARY_PROPERTY_NAME, null))
                .ifPresent(success -> setProperty(CHROME_DRIVER_PROPERTY_NAME, success));

        Optional.ofNullable(configProperties.getProperty(GECKO_BINARY_PROPERTY_NAME, null))
                .ifPresent(success -> setProperty(FIREFOX_DRIVER_PROPERTY_NAME, success));

        Optional.ofNullable(configProperties.getProperty(IE_BINARY_PROPERTY_NAME, null))
                .ifPresent(success -> setProperty(IE_DRIVER_PROPERTY_NAME, success));

        Optional.ofNullable(configProperties.getProperty(OPERA_BINARY_PROPERTY_NAME, null))
                .ifPresent(success -> setProperty(OPERA_DRIVER_PROPERTY_NAME, success));

        Optional.ofNullable(configProperties.getProperty(EDGE_BINARY_PROPERTY_NAME, null))
                .ifPresent(success -> setProperty(EDGE_DRIVER_PROPERTY_NAME, success));

        // TODO(sven): Check if compat test should run on local browser.
        final List<GridConfig> gridConfigs = unmodifiableList(createGridConfigs(configProperties));

//    logger().info("Loaded " + gridConfigs.size() + " grid configuration(s)");
        return new WebdriversConfig(gridConfigs);
    }

    private List<GridConfig> createGridConfigs(Properties configProperties) {
        List<GridConfig> grids = new ArrayList<>();
        Set<String> gridNames = configProperties.stringPropertyNames()
                .stream()
                .filter(key -> key.startsWith(COMPATTESTING_GRID))
                .map(key -> key.substring(COMPATTESTING_GRID.length() + 1))
                .map(key -> key.substring(0, key.indexOf('.'))).collect(toSet());
        if (gridNames.isEmpty()) {
            grids.add(createDefaultGrid());
        }
        for (String gridName : gridNames) {
            if (isActive(configProperties, gridName)) {
                Type type = getGridType(configProperties, gridName);
                String target;

                switch (type) {
                    case BROWSERSTACK:
                        target = getGridTargetBrowserStack(configProperties, gridName);
                        break;
                    case SAUCELABS:
                        target = getGridTargetSauceLabs(configProperties, gridName);
                        break;
                    default:
                        target = getGridTarget(configProperties, gridName);
                }

                grids.add(new GridConfig(type, gridName, target,
                        getDesiredCapabilities(configProperties, gridName, type)
                ));
            }
        }
        return grids;
    }

    private GridConfig createDefaultGrid() {
        DesiredCapabilities desiredCapability = new DesiredCapabilities();
        desiredCapability.setPlatform(Platform.ANY);
        desiredCapability.setBrowserName(DEFAULT_UNITTESTING_BROWSER);
        return new GridConfig(Type.GENERIC, "autoConfGrid",
                "http://localhost:4444/wd/hub", singletonList(desiredCapability));
    }

    private boolean isActive(Properties configProperties, String gridName) {
        return Boolean.valueOf(getProperty(configProperties, gridName, "active", "true"));
    }

    private Type getGridType(Properties configProperties, String gridName) {
        String stringType = configProperties.getProperty(getGridNameKey(gridName) + ".type", "generic");
        return Type.valueOf(stringType.toUpperCase());
    }

    private List<DesiredCapabilities> getDesiredCapabilities(Properties configProperties,
                                                             String gridName, Type type) {
        Set<String> oses = getOSes(configProperties, gridName);

        Set<String> browsers = getBrowsers(configProperties, gridName);
        List<DesiredCapabilities> desiredCapabilites = new ArrayList<>();

        // TODO(sven): Should work without OS as well.
        for (String os : oses) {
            for (String browser : browsers) {
                for (String version : getVersions(configProperties, gridName, browser)) {
                    DesiredCapabilities desiredCapability = new DesiredCapabilities();
                    desiredCapability.setPlatform(Platform.fromString(os));
                    desiredCapability.setBrowserName(browser);
                    if (!"ANY".equals(version)) {
                        desiredCapability.setVersion(version);
                    }
                    if (type == Type.SELENOID) {
                        desiredCapability.setCapability(BrowserDriverFunctions.ENABLE_VIDEO,
                                getBoolean(configProperties, gridName, BrowserDriverFunctions.ENABLE_VIDEO)
                        );
                        desiredCapability.setCapability(BrowserDriverFunctions.ENABLE_VNC,
                                getBoolean(configProperties, gridName, BrowserDriverFunctions.ENABLE_VNC)
                        );
                    } else if (type == Type.BROWSERSTACK) {
                        final String project = getProperty(configProperties, gridName, "project");
                        if (StringUtils.isNotBlank(project)) {
                            desiredCapability.setCapability(BrowserDriverFunctions.PROJECT, project);
                        }
                    } else if (type == Type.SAUCELABS) {
                        final String project = getProperty(configProperties, gridName, "project");
                        if (StringUtils.isNotBlank(project)) {
                            desiredCapability.setCapability(BrowserDriverFunctions.TAGS, project);
                        }
                        final String build = System.getenv("SAUCELABS_BUILD");
                        if (StringUtils.isNotBlank(build)) {
                            desiredCapability.setCapability("build", build);
                        }
                    }
                    desiredCapabilites.add(desiredCapability);
                }
            }
        }

        // TODO(sven): If nothing was added, add default Chrome.
        if (desiredCapabilites.isEmpty()) {
            desiredCapabilites.add(DesiredCapabilities.chrome());
        }
        return desiredCapabilites;
    }

    private boolean getBoolean(Properties configProperties, String gridName, String propertieName) {
        String stringValue =
                configProperties.getProperty(getGridNameKey(gridName) + "." + propertieName).trim();
        return StringUtils.isNotBlank(stringValue) ? Boolean.valueOf(stringValue) : false;
    }

    private Set<String> getBrowsers(Properties configProperties, String gridName) {
        return stream(configProperties.getProperty(getGridNameKey(gridName) + ".browser")
                .split(","))
                .map(String::trim)
                .collect(toSet());
    }

    private Set<String> getOSes(Properties configProperties, String gridName) {
        String property = configProperties.getProperty(getGridNameKey(gridName) + ".os");
        return (property == null)
                ? stream(new String[]{"ANY"})
                .collect(toSet())
                : stream(property.split(","))
                .map(String::trim)
                .collect(toSet());
    }

    private Set<String> getVersions(Properties configProperties, String gridName, String browser) {
        String property = configProperties.getProperty(getGridNameKey(gridName) + ".browser." + browser + ".version");
        return (property == null)
                ? stream(new String[]{"ANY"})
                .collect(toSet())
                : stream(property.split(","))
                .map(String::trim)
                .collect(toSet());
    }

    private String getGridTarget(Properties configProperties, String gridName) {
        final String host = getProperty(configProperties, gridName, "target");
        Validate.notBlank(host, "The target for the grid {} may not be blank", gridName);
        if (host.equals("locale")) {
            return host;
        } else {
            final String proto = getProperty(configProperties, gridName, PROTO, DEFAULT_PROTO);
            final String port = getProperty(configProperties, gridName, PORT, DEFAULT_PORT);
            final String path = getProperty(configProperties, gridName, PATH, DEFAULT_PATH);

            return proto + "://" + host + ":" + port + "/" + path;
        }
    }

    private String getProperty(Properties configProperties, String gridName, String property) {
        return configProperties.getProperty(getGridNameKey(gridName) + "." + property);
    }

    private String getProperty(Properties configProperties, String gridName, String property, String defaultVaule) {
        return configProperties.getProperty(getGridNameKey(gridName) + "." + property, defaultVaule);
    }

    private String getGridTargetBrowserStack(Properties configProperties, String gridName) {
        final String userName =
                Validate.notBlank(configProperties.getProperty(getGridNameKey(gridName) + ".username"));
        final String key =
                Validate.notBlank(configProperties.getProperty(getGridNameKey(gridName) + ".key"));
        return "https://" + userName + ":" + key + "@hub-cloud.browserstack.com/wd/hub";
    }

    private String getGridTargetSauceLabs(Properties configProperties, String gridName) {
        final String userName =
                Validate.notBlank(configProperties.getProperty(getGridNameKey(gridName) + ".username"));
        final String key =
                Validate.notBlank(configProperties.getProperty(getGridNameKey(gridName) + ".key"));
        return "https://" + userName + ":" + key + "@ondemand.saucelabs.com:443/wd/hub";
    }

    private String getGridNameKey(String gridName) {
        return COMPATTESTING_GRID + "." + gridName;
    }
}
