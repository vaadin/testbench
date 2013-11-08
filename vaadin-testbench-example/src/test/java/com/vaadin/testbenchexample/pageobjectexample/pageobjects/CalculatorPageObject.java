package com.vaadin.testbenchexample.pageobjectexample.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.vaadin.testbench.TestBenchTestCase;

/**
 * The CalculatorPageObject knows how to enter digits and operands
 * in the calculator and read back the display. As a bonus this class
 * is implemented as a fluent interface, with chainable methods.
 *
 * The calculator can also be used by calling only the CalculatorPageObject#enter
 * method with the buttons to press as a sequence of characters (given as a string).
 * Usually there is no need for two different ways of using a page object, but here
 * are two APIs provided as examples of two different ways to create readable tests.
 */
public class CalculatorPageObject extends TestBenchTestCase {

    /*
     * These fields are initialized by PageFactory.initElements(...)
     */
    private WebElement display;

    @FindBy(id = "button_*")
    private WebElement multiply;

    @FindBy(id = "button_/")
    private WebElement divide;

    @FindBy(id = "button_+")
    private WebElement add;

    @FindBy(id = "button_-")
    private WebElement subtract;

    @FindBy(id = "button_=")
    private WebElement equals;

    @FindBy(id = "button_C")
    private WebElement clear;

    /**
     * Constructs a new calculator page object
     *
     * @param driver the driver to use
     */
    public CalculatorPageObject(WebDriver driver) {
        setDriver(driver);
    }

    /**
     * Opens the URL where the calculator resides.
     */
    public void open() {
        getDriver().get("http://localhost:8080/testbenchexample/?restartApplication");
    }

    /**
     * Pushes buttons on the calculator
     *
     * @param buttons the buttons to push, e.g. "1234" or "1+2", etc.
     * @return The same CalculatorPageObject instance for method chaining.
     */
    public CalculatorPageObject enter(String buttons) {
        for (char numberChar : buttons.toCharArray()) {
            pushButton(numberChar);
        }
        return this;
    }

    /**
     * Pushes the specified button.
     *
     * @param button The character representation of the button to push.
     */
    private void pushButton(char button) {
        getDriver().findElement(By.id("button_" + button)).click();
    }

    /**
     * Pushes the divide button.
     *
     * @param number the number to enter after pushing the divide button.
     * @return The same CalculatorPageObject for method chaining.
     */
    public CalculatorPageObject divideBy(String number) {
        divide.click();
        return enter(number);
    }

    /**
     * Pushes the multiplication button and enters the number provided.
     *
     * @param number the number to enter after pushing the multiply button.
     * @return The same CalculatorPageObject for method chaining.
     */
    public CalculatorPageObject multiplyBy(String number) {
        multiply.click();
        return enter(number);
    }

    /**
     * Pushes the add button and enters the number provided.
     *
     * @param number the number to enter after pushing the add button.
     * @return The same CalculatorPageObject for method chaining.
     */
    public CalculatorPageObject add(String number) {
        add.click();
        return enter(number);
    }

    /**
     * Pushes the equals button and returns the contents of the "display".
     *
     * @return The string (number) shown in the "display" of the calculator.
     */
    public String getResult() {
        equals.click();
        return display.getAttribute("value");
    }

    /**
     * Pushes the subtract button and enters the number provided.
     *
     * @param number the number to enter after pushing the subtract button.
     * @return The same CalculatorPageObject for method chaining.
     */
    public CalculatorPageObject subtract(String number) {
        subtract.click();
        return enter(number);
    }
}
