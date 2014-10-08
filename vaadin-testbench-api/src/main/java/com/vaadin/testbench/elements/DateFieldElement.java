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

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.DateField")
public class DateFieldElement extends AbstractFieldElement {

    /**
     * Clear DateField element
     */
    @Override
    public void clear() {
        WebElement elem = findElement(By.tagName("input"));
        elem.clear();
    }

    @Override
    public void setValue(CharSequence chars) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        WebElement elem = findElement(By.tagName("input"));
        TestBenchElement tbElement = (TestBenchElement) elem;
        clearElementClientSide(tbElement);
        tbElement.sendKeys(chars);
        tbElement.sendKeys(Keys.TAB);
    }
}
