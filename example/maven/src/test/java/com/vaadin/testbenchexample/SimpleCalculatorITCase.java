package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

/**
 * This is an example of very simple TestBench usage.
 * <p>
 * This kind of simple tests can easily be written, but also they can be
 * recorded with the Recorder. This example is first recorded and then commented
 * and re-factored to be more readable.
 */
public class SimpleCalculatorITCase extends TestBenchTestCase {

    private String baseUrl;

    /**
     * Preparing for actual test, create a firefox driver and define the address
     * where the app is located.
     */
    @Before
    public void setUp() throws Exception {
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        baseUrl = "http://localhost:8080";
    }

    @Test
    public void testOnePlusTwo() throws Exception {
        // Tell the browser to open the app, see method for details
        openCalculator();
        // Simulated user clicking "1+2=", see method for details
        calculateOnePlusTwo();
        // Verify the expected result
        assertEquals("3.0", getDriver().findElement(By.id("display")).getText());
    }

    private void openCalculator() {
        getDriver().get(baseUrl + "/demo-site/Calc?restartApplication");
    }

    private void calculateOnePlusTwo() {
        // Select buttons by their identifier and simulate click event
        getDriver().findElement(By.id("button_1")).click();
        getDriver().findElement(By.id("button_+")).click();
        getDriver().findElement(By.id("button_2")).click();
        getDriver().findElement(By.id("button_=")).click();
    }

    @After
    public void tearDown() throws Exception {
        // Tell the driver to quit the browser
        getDriver().quit();
    }

}
