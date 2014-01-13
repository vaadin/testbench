package com.vaadin.testbenchexample.pageobjectexample.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;

/**
 * This page object knows how to retrieve individual log lines from the
 * calculator log table.
 */
public class LogPageObject extends TestBenchTestCase {

    public LogPageObject(WebDriver driver) {
        setDriver(driver);
    }

    /**
     * Returns the text contents of the specified log row.
     * 
     * @param row
     *            the row index
     * @return the text at the specified row.
     */
    public String getRow(int row) {
        return findRowElement(row).getText();
    }

    /**
     * Finds the Nth row element
     * 
     * @param row
     *            the index of the row element
     * @return the Nth row element.
     */
    private WebElement findRowElement(int row) {

        // To get at the CELL specified by a certain row and column in a Table,
        // we need to select them both, in the specific order of row first, then
        // col.
        return $(TableElement.class).first().getCell(row, 0);
    }

    /**
     * Opens the context menu and clicks "Add Comment" in the menu.
     * 
     * @return An AddCommentPageObject to interact with the add comment window.
     */
    public AddCommentPageObject openAddCommentWindow() {
        $(ButtonElement.class).caption("Add Comment").first().click();
        return PageFactory
                .initElements(getDriver(), AddCommentPageObject.class);
    }

}
