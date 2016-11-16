package com.vaadin.tests.testbenchapi;

import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testUI.ComponentElementGetValue;
import com.vaadin.testbench.elements.AbstractTextFieldElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TwinColSelectElement;

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

public class ComponentElementGetValueIT extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void checkComboBox() {
        ComboBoxElement elem = $(ComboBoxElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkListSelect() {
        ListSelectElement elem = $(ListSelectElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkNativeSelect() {
        NativeSelectElement elem = $(NativeSelectElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkCheckBoxGroup() {
        CheckBoxGroupElement elem = $(CheckBoxGroupElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkRadioButtonGroup() {
        RadioButtonGroupElement elem = $(RadioButtonGroupElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkTwinColSelect() {
        TwinColSelectElement elem = $(TwinColSelectElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkTextField() {
        TextFieldElement elem = $(TextFieldElement.class).get(0);
        checkValue(elem);
    }

    @Test
    public void checkTextArea() {
        TextAreaElement elem = $(TextAreaElement.class).get(0);
        checkValue(elem);
    }

    @Test
    public void checkPassword() {
        PasswordFieldElement elem = $(PasswordFieldElement.class).get(0);
        checkValue(elem);
    }

    @Test
    public void checkCheckBox() {
        CheckBoxElement cb = $(CheckBoxElement.class).get(0);
        String expected = "checked";
        String actual = cb.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkDateField() {
        DateFieldElement df = $(DateFieldElement.class).get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expected = formatter
                .format(ComponentElementGetValue.TEST_DATE_VALUE);
        String actual = df.getValue();
        Assert.assertEquals(expected, actual);
    }

    private void checkValue(AbstractTextFieldElement elem) {
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }
}
