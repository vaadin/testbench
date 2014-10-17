package com.vaadin.testbench.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class TableHeaderElement extends AbstractComponentElement {

    @Override
    public String getCaption() {
        WebElement captionElement = findElement(By
                .className("v-table-caption-container"));
        return captionElement.getText();
    }

    /**
     * Returns column resize handle.
     *
     * You can resize column by using selenium Actions i.e. new
     * Actions(getDriver()).clickAndHold(handle).moveByOffset(x,
     * y).release().build().perform();
     *
     * @return column resize handle
     */
    public WebElement getResizeHandle() {
        return findElement(By.className("v-table-resizer"));
    }
}
