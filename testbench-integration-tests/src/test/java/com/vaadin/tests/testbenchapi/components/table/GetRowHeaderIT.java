package com.vaadin.tests.testbenchapi.components.table;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testUI.TableScroll;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableHeaderElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class GetRowHeaderIT extends MultiBrowserTest {

    TableElement table;
    private static final int COLUMN_INDEX = 1;

    @Override
    protected Class<?> getUIClass() {
        return TableScroll.class;
    }

    @Before
    public void init() {
        openTestURL();
        table = $(TableElement.class).first();
    }

    @Test
    public void testGetRowHeaderCaption() {
        TableHeaderElement header = table.getHeader(COLUMN_INDEX);
        String expected = "PROPERTY1";
        String actual = header.getCaption();
        Assert.assertEquals(
                "TableHeaderElement.getCaption() returns wrong value.",
                expected, actual);
    }

    // Test that clicking on the header sorts the column
    @Test
    public void testTableRowHeaderSort() {
        TableHeaderElement header = table.getHeader(COLUMN_INDEX);
        // sort in asc order
        header.click();
        // sort in desc order
        header.click();
        String expected = "col=1 row=99";
        String actual = table.getCell(0, COLUMN_INDEX).getText();
        Assert.assertEquals(
                "TableHeaderElement.toggleSort() did not sort column "
                        + COLUMN_INDEX, expected, actual);
    }

    @Test
    public void testTableRowHeaderGetHandle() {
        TableHeaderElement header = table.getHeader(COLUMN_INDEX);
        int initialWidth = header.getSize().width;
        WebElement handle = header.getResizeHandle();
        Actions builder = new Actions(getDriver());
        builder.clickAndHold(handle).moveByOffset(-20, 0).release().build()
                .perform();
        header = table.getHeader(COLUMN_INDEX);
        int widthAfterResize = header.getSize().width;
        Assert.assertTrue("The column with index " + COLUMN_INDEX
                + " was not resized.", initialWidth > widthAfterResize);
    }
}
