package com.vaadin.testbench.tests;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestSeleniumWorking {
    private FirefoxDriver driver;

    @Before
    public void setUp() {
        driver = new FirefoxDriver();
    }

    @After
    public void tareDown() {
        driver.close();
    }

    @Test
    public void testStartBrowser() throws IOException {
        driver.navigate().to("http://google.com");
        // File screenshot = driver.getScreenshotAs(OutputType.FILE);
        // FileUtils.copyFile(screenshot, new File("/tmp/shot.png"));
    }
}
