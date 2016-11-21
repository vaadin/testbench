/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.testbenchapi.components.combobox;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

/**
 * Validates ComboBox.selectByText(String s) works properly if input String s
 * contains parentheses
 */
public class SelectByTextIT extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void selectByParenthesesOnly() {
        String text = "(";
        selectByText(text);
        assertEquals(text, getComboBoxValue());
    }

    @Test
    public void selectByStartingParentheses() {
        String text = "(Value";
        selectByText(text);
        assertEquals(text, getComboBoxValue());
    }

    @Test
    public void selectByFinishingParentheses() {
        String text = "Value(";
        selectByText(text);
        assertEquals(text, getComboBoxValue());
    }

    @Test
    public void selectByRegularParentheses() {
        String text = "Value(i)";
        selectByText(text);
        assertEquals(text, getComboBoxValue());
    }

    @Test
    public void selectByComplexParenthesesCase() {
        String text = "((Test my ) selectByTest() method(with' parentheses)((";
        selectByText(text);
        assertEquals(text, getComboBoxValue());
    }

    private void selectByText(String text) {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        comboBox.selectByText(text);
    }

    private String getComboBoxValue() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        WebElement textbox = comboBox.findElement(By.vaadin("#textbox"));
        return textbox.getAttribute("value");
    }

    @Test
    public void selectSharedPrefixOption() {
        for (String text : new String[] { "Value 2", "Value 22",
                "Value 222" }) {
            selectByText(text);
            assertEquals(text, getComboBoxValue());
            assertEquals("Value is now '" + text + "'",
                    $(LabelElement.class).last().getText());
        }
    }

}
