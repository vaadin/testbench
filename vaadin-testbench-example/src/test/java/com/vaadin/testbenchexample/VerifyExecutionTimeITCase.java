package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;

/**
 * This case demonstrates usage of execution time reporting.
 */
public class VerifyExecutionTimeITCase extends TestBase {

    private WebElement getButton(String caption) {
        return $(ButtonElement.class).caption(caption).first();
    }

    private void calculateOnePlusTwo() {
        // We use the getButton shortcut we defined earlier so we don't have to
        // write getElementByCaption(Button.class, caption) every time
        getButton("1").click();
        getButton("+").click();
        getButton("2").click();
        getButton("=").click();
    }

    /**
     * Does the same thing as in {@link SimpleCalculatorITCase} and verifies
     * server don't spend too much time during the process. Also the test makes
     * sure the time spent by browser to render the UI within sane limits.
     * 
     * @throws Exception
     */
    @Test
    public void verifyServerExecutionTime() throws Exception {
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
        if (totalTimeSpentRendering > 400) {
            fail("Rendering UI shouldn't take " + totalTimeSpentRendering
                    + "ms!");
        }

        assertEquals("3.0",
                $(TextFieldElement.class).first().getAttribute("value"));
    }
}
