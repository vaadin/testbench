/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadTestItHelperTest {

    private String originalServerPort;
    private String originalProxyHost;

    @BeforeEach
    void captureSystemProperties() {
        originalServerPort = System.getProperty("server.port");
        originalProxyHost = System.getProperty("k6.proxy.host");
        System.clearProperty("server.port");
        System.clearProperty("k6.proxy.host");
    }

    @AfterEach
    void restoreSystemProperties() {
        restore("server.port", originalServerPort);
        restore("k6.proxy.host", originalProxyHost);
    }

    private static void restore(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

    @Test
    void getRootURL_usesHostnameAndPort() {
        String envHostname = System.getenv("HOSTNAME");
        String expectedHost = (envHostname != null && !envHostname.isEmpty())
                ? envHostname
                : "localhost";
        assertEquals("http://" + expectedHost + ":8080",
                LoadTestItHelper.getRootURL());
    }

    @Test
    void getDeploymentHostname_defaultsToLocalhost_whenEnvNotSet() {
        // HOSTNAME env is read directly; on most dev machines it's unset.
        // Default branch: when env is null/empty, return localhost.
        String hostname = LoadTestItHelper.getDeploymentHostname();
        String envHostname = System.getenv("HOSTNAME");
        if (envHostname == null || envHostname.isEmpty()) {
            assertEquals("localhost", hostname);
        } else {
            assertEquals(envHostname, hostname);
        }
    }

    @Test
    void getDeploymentPort_defaultsTo8080_whenSystemPropertyUnset() {
        assertEquals(8080, LoadTestItHelper.getDeploymentPort());
    }

    @Test
    void getDeploymentPort_readsFromSystemProperty() {
        System.setProperty("server.port", "9090");
        assertEquals(9090, LoadTestItHelper.getDeploymentPort());
    }

    @Test
    void getDeploymentPort_defaultsTo8080_whenSystemPropertyEmpty() {
        System.setProperty("server.port", "");
        assertEquals(8080, LoadTestItHelper.getDeploymentPort());
    }

    @Test
    void getDeploymentPort_throwsOnMalformedValue() {
        System.setProperty("server.port", "not-a-number");
        assertThrows(NumberFormatException.class,
                LoadTestItHelper::getDeploymentPort);
    }

    @Test
    void openWithProxy_proxyUnset_navigatesExistingDriver() {
        WebDriver driver = Mockito.mock(WebDriver.class);

        WebDriver result = LoadTestItHelper.openWithProxy(driver,
                "http://example.com");

        assertSame(driver, result);
        Mockito.verify(driver).get("http://example.com");
        Mockito.verify(driver, Mockito.never()).quit();
    }

    @Test
    void openWithProxy_proxyUnsetWithEmptyValue_navigatesExistingDriver() {
        System.setProperty("k6.proxy.host", "");
        WebDriver driver = Mockito.mock(WebDriver.class);

        WebDriver result = LoadTestItHelper.openWithProxy(driver,
                "http://example.com");

        assertSame(driver, result);
        Mockito.verify(driver).get("http://example.com");
        Mockito.verify(driver, Mockito.never()).quit();
    }

}
