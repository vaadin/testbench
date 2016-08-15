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

import java.util.List;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Button")
public class ButtonElement extends AbstractComponentElement {

    @Override
    public String getCaption() {
        WebElement captElem = findElement(By.className("v-button-caption"));
        return captElem.getText();
    }

    private boolean tryClickChild(WebElement e) {
        List<WebElement> children = e.findElements(By.xpath(".//*"));
        for (WebElement c : children) {
            if (c.isDisplayed()) {
                c.click();
                return true;
            } else {
                if (tryClickChild(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void click() {
        if (!isDisplayed()) {
            if (tryClickChild(this)) {
                return;
            }
        }

        super.click();
    }
}
