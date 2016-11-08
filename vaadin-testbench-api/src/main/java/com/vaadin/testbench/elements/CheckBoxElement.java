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
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.CheckBox")
public class CheckBoxElement extends AbstractFieldElement {

    /**
     * Return string representation of value of the checkbox Return either
     * checked or unchecked
     */
    public String getValue() {
        if (isChecked()) {
            return "checked";
        } else {
            return "unchecked";
        }
    }

    /**
     * Checks if the checkbox is checked.
     *
     * @return <code>true</code> if the checkbox is checked, <code>false</code>
     *         otherwise.
     */
    public boolean isChecked() {
        return getInputElement().isSelected();
    }

    /**
     * Clears the check box, setting unchecked value. The check box is unchecked
     * by sending a click event on it.
     *
     */
    @Override
    public void clear() {
        if (isChecked()) {
            click();
        }
    }

    @Override
    public String getCaption() {
        WebElement elem = findElement(By.xpath(".."))
                .findElement(By.tagName("label"));
        return elem.getText();
    }

    @Override
    public void click() {
        WebElement input = getInputElement();
        if (isFirefox()) {
            // When using Valo, the input element is covered by a
            // pseudo-element, which Firefox will complain about
            getTestBenchCommandExecutor().executeScript("arguments[0].click()",
                    input);
        } else if (isChrome()) {
            ((TestBenchElementCommands) (input)).click(0, 0);
        } else {
            input.click();
        }
    }

    /**
     * Gets the &lt;input&gt; element of the checkbox.
     *
     * @return the input element
     */
    public WebElement getInputElement() {
        return findElement(By.tagName("input"));
    }
}
