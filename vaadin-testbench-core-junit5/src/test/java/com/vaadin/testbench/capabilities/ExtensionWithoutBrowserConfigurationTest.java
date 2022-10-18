package com.vaadin.testbench.capabilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.SetCapabilities;
import com.vaadin.testbench.TestBenchTest;

public class ExtensionWithoutBrowserConfigurationTest
        implements SetCapabilities, DriverSupplier {

    private static String oldBrowsers;

    private Capabilities capabilities;

    @BeforeAll
    public static void setParameters() {
        oldBrowsers = Parameters.getGridBrowsersString();
        Parameters.setGridBrowsers("");
    }

    @AfterAll
    public static void unsetParameters() {
        Parameters.setGridBrowsers(oldBrowsers);
    }

    @TestBenchTest
    public void withoutBrowsersConfiguration() {
        DesiredCapabilities caps = DesiredCapabilitiesUtil
                .getDefaultCapabilities().get(0);
        Assertions.assertEquals(caps.getBrowserName(),
                capabilities.getBrowserName());
        Assertions.assertEquals(caps.getBrowserVersion(),
                capabilities.getBrowserVersion());
        Assertions.assertEquals(caps.getPlatformName(),
                capabilities.getPlatformName());
    }

    @Override
    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

}
