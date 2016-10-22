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
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Link")
public class LinkElement extends AbstractComponentElement {

    @Override
    public void click() {
        getAnchor().click();
    }

    private WebElement getAnchor() {
        return findElement(By.tagName("a"));
    }

    @Override
    public String getCaption() {
        return getAnchor().getText();
    }
}
