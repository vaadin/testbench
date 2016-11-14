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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Tree")
@Deprecated
public class TreeElement extends AbstractSelectElement {
    /**
     * Returns selected item of the tree. In multiselect mode returns first
     * selected item. If there is no selected item returns empty string
     *
     * @return selected item of the tree
     */
    public String getValue() {
        List<WebElement> selectedElements = findElements(By
                .className("v-tree-node-selected"));
        if (selectedElements.isEmpty()) {
            return "";
        } else {
            return selectedElements.get(0).getText();
        }
    }
}
