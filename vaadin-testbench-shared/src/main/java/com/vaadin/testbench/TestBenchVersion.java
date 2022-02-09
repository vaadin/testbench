package com.vaadin.testbench;

import java.util.Properties;

import com.vaadin.pro.licensechecker.LicenseChecker;

import org.openqa.selenium.BuildInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBenchVersion {

    public static final String testbenchVersion;
    static {
        Properties properties = new Properties();
        try {
            properties.load(TestBenchVersion.class
                    .getResourceAsStream("testbench.properties"));
        } catch (Exception e) {
            getLogger().warn("Unable to read TestBench properties file",
                    e);
            throw new ExceptionInInitializerError(e);
        }

        String seleniumVersion = new BuildInfo().getReleaseLabel();
        testbenchVersion = properties.getProperty("testbench.version");
        String expectedVersion = properties.getProperty("selenium.version");
        if (seleniumVersion == null
                || !seleniumVersion.equals(expectedVersion)) {
            getLogger().warn(
                    "This version of TestBench depends on Selenium version "
                            + expectedVersion + " but version "
                            + seleniumVersion
                            + " was found. Make sure you do not have multiple versions of Selenium on the classpath.");
        }

        LicenseChecker.checkLicenseFromStaticBlock("vaadin-testbench",
                testbenchVersion);
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(TestBenchVersion.class);
    }

}
