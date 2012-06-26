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

/**
 * This example contains usage examples of screenshot comparison feature.
 * <p>
 * By default tests should pass (in case Firefox hasn't upgraded and correct
 * reference image is found). Rendering of the example app can though differ
 * from platform to platform (e.g. rendering of fonts or NativeButton styles).
 * <p>
 * To see how TestBench visualizes changed parts, either modify reference images
 * from src/test/resources/screenshots or change the application a bit (like add
 * components).
 * <p>
 * Results of failed screenshot comparisons are stored to
 * target/testbench/screenshot_errors. If you wish to make them pass copy,
 * failed screenshots to reference directory. Note that in in the random log
 * test the reference screenshot should have the log part masked as transparent.
 * TestBench ignores transparent pixels from the comparison and changes on that
 * area are permitted.
 * 
 * 
 */
public class ScreenshotITCase extends TestBenchTestCase {

    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        baseUrl = "http://localhost:8080";

        // Try to adjust browser window size so that its content area is 500x400
        // pixels
        testBench().resizeViewPortTo(500, 400);

        // Define the default directory for reference screenshots
        Parameters
                .setScreenshotReferenceDirectory("src/test/resources/screenshots");

        // Define the directory where possible error files and screenshots
        // should go
        Parameters
                .setScreenshotErrorDirectory("target/testbench/screenshot_errors");

        // Capture a screenshot if no reference image is found
        Parameters.setCaptureScreenshotOnFailure(true);
    }

    private void openCalculator() {
        getDriver().get(baseUrl + "/demo-site/Calc?restartApplication");
    }

    @Test
    public void testOnePlusTwo() throws Exception {
        openCalculator();
        calculateOnePlusTwo();
        assertEquals("3.0", getDriver().findElement(By.id("display")).getText());

        // Compare screen with reference image with id "oneplusto" from the
        // reference image directory. Reference image filenames also contain
        // browser, version and platform.
        // Note, that this will likely fail if you have a bit different platform
        // Reference images currently exist for FF13 XP, FF13 mac, FF12 linux
        assertTrue(testBench().compareScreen("oneplustwo"));

        // If the id based reference image don't fit for your needs, you can
        // also use a direct File reference like this:
        // testBench().compareScreen(new File("path/to/MyFile"));
    }

    private void calculateOnePlusTwo() {
        getDriver().findElement(By.id("button_1")).click();
        getDriver().findElement(By.id("button_+")).click();
        getDriver().findElement(By.id("button_2")).click();
        getDriver().findElement(By.id("button_=")).click();
    }

    @Test
    public void testOnePlusTwoWithRandomLog() throws Exception {
        openCalculator();
        // Add a bunch of random values together to fill the log with
        // randomness.
        addRandomValues();
        WebElement display = getDriver().findElement(By.id("display"));

        // Clear and calculate 1 + 2
        getDriver().findElement(By.id("button_C")).click();
        calculateOnePlusTwo();
        assertEquals("3.0", display.getText());

        // Compare with a screenshot, which should pass as the log is
        // masked.
        assertTrue(testBench().compareScreen("onePlusTwoMasked"));
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
