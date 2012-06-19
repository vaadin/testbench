package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

/**
 * This case demonstrates usage of execution time reporting.
 */
public class VerifyExecutionTimeITCase extends TestBenchTestCase {

    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        baseUrl = "http://localhost:8080";
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

    /**
     * Makes the same thing as in {@link #testOnePlusTwo()} and verifies server
     * don't spend too much time during the process. Also the test makes sure
     * the time spent by browser to render the UI within sane limits.
     * 
     * @throws Exception
     */
    @Test
    public void verifyServerExecutionTime() throws Exception {
        openCalculator();
        long currentSessionTime = testBench(getDriver())
                .totalTimeSpentServicingRequests();
        calculateOnePlusTwo();

        long timeSpentByServerForSimpleCalculation = testBench()
                .totalTimeSpentServicingRequests() - currentSessionTime;

        System.out.println("Calculating 1+2 took about "
                + timeSpentByServerForSimpleCalculation
                + "ms in servlets service method.");

        if (timeSpentByServerForSimpleCalculation > 30) {
            fail("Simple calculation shouldn't take "
                    + timeSpentByServerForSimpleCalculation + "ms!");
        }

        long totalTimeSpentRendering = testBench().totalTimeSpentRendering();
        System.out.println("Rendering UI took " + totalTimeSpentRendering
                + "ms");
        if (timeSpentByServerForSimpleCalculation > 400) {
            fail("Rendering UI shouldn't take "
                    + timeSpentByServerForSimpleCalculation + "ms!");
        }

        assertEquals("3.0", getDriver().findElement(By.id("display")).getText());

    }

    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }

}
