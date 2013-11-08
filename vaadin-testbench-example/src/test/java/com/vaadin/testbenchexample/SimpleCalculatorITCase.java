package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

/**
 * This is an example of very simple TestBench usage.
 * <p>
 * This kind of simple tests can easily be written, but also they can be
 * recorded with the Recorder.
 */
public class SimpleCalculatorITCase extends TestBase {

    private WebElement getButton(String caption) {
        return getElementByCaption(Button.class, caption);
    }

    private void calculateOnePlusTwo() {
        // We use the getButton shortcut we defined earlier so we don't have to
        // write getElementByCaption(Button.class, caption) every time
        getButton("1").click();
        getButton("+").click();
        getButton("2").click();
        getButton("=").click();
    }

    @Test
    public void testOnePlusTwo() throws Exception {

        // Simulated user clicking "1+2=", see method for details
        calculateOnePlusTwo();

        // Verify the expected result
        assertEquals("3.0", getElement(TextField.class).getAttribute("value"));
    }
}
