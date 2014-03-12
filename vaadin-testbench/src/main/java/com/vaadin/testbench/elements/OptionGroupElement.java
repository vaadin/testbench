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

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;

@ServerClass("com.vaadin.ui.OptionGroup")
public class OptionGroupElement extends AbstractSelectElement {

    private static org.openqa.selenium.By byButtonSpan = By
            .className("v-select-option");
    private static org.openqa.selenium.By byLabel = By.tagName("label");
    private static org.openqa.selenium.By byRadioInput = By.tagName("input");

    public List<String> getOptions() {
        List<String> optionTexts = new ArrayList<String>();
        List<WebElement> options = findElements(byButtonSpan);
        for (WebElement option : options) {
            optionTexts.add(option.findElement(byLabel).getText());
        }
        return optionTexts;
    }

    public void selectByText(String text) {
        List<WebElement> options = findElements(byButtonSpan);
        for (WebElement option : options) {
            if (text.equals(option.findElement(byLabel).getText())) {
                option.findElement(byRadioInput).click();
            }
        }
    }
}
