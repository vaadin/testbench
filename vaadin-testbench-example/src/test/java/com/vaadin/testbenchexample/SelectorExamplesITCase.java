package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

/**
 * This example demonstrates usege of varios element selection methods.
 */
public class SelectorExamplesITCase extends TestBenchTestCase {

    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        baseUrl = "http://localhost:8080";

    }

    /**
     * This example calculates 1+2 with identifier selectors. Using identifiers
     * (like this or within Vaadin or xpath selectors) is often good choice as
     * they are stable, readable and easy to use. The downside is that
     * developers need to use {@link com.vaadin.ui.Component#setDebugId(String)}
     * to add identifiers to DOM.
     * 
     * @throws Exception
     */
    @Test
    public void testOnePlusTwoWithIdentifiers() throws Exception {
        openCalculator();
        getDriver().findElement(By.id("button_1")).click();
        getDriver().findElement(By.id("button_+")).click();
        getDriver().findElement(By.id("button_2")).click();
        getDriver().findElement(By.id("button_=")).click();
        assertEquals("3.0", getDriver().findElement(By.id("display")).getText());

    }

    private void openCalculator() {
        getDriver().get(baseUrl + "/testbenchexample?restartApplication");
    }

    /**
     * Using identifiers is often the most easiest and most stable approach.
     * There are though several other methods to select elements during tests of
     * which XPath is one of the most powerful. This example is identical to
     * {@link #testOnePlusTwoWithIdentifiers()}, but uses XPath queries.
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
