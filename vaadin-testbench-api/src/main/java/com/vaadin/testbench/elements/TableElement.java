/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.elements;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Table")
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
     */
    public TestBenchElement getCell(int row, int column) {

        TestBenchElement cell = wrapElement(
                findElement(By.vaadin("#row[" + row + "]/col[" + column + "]")),
                getCommandExecutor());

        return cell;
    }

    /**
     * Function to get header cell with given column index
     * 
     * @param column
     *            0 based column index
     * @return TestBenchElement containing wanted header cell
     */
    public TestBenchElement getHeaderCell(int column) {
        TestBenchElement headerCell = wrapElement(
                findElement(By.vaadin("#header[" + column + "]")),
                getCommandExecutor());
        return headerCell;
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
                getCommandExecutor());
        return footerCell;
    }

    /**
     * Return value of selected value or first selected item in multiselect mode
     */
    @Override
    public String getValue() {
        throw new UnsupportedOperationException(
                "Implement table get Value. Ticket #14498");
    }
}
