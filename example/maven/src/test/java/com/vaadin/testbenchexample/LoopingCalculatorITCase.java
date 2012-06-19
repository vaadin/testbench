package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

public class LoopingCalculatorITCase extends TestBenchTestCase {
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        baseUrl = "http://localhost:8080";
    }

    @Test
    public void testOnePlusTwo() throws Exception {
        openCalculator();
        WebElement display = getDriver().findElement(By.id("display"));
        // Add five ones together and verify that the result is 5.0
        performAddition("button_1", 5);
        assertEquals("5.0", display.getText());

        // Clear the calculator and add four twos together
        getDriver().findElement(By.id("button_C")).click();
        performAddition("button_2", 4);
        assertEquals("8.0", display.getText());
    }

    private void openCalculator() {
        getDriver().get(baseUrl + "/demo-site/Calc?restartApplication");
    }

    /**
     * Clicks the button identified by buttonId followed by the + button
     * timesToAdd times and then clicks the = button to make the calculator
     * display the result.
     * 
     * @param buttonId
     * @param timesToAdd
     */
    private void performAddition(String buttonId, int timesToAdd) {
        WebElement numberButton = getDriver().findElement(By.id(buttonId));
        WebElement plusButton = getDriver().findElement(By.id("button_+"));
        for (int i = 0; i < timesToAdd - 1; i++) {
            numberButton.click();
            plusButton.click();
        }
        numberButton.click();
        getDriver().findElement(By.id("button_=")).click();
    }

    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }
}
