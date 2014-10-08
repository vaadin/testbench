package com.vaadin.testbench.elements;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.exceptions.NoSuchColumnException;

public class TableRowElement extends AbstractComponentElement {

    WebElement actualElement;

    public TableRowElement(WebElement actualElement) {
        this.actualElement = actualElement;
    }

    /**
     * Returns cell from current row by index. Returns the same element as
     * $(TableElement.class).first().getCell(row, col).
     *
     * @see com.vaadin.testbench.elements.TableElement#getCell(int, int)
     * @param col
     *            column index
     * @return cell from current row by index.
     */
    public TestBenchElement getCell(int col) {
        List<WebElement> cells = actualElement.findElements(By.tagName("td"));
        if (col >= cells.size()) {
            throw new NoSuchColumnException();
        }

        WebElement cellContent = cells.get(col);
        return wrapElement(cellContent.findElement(By.xpath("./*")),
                getCommandExecutor());
    }

    /**
     * Returns the element in the cell, wrapped to the provided Element type.
     *
     * <p>
     * For example getElementInCell(1,ButtonElement.class) will return a
     * ButtonElement, getElementInCell(1,ComboBoxElement.class) ComboBoxElement
     * and so on.
     *
     * @param col
     *            column index
     * @param elementClass
     *            type to which result will be wrapped
     * @return the element in the cell, wrapped to the provided Element type.
     */
    public <T extends AbstractComponentElement> T getElementInCell(int col,
            Class<T> elementClass) {
        WebElement cell = getCell(col);
        List<WebElement> elements = cell.findElements(By.xpath("./*"));
        if (elements.size() == 0) {
            return null;
        }
        WebElement elem = elements.get(0);
        TestBenchElement tbElem = TestBenchElement.wrapElement(elem,
                getCommandExecutor());

        return tbElem.wrap(elementClass);
    }
}
