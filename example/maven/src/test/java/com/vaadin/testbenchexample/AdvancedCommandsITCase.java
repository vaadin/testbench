package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.commands.TestBenchElementCommands;

/**
 * This example contains some usages of bit more advanced TestBench and
 * WebDriver usage.
 * 
 */
public class AdvancedCommandsITCase extends TestBenchTestCase {

    private static final String COMMENT_TEXT = "Next we'll click button 2";
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        baseUrl = "http://localhost:8080";
    }

    /**
     * This test demonstrates how developers can use context menus in tests. The
     * example app has some features behind a context menu that can be opened
     * over calculators log.
     * 
     * @throws Exception
     */
    @Test
    public void useContextMenuToAddCommentRow() throws Exception {
        openCalculator();
        // just do some math first
        getDriver().findElement(By.id("button_1")).click();
        getDriver().findElement(By.id("button_+")).click();

        // This is the essential part of this test. We select the table body
        // element and perform context click action on it and select
        // "Add Comment" from the opened menu
        WebElement e = getDriver().findElement(By.className("v-table-body"));
        new Actions(getDriver()).moveToElement(e).contextClick(e).perform();
        getDriver().findElement(By.xpath("//*[text() = 'Add Comment']"))
                .click();

        // The rest of the test is less relevant as a context menu
        // demonstration. We just fill in a comment and verify the commenting
        // feature works as expected
        getDriver().findElement(By.className("v-textfield")).sendKeys(
                COMMENT_TEXT);

        // FIXME this shouldn't be needed!
        getDriver().findElement(By.className("v-textfield")).sendKeys("\n");

        getDriver().findElement(By.xpath("//*[text() = 'Add']")).click();

        // Ensure window is closed
        boolean windowPresent = isElementPresent(By.className("v-window"));
        if (windowPresent) {
            fail("Modal window prompting textfield was not properly closed");
        }
        getDriver().findElement(By.id("button_2")).click();
        getDriver().findElement(By.id("button_=")).click();
        assertEquals("3.0", getDriver().findElement(By.id("display")).getText());

        // Verify the second row in log contains our comment
        String secondRowText = getDriver()
                .findElements(By.className("v-table-cell-wrapper")).get(1)
                .getText();
        assertTrue(secondRowText.contains(COMMENT_TEXT));

    }

    /**
     * This demonstrates usage of {@link TestBenchElementCommands#showTooltip()}
     * method.
     * 
     * @throws Exception
     */
    @Test
    public void verifyAddCommentButtonHasProperTooltip() throws Exception {
        openCalculator();

        // using context menu, add the popup to fill in a comment
        WebElement e = getDriver().findElement(By.className("v-table-body"));
        new Actions(getDriver()).moveToElement(e).contextClick(e).perform();
        getDriver().findElement(By.xpath("//*[text() = 'Add Comment']"))
                .click();

        // Use TestBench helper to show tooltip (practically moves mouse over
        // the specified element and waits until tooltip is visible)
        TestBenchElementCommands testBenchElement = testBenchElement(getDriver()
                .findElement(By.xpath("//*[text() = 'Add']")));
        testBenchElement.showTooltip();

        // Verify the tooltip showed contains the expected text
        String tooltipText = driver.findElement(By.className("v-tooltip"))
                .getText();
        assertEquals("Clicking this button will add a comment row to log.",
                tooltipText);

    }

    private void openCalculator() {
        getDriver().get(baseUrl + "/demo-site/Calc?restartApplication");
    }

    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }

}
