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

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.AbstractTextField")
public class AbstractTextFieldElement extends AbstractFieldElement {

    /**
     * Return value of the field element
     *
     * @return value of the field element
     */
    public String getValue() {
        return findElement(By.tagName("input")).getAttribute("value");
    }

    /**
     * Set value of the field element
     *
     * @param chars
     *            new value of the field
     */
    public void setValue(CharSequence chars) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        clearElementClientSide(this);
        focus();
        sendKeys(chars);
        sendKeys(Keys.TAB);
    }
}
