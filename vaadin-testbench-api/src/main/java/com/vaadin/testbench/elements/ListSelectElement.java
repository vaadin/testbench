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

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.ListSelect")
public class ListSelectElement extends AbstractSelectElement {

    private Select select;
    private static By bySelect = By.tagName("select");

    @Override
    protected void init() {
        super.init();
        select = new Select(findElement(bySelect));
    }

    public void selectByText(String text) {
        select.selectByVisibleText(text);
    }

    public void deselectByText(String text) {
        select.deselectByVisibleText(text);
    }

    public List<String> getOptions() {
        List<String> options = new ArrayList<String>();
        for (WebElement webElement : select.getOptions()) {
            options.add(webElement.getText());
        }
        return options;
    }

    /**
     * Clear operation is not supported for List Select. This operation has no
     * effect on List Select element.
     */
    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public String getValue() {
        return select.getFirstSelectedOption().getText();
    }
}
