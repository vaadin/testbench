package com.vaadin.testbench.capabilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.SetCapabilities;
import com.vaadin.testbench.TestBenchTest;
import com.vaadin.testbench.parallel.Browser;

public class ExtensionWithBrowserConfigurationInParametersTest
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
    public void withBrowsersConfigurationInParameters(TestInfo testInfo) {
        DesiredCapabilities caps1 = Browser.FIREFOX.getDesiredCapabilities();
        DesiredCapabilities caps2 = Browser.SAFARI.getDesiredCapabilities();
        caps2.setVersion("9");
        if (testInfo.getDisplayName().contains("Firefox")) {
            Assertions.assertEquals(caps1.getBrowserName(),
                    capabilities.getBrowserName());
            Assertions.assertEquals(caps1.getBrowserVersion(),
                    capabilities.getBrowserVersion());
            Assertions.assertEquals(caps1.getPlatformName(),
                    capabilities.getPlatformName());
        } else {
            Assertions.assertEquals(caps2.getBrowserName(),
                    capabilities.getBrowserName());
            Assertions.assertEquals(caps2.getBrowserVersion(),
                    capabilities.getBrowserVersion());
            Assertions.assertEquals(caps2.getPlatformName(),
                    capabilities.getPlatformName());
        }
    }

    @Override
    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

}
