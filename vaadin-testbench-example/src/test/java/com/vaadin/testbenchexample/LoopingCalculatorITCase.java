package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

/**
 * This example demonstrates how developers can use techniques like loops and
 * random data to make their tests more powerful. Using Java this kind of
 * techniques can be easily implemented with your existing skills.
 * <p>
 * Developers should also consider add parameters to their tests and
 * systematically test boundary values etc.
 */
public class LoopingCalculatorITCase extends TestBase {

    private String getDisplayValue() {
        return getElement(TextField.class).getAttribute("value");
    }

    private WebElement getButton(String caption) {
        return getElementByCaption(Button.class, caption);
    }

    @Test
    public void testAdditionWithLoop() throws Exception {
        // Add five ones together and verify that the result is 5.0
        performAdditions(1, 5);
        assertEquals("5.0", getDisplayValue());

        // Clear the calculator and add four twos together
        getButton("C").click();

        performAdditions(2, 4);
        assertEquals("8.0", getDisplayValue());
    }

    /**
     * Clicks the button identified by buttonId followed by the + button
     * timesToAdd times and then clicks the = button to make the calculator
     * display the result.
     * 
     * @param value
     * @param timesToAdd
     * @throws Exception
     */
    private void performAdditions(int value, int timesToAdd) throws Exception {

        assert (value >= 0 && value < 10);

        WebElement numberButton = getButton("" + value);
        WebElement plusButton = getButton("+");

        for (int i = 0; i < timesToAdd - 1; i++) {
            numberButton.click();
            plusButton.click();
        }

        numberButton.click();

        getButton("=").click();
    }
}
