package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/**
 * This example contains some usages of bit more advanced TestBench and
 * WebDriver usage.
 * 
 */
public class AdvancedCommandsITCase extends TestBase {

    private static final String COMMENT_TEXT = "Next we'll click button 2";

    private WebElement getButton(String caption) {
        return getElementByCaption(Button.class, caption);
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

        // Get some buttons using getButton(String caption) function defined
        // earlier.
        WebElement oneButton = getButton("1");
        WebElement addButton = getButton("+");

        // Click them
        oneButton.click();
        addButton.click();

        // We fill in a comment and verify the commenting feature works as
        // expected.
        getButton("Add Comment").click();
        WebElement commentField = getElement(TextField.class,
                getElement(Window.class));

        // Make sure the input is empty
        commentField.clear();

        // Sending Keys.RETURN updates the input value and triggers
        // a Shortcut clicking the OK button for us.
        commentField.sendKeys(COMMENT_TEXT, Keys.RETURN);

        // Ensure window is closed
        boolean windowPresent = isElementPresent(Window.class);
        if (windowPresent) {
            fail("Modal window prompting textfield was not properly closed");
        }

        // Click a few more buttons
        getButton("2").click();
        getButton("=").click();

        // Check that the display is correct (1 + 2 = 3)
        assertEquals("3.0", getElement(TextField.class).getAttribute("value"));

        // Verify the second row in log contains our comment
        // Uses Vaadin table selector with subpart.
        String secondRowText = getElementByPath("//VScrollTable#row[1]/col[0]")
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

        // using the button, show the popup to fill in a comment
        getButton("Add Comment").click();

        // Use TestBench helper to show tooltip (practically moves mouse over
        // the specified element and waits until tooltip is visible)
        TestBenchElementCommands testBenchElement = testBenchElement(getButton("OK"));
        testBenchElement.showTooltip();

        // Verify the tooltip showed contains the expected text
        String tooltipText = driver.findElement(By.className("v-tooltip"))
                .getText();
        assertEquals("Clicking this button will add a comment row to log.",
                tooltipText);

    }
}
