package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TextFieldElement;

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
        // can be uniquely identified by its caption.
        $(ButtonElement.class).caption("1").first().click();

        // ElementQueries allow you to define a search hierarchy. Searching for
        // a button with given caption in a specific container can be done with
        // .in() or .childOf() function. We are looking for a button with
        // caption "+" somewhere inside a Panel with caption "Calculator".
        $(PanelElement.class).caption("Calculator").$(ButtonElement.class)
                .caption("+").first().click();

        // You can also get all the ButtonElements with .all(). You get a list
        // of Elements that can be used in many ways. Filtering with
        // ElementQuery features can be used to limit the results. This will
        // find all direct children of Keypad (which is a GridLayout).
        for (ButtonElement button : $(GridLayoutElement.class).$$(
                ButtonElement.class).all()) {
            if ("2".equals(button.getText())) {
                button.click();
            }
        }

        // If $() doesn't work for you, or you know exactly what kind of
        // selector string you want to enter, you can do so manually using
        // Selenium like By.vaadin(String vaadinSelector).
        getDriver().findElement(By.vaadin("//VButton[caption='=']")).click();

        // Finally, test that we actually got the right answer from our clicking
        // with the different ways of selecting buttons. Here, we get the first
        // TextField Element we come across, and read its "value" attribute.
        assertEquals("3.0",
                $(TextFieldElement.class).first().getAttribute("value"));
    }

    /**
     * XPath queries are Selenium's way of finding elements with a rather
     * complex but powerful syntax. You can use the classic Selenium style
     * .findElements(By.xpath())
     * 
     * @throws AssertionError
     * @throws IOException
     */
    @Test
    public void onePlusTwoWithXPathSelectors() throws IOException,
            AssertionError {

        // Find an element whose text is exactly '1', that should be "button 1"
        // and click it, ...
        findElement(By.xpath("//*[text() = '1']")).click();

        // Find the element that has been given the exact id "button_+", and
        // click that
        findElement(By.xpath("//*[@id = 'button_+']")).click();

        // Here we do the same thing as with button 1, except we do it the old
        // Selenium way, by calling the .findElement() method on the applicable
        // search context (in this case, the driver).
        findElement(By.xpath("//*[text() = '2']")).click();

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
        assertEquals(
                "3.0",
                findElement(
                        By.xpath("//input[contains(@class, 'v-textfield')]"))
                        .getAttribute("value"));
    }
}
