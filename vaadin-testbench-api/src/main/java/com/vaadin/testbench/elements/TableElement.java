/**
 * Copyright (C) 2012 Vaadin Ltd
 * <p/>
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * <p/>
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * <p/>
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.elements;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Table")
@Deprecated
public class TableElement extends AbstractSelectElement {

    /**
     * Function to find a Table cell. Looking for a cell that is currently not
     * visible will throw NoSuchElementException
     *
     * @param row
     *            0 based row index
     * @param column
     *            0 based column index
     * @return TestBenchElement containing wanted cell.
     * @throws NoSuchElementException
     *             if the cell (row, column) is not found.
     */
    public TestBenchElement getCell(int row, int column) {

        TestBenchElement cell = wrapElement(
                findElement(By.vaadin("#row[" + row + "]/col[" + column + "]")),
                getTestBenchCommandExecutor());

        return cell;
    }

    /**
     * Return table row element by zero-based index
     *
     * @return table row element by zero-based index
     */
    public TableRowElement getRow(int row) {
        TestBenchElement rowElem = wrapElement(
                findElement(By.vaadin("#row[" + row + "]")),
                getTestBenchCommandExecutor());
        return rowElem.wrap(TableRowElement.class);
    }

    /**
     * Returns the header cell with the given column index.
     *
     * @param column
     *            0 based column index
     * @return TableHeaderElement containing the wanted header cell
     */
    public TableHeaderElement getHeaderCell(int column) {
        TestBenchElement headerCell = wrapElement(
                findElement(By.vaadin("#header[" + column + "]")),
                getTestBenchCommandExecutor());
        return headerCell.wrap(TableHeaderElement.class);
    }

    /**
     * Function to get footer cell with given column index
     *
     * @param column
     *            0 based column index
     * @return TestBenchElement containing wanted footer cell
     */
    public TestBenchElement getFooterCell(int column) {
        TestBenchElement footerCell = wrapElement(
                findElement(By.vaadin("#footer[" + column + "]")),
                getTestBenchCommandExecutor());
        return footerCell;
    }

    @Override
    public void scroll(int scrollTop) {
        ((TestBenchElement) findElement(By.className("v-scrollable")))
                .scroll(scrollTop);
    }

    @Override
    public void scrollLeft(int scrollLeft) {
        ((TestBenchElement) findElement(By.className("v-scrollable")))
                .scrollLeft(scrollLeft);
    }

    @Override
    public void contextClick() {
        WebElement tbody = findElement(By.className("v-table-body"));
        // There is a problem in with phantomjs driver, just calling
        // contextClick() doesn't work. We have to use javascript.
        if (isPhantomJS()) {
            JavascriptExecutor js = getTestBenchCommandExecutor();
            String scr = "var element=arguments[0];"
                    + "var ev = document.createEvent('HTMLEvents');"
                    + "ev.initEvent('contextmenu', true, false);"
                    + "element.dispatchEvent(ev);";
            js.executeScript(scr, tbody);
        } else {
            new Actions(getDriver()).contextClick(tbody).build().perform();
        }
    }

    public static class ContextMenuElement extends AbstractElement {

        public WebElement getItem(int index) {
            return findElement(By.xpath(".//table//tr[" + (index + 1)
                    + "]//td/*"));
        }

    }

    /**
     * Fetches the context menu for the table
     *
     * @return {@link com.vaadin.testbench.elements.TableElement.ContextMenuElement}
     * @throws java.util.NoSuchElementException if the menu isn't open
     */
    public ContextMenuElement getContextMenu() {
        try {
            WebElement cm = getDriver().findElement(By.className("v-contextmenu"));
            return wrapElement(cm, getTestBenchCommandExecutor()).wrap(
                    ContextMenuElement.class);
        } catch (WebDriverException e) {
            throw new NoSuchElementException("Context menu not found", e);
        }
    }


}
