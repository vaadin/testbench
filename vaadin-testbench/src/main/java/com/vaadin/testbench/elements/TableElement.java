package com.vaadin.testbench.elements;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

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

        TestBenchElement cell = wrapElement(findElement(By.vaadin("#row[" + row
                + "]/col[" + column + "]")), getCommandExecutor());

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
        TestBenchElement headerCell = wrapElement(findElement(By.vaadin("#header["
                + column + "]")), getCommandExecutor());
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
        TestBenchElement footerCell = wrapElement(findElement(By.vaadin("#footer["
                + column + "]")), getCommandExecutor());
        return footerCell;
    }
}
