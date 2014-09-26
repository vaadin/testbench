package com.vaadin.testbenchexample.pageobjectexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbenchexample.pageobjectexample.pageobjects.AddCommentPageObject;
import com.vaadin.testbenchexample.pageobjectexample.pageobjects.CalculatorPageObject;
import com.vaadin.testbenchexample.pageobjectexample.pageobjects.LogPageObject;

/**
 * A simple test case using page objects.
 */
public class PageObjectExampleITCase {

    public static final String COMMENT = "That was a simple calculation";
    private CalculatorPageObject calculator;
    private LogPageObject log;
    private WebDriver driver;

    /**
     * Creates a WebDriver instance and the page objects used.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        driver = TestBench.createDriver(new FirefoxDriver());

        // Use the PageFactory to automatically initialize fields.
        calculator = PageFactory.initElements(driver, CalculatorPageObject.class);
        log = PageFactory.initElements(driver, LogPageObject.class);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    /**
     * @see com.vaadin.testbenchexample.SimpleCalculatorITCase#testOnePlusTwo()
     */
    @Test
    public void testOnePlusTwo() throws Exception {
        calculator.open();
        // Enter 1+2 and verify the result
        assertEquals("3.0", calculator.enter("1").add("2").getResult());
        // Verify the log
        assertEquals("1.0 + 2.0 = 3.0", log.getRow(0));
    }

    @Test
    public void testCalculateWithLongNumbers() throws Exception {
        calculator.open();
        assertEquals("1337.0", calculator.enter("1337").multiplyBy("5").divideBy("5").getResult());
        assertEquals("-4958.0", calculator.enter("42").subtract("5000").getResult());
    }

    @Test
    public void testAlternateAPI() throws Exception {
        calculator.open();
        assertEquals("1337.0", calculator.enter("1337*5/5").getResult());
        assertEquals("-4958.0", calculator.enter("42-5000").getResult());
    }

    /**
     * @see com.vaadin.testbenchexample.AdvancedCommandsITCase#useContextMenuToAddCommentRow()
     */
    @Test
    public void testAddCommentRowToLog() throws Exception {
        calculator.open();
        // just do some math first
        calculator.enter("1+2");
        // Verify the result of the calculation
        assertEquals("3.0", calculator.getResult());

        // Add a comment
        AddCommentPageObject addComment = log.openAddCommentWindow();
        addComment.enterComment(COMMENT);

        // Ensure the comment window is closed
        assertFalse(addComment.isOpen());

        // Verify that the log contains our comment
        assertTrue(log.getRow(1).contains(COMMENT));
    }
}
