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
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.OptionGroup")
@Deprecated
public class OptionGroupElement extends AbstractSelectElement {

    private static org.openqa.selenium.By bySelectOption = By
            .className("v-select-option");
    private static org.openqa.selenium.By byLabel = By.tagName("label");
    private static org.openqa.selenium.By byRadioInput = By.tagName("input");

    public List<String> getOptions() {
        List<String> optionTexts = new ArrayList<String>();
        List<WebElement> options = findElements(bySelectOption);
        for (WebElement option : options) {
            optionTexts.add(option.findElement(byLabel).getText());
        }
        return optionTexts;
    }

    public void selectByText(String text) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        List<WebElement> options = findElements(bySelectOption);
        for (WebElement option : options) {
            if (text.equals(option.findElement(byLabel).getText())) {
                WebElement input = option.findElement(byRadioInput);
                if (isFirefox()) {
                    // When using Valo, the input element is covered by a
                    // pseudo-element, which Firefox will complain about
                    getTestBenchCommandExecutor().executeScript(
                            "arguments[0].click()", input);
                } else if (isChrome()) {
                    ((TestBenchElementCommands) (input)).click(0, 0);
                } else {
                    input.click();
                }
            }
        }
    }

    /**
     * Return value of the selected option in the option group
     *
     * @return value of the selected option in the option group
     */
    public String getValue() {
        List<WebElement> options = findElements(bySelectOption);
        for (WebElement option : options) {
            WebElement checkedItem;
            checkedItem = option.findElement(By.tagName("input"));
            String checked = checkedItem.getAttribute("checked");
            if (checked != null
                    && checkedItem.getAttribute("checked").equals("true")) {
                return option.findElement(By.tagName("label")).getText();
            }
        }
        return null;
    }

    /**
     * Select option in the option group with the specified value
     *
     * @param chars
     *            value of the option in the option group which will be selected
     */
    public void setValue(CharSequence chars) throws ReadOnlyException {
        selectByText((String) chars);
    }

    /**
     * Clear operation is not supported for Option Group. This operation has no
     * effect on Option Group element.
     */
    @Override
    public void clear() {
        super.clear();
    }
}
