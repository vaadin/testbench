package com.vaadin.testbench.elements;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.exceptions.NoSuchColumnException;

public class TableRowElement extends AbstractComponentElement {

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
        List<WebElement> cells = getWrappedElement().findElements(
                By.tagName("td"));
        if (col >= cells.size()) {
            throw new NoSuchColumnException();
        }

        WebElement cellContent = cells.get(col);
        return wrapElement(cellContent.findElement(By.xpath("./*")),
                getCommandExecutor());
    }
}
