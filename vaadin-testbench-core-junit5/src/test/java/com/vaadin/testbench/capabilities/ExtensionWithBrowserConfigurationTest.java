package com.vaadin.testbench.capabilities;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.SetCapabilities;
import com.vaadin.testbench.TestBenchTest;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.Browser;

public class ExtensionWithBrowserConfigurationTest
        implements SetCapabilities, DriverSupplier {

    private static String oldBrowsers;

    private Capabilities capabilities;

    @BeforeAll
    public static void setParameters() {
        oldBrowsers = Parameters.getGridBrowsersString();
        Parameters.setGridBrowsers("firefox,safari-9");
    }

    @AfterAll
    public static void unsetParameters() {
        Parameters.setGridBrowsers(oldBrowsers);
    }

    @TestBenchTest
    public void withBrowserConfigurationInClass() {
        DesiredCapabilities caps = Browser.FIREFOX.getDesiredCapabilities();
        Assertions.assertEquals(caps.getBrowserName(),
                capabilities.getBrowserName());
        Assertions.assertEquals(caps.getBrowserVersion(),
                capabilities.getBrowserVersion());
        Assertions.assertEquals(caps.getPlatformName(),
                capabilities.getPlatformName());
    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        return Arrays.asList(Browser.FIREFOX.getDesiredCapabilities());
    }

    @Override
    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

}
