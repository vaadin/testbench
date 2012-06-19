package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

public class MaskedScreenshotITCase extends TestBenchTestCase {
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        baseUrl = "http://localhost:8080";
        Parameters
                .setScreenshotReferenceDirectory("src/test/resources/screenshots");
        Parameters
                .setScreenshotErrorDirectory("target/testbench/screenshot_errors");
        // Capture a screenshot if no reference image is found
        Parameters.setCaptureScreenshotOnFailure(true);
    }

    @Test
    public void testOnePlusTwo() throws Exception {
        openCalculator();
        // Add a bunch of random values together to fill the log with
        // randomness.
        addRandomValues();
        WebElement display = getDriver().findElement(By.id("display"));

        // Clear and calculate 1 + 2
        getDriver().findElement(By.id("button_C")).click();
        getDriver().findElement(By.id("button_1")).click();
        getDriver().findElement(By.id("button_+")).click();
        getDriver().findElement(By.id("button_2")).click();
        getDriver().findElement(By.id("button_=")).click();
        assertEquals("3.0", display.getText());

        // Compare with a screenshot, which should pass as the log is
        // masked.
        assertTrue(testBench().compareScreen("onePlusTwoMasked"));
    }

    private void openCalculator() {
        getDriver().get(baseUrl + "/demo-site/Calc?restartApplication");
    }

    /**
     * Adds random values together by clicking the buttons.
     */
    private void addRandomValues() {
        WebElement plusButton = getDriver().findElement(By.id("button_+"));
        Random rnd = new Random();
        String buttonId = "";
        for (int i = 0; i < rnd.nextInt(10) + 1; i++) {
            buttonId = String.format("button_%d", rnd.nextInt(9) + 1);
            getDriver().findElement(By.id(buttonId)).click();
            plusButton.click();
        }
        getDriver().findElement(By.id(buttonId)).click();
        getDriver().findElement(By.id("button_=")).click();
    }

    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }
}
