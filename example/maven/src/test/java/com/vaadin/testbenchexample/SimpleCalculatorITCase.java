package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

public class SimpleCalculatorITCase extends TestBenchTestCase {

    private String baseUrl;
    private static final File ONE_PLUS_TWO_SCREENSHOT = new File(
            "src/test/resources/screenshots/onePlusTwo.png");

    @Before
    public void setUp() throws Exception {

        setDriver(TestBench.createDriver(new FirefoxDriver()));

        baseUrl = "http://localhost:8080";

        Parameters
                .setScreenshotReferenceDirectory("src/test/resources/screenshots");
        Parameters
                .setScreenshotErrorDirectory("target/testbench/screenshot_errors");
    }

    @Test
    public void testOnePlusTwo() throws Exception {
        openCalculator();
        calculateOnePlusTwo();
        assertEquals("3.0", getDriver().findElement(By.id("display")).getText());

        // Note that this will likely fail if you have a bit different platform,
        // the reference image has been taken with mac and Firefox 11
        assertTrue(testBench().compareScreen(ONE_PLUS_TWO_SCREENSHOT));
    }

    private void openCalculator() {
        getDriver().get(baseUrl + "/demo-site/Calc");
    }

    private void calculateOnePlusTwo() {
        getDriver().findElement(By.id("button_1")).click();
        getDriver().findElement(By.id("button_+")).click();
        getDriver().findElement(By.id("button_2")).click();
        getDriver().findElement(By.id("button_=")).click();
    }

    /**
     * Makes the same thing as in {@link #testOnePlusTwo()}, but
     * 
     * @throws Exception
     */
    @Test
    public void verifyServerExecutionTime() throws Exception {
        openCalculator();
        long currentSessionTime = testBench(getDriver())
                .totalTimeSpentServicingRequests();
        calculateOnePlusTwo();

        long timeSpentByServerForSimpleCalculation = testBench(getDriver())
                .totalTimeSpentServicingRequests() - currentSessionTime;

        System.out.println("Calculating 1+2 took about "
                + timeSpentByServerForSimpleCalculation
                + "ms in servlets service method.");

        if (timeSpentByServerForSimpleCalculation > 10) {
            fail("Simple calculation shouldn't take "
                    + timeSpentByServerForSimpleCalculation + "ms!");
        }

        assertEquals("3.0", getDriver().findElement(By.id("display")).getText());

    }

    /**
     * Using identifiers is often the most easiest and most stable approach.
     * There are though several other methods to select elements during tests of
     * which XPath is one of the most powerful. This example is identical to
     * {@link #testOnePlusTwo()}, but uses XPath queries.
     * 
     * @throws AssertionError
     * @throws IOException
     */
    @Test
    public void onePlusTwoWithXPathSelectors() throws IOException,
            AssertionError {
        openCalculator();
        // select element whose text is exactly '1', that should be "button 1"
        // and click it, ...
        getDriver().findElement(By.xpath("//*[text() = '1']")).click();
        getDriver().findElement(By.xpath("//*[text() = '+']")).click();
        getDriver().findElement(By.xpath("//*[text() = '2']")).click();
        getDriver().findElement(By.xpath("//*[text() = '=']")).click();

        // Here we could just check for 3.0, but to demonstrate powers of XPath
        // we also verify the tag name and classname to be v-label
        assertTrue(isElementPresent(By
                .xpath("//div[text() = '3.0' and contains(@class, 'v-label')]")));

    }

    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }

}
