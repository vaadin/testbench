package com.vaadin.testbenchexample.pageobjectexample.pageobjects;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchTestCase;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This page object knows how to retrieve individual log lines from the calculator log table.
 */
public class LogPageObject extends TestBenchTestCase {

    public LogPageObject(WebDriver driver) {
        setDriver(driver);
    }

    /**
     * Returns the text contents of the specified log row.
     * @param row the row index
     * @return the text at the specified row.
     */
    public String getRow(int row) {
        return findRowElement(row).getText();
    }

    /**
     * Finds the Nth row element
     *
     * @param row the index of the row element
     * @return the Nth row element.
     */
    private WebElement findRowElement(int row) {
        return getDriver().findElements(By.className("v-table-cell-wrapper")).get(row);
    }

    /**
     * Right clicks on the log table to open the context menu.
     */
    public void openContextMenu() {
        WebElement tableBody = getDriver().findElement(By.className("v-table-body"));
        new Actions(getDriver()).moveToElement(tableBody).contextClick().perform();
    }

    /**
     * Opens the context menu and clicks "Add Comment" in the menu.
     *
     * @return An AddCommentPageObject to interact with the add comment window.
     */
    public AddCommentPageObject openAddCommentWindow() {
        openContextMenu();
        getDriver().findElement(By.xpath("//*[text() = 'Add Comment']")).click();
        return PageFactory.initElements(getDriver(), AddCommentPageObject.class);
    }

}
