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

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.CheckBox")
public class CheckBoxElement extends AbstractFieldElement {

    /**
     * Return string representation of value of the checkbox Return either
     * checked or unchecked
     */
    @Override
    public String getValue() {
        WebElement elem = findElement(By.tagName("input"));
        if (elem.isSelected()) {
            return "checked";
        } else {
            return "unchecked";
        }
    }

    /**
     * Clears the check box, setting unchecked value. The check box is unchecked
     * by sending a click event on it.
     * 
     */
    @Override
    public void clear() {
        WebElement elem = findElement(By.tagName("input"));
        if (elem.isSelected()) {
            elem.click();
        }
    }

    @Override
    public String getCaption() {
        WebElement elem = findElement(By.xpath("..")).findElement(
                By.tagName("label"));
        return elem.getText();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.testbench.TestBenchElement#click()
     */
    @Override
    public void click() {
        findElement(By.xpath("input")).click();
    }
}
