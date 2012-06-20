package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

public class SimpleCalculatorITCase extends TestBenchTestCase {

    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        baseUrl = "http://localhost:8080";
    }

    @Test
    public void testOnePlusTwo() throws Exception {
        openCalculator();
        calculateOnePlusTwo();
        assertEquals("3.0", getDriver().findElement(By.id("display")).getText());
    }

    private void openCalculator() {
        getDriver().get(baseUrl + "/demo-site/Calc?restartApplication");
    }

    private void calculateOnePlusTwo() {
        getDriver().findElement(By.id("button_1")).click();
        getDriver().findElement(By.id("button_+")).click();
        getDriver().findElement(By.id("button_2")).click();
        getDriver().findElement(By.id("button_=")).click();
    }

    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }

}
