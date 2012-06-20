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

public class AdvancedCommandsITCase extends TestBenchTestCase {

    private static final String COMMENT_TEXT = "Next we'll click button 2";
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        setDriver(TestBench.createDriver(new FirefoxDriver()));
        baseUrl = "http://localhost:8080";
    }

    @Test
    public void useContextMenuToAddCommentRow() throws Exception {
        openCalculator();
        getDriver().findElement(By.id("button_1")).click();
        getDriver().findElement(By.id("button_+")).click();
        WebElement e = getDriver().findElement(By.className("v-table-body"));
        new Actions(getDriver()).moveToElement(e).contextClick(e).perform();
        getDriver().findElement(By.xpath("//*[text() = 'Add Comment']"))
                .click();

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

    @Test
    public void verifyAddCommentButtonHasProperTooltip() throws Exception {
        openCalculator();
        WebElement e = getDriver().findElement(By.className("v-table-body"));
        new Actions(getDriver()).moveToElement(e).contextClick(e).perform();
        getDriver().findElement(By.xpath("//*[text() = 'Add Comment']"))
                .click();

        TestBenchElementCommands testBenchElement = testBenchElement(getDriver().findElement(
                By.xpath("//*[text() = 'Add']")));

        testBenchElement.showTooltip();

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
