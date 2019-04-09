package com.vaadin.testbench.addons.junit5.pageobject;

import com.vaadin.testbench.addons.junit5.extensions.container.HasContainerInfo;
import com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions;
import com.vaadin.testbench.addons.webdriver.HasDriver;

import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.takeScreenshot;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;
import static java.lang.System.getProperties;

public interface PageObject extends HasContainerInfo, HasDriver {

    String BACK_SLASH = "/";

    default void loadPage() {
        final String url = url();
        getDriver().get(url);
    }

    default void loadPage(String route) {
        final String url = url();
        getDriver().get(url + route);
    }

    default String getTitle() {
        return getDriver().getTitle();
    }

    default String property(String key, String defaultValue) {
        return (String) getProperties().getOrDefault(key, defaultValue);
    }

    default String protocol() {
        return property(NetworkFunctions.SERVER_PROTOCOL, NetworkFunctions.DEFAULT_PROTOCOL);
    }

    default String ip() {
        return getContainerInfo().host();
    }

    default String port() {
        return String.valueOf(getContainerInfo().port());
    }

    // TODO(sven): Per properties.
    default String webapp() {
        return property(NetworkFunctions.SERVER_WEBAPP, NetworkFunctions.DEFAULT_SERVLET_WEBAPP);
    }

    default String baseUrl() {
        return protocol() + "://" + ip() + ":" + port();
    }

    default String url() {
        final String webapp = webapp();
        final String url = webapp.isEmpty() ? BACK_SLASH
                : webapp.endsWith(BACK_SLASH) && webapp.startsWith(BACK_SLASH) ? webapp
                : webapp.endsWith(BACK_SLASH) && !webapp.startsWith(BACK_SLASH) ? BACK_SLASH + webapp
                : webapp.equals(BACK_SLASH) ? BACK_SLASH
                : BACK_SLASH + webapp + BACK_SLASH;

        return baseUrl() + url;
    }

    default void destroy() {
        getDriver().quit();
        getDriver().close();
    }

    default void screenshot() {
        takeScreenshot(getDriver());
    }

    default String driverName() {
        return webdriverName(getDriver());
    }
}
