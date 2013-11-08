package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

/**
 * This example demonstrates usege of varios element selection methods.
 */
public class SelectorExamplesITCase extends TestBase {

    /**
     * This example calculates 1+2 using different finding alternatives. It is a
     * demonstration of some of the ways of using TestBench4 element finders.
     * 
     * @throws Exception
     */
    @Test
    public void testButtonFindingAlternatives() throws Exception {

        // When IDs are not available, this is the simplest and generally
        // preferred way of finding a Vaadin widget in your application when it
        // can be uniquely identified by its caption. getElementByCaption is a
        // convenience function provided by TestBenchTestCase.
        getElementByCaption(Button.class, "1").click();

        // This is a slightly more complex alternative, allowing you to build
        // your own selector string by chaining together ComponentFinder
        // identifier calls. When you're done specifying how to identify the
        // element (in this case, withCaption()), call done() to have TestBench
        // try to fetch your Element.
        find(Button.class, getDriver()).withCaption("+").done().click();

        // If find() doesn't work for you, or you know exactly what kind of
        // selector string you want to enter, you can do so manually.
        // getElementByPath() is a convenience function provided by
        // TestBenchTestCase, which calls By.vaadin() with the given selector
        // and a suitable search context.
        getElementByPath("//VButton[caption='2']").click();

        // If all else fails, you can find elements "the classic Selenium way",
        // by calling findElements on a SearchContext directly, and specifying a
        // suitable By instance (in this case, ByVaadin), and using the results
        // as usual.
        getDriver().findElements(By.vaadin("//VButton[caption='=']")).get(0)
                .click();

        // Finally, test that we actually got the right answer from our clicking
        // with the different ways of selecting buttons. Here, we get the first
        // TextField Element we come across, and read its "value" attribute.
        assertEquals("3.0", getElement(TextField.class).getAttribute("value"));
    }

    /**
     * XPath queries are Selenium's way of finding elements with a rather
     * complex but powerful syntax. You can easily use XPath searches through
     * TestBenchTestCases convenience function .getElementByXPath() or you can
     * use the classic Selenium style .findElements(By.xpath())
     * 
     * @throws AssertionError
     * @throws IOException
     */
    @Test
    public void onePlusTwoWithXPathSelectors() throws IOException,
            AssertionError {

        // Find an element whose text is exactly '1', that should be "button 1"
        // and click it, ...
        getElementByXPath("//*[text() = '1']").click();

        // Find the element that has been given the exact id "button_+", and
        // click that
        getElementByXPath("//*[@id = 'button_+']").click();

        // Here we do the same thing as with button 1, except we do it the old
        // Selenium way, by calling the .findElement() method on the applicable
        // search context (in this case, the driver).
        getDriver().findElement(By.xpath("//*[text() = '2']")).click();

        // findElements returns a list of all matching elements. We can use this
        // to find the element we're looking for - in this case, we want to find
        // an element with the exact text "=". As soon as we find it, we send a
        // click() request to that element and exit the loop.
        List<WebElement> buttons = getDriver().findElements(
                By.xpath("//*[contains(@class, 'v-button')]"));
        for (WebElement e : buttons) {
            if (e.getText().equals("=")) {
                e.click();
                break;
            }
        }

        // Vaadin TextFields are implemented as input elements - we'll find an
        // input element that has a specified class value of 'v-textfield', and
        // then retrieve its 'value' attribute. This should be the Vaadin
        // TextField's displayed text.
        assertEquals("3.0",
                getElementByXPath("//input[contains(@class, 'v-textfield')]")
                        .getAttribute("value"));
    }
}
