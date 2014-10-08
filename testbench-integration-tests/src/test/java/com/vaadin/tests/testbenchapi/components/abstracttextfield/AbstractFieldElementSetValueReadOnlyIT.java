package com.vaadin.tests.testbenchapi.components.abstracttextfield;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.AbstractComponentElement.ReadOnlyException;
import com.vaadin.testbench.elements.AbstractFieldElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.testbench.elements.RichTextAreaElement;
import com.vaadin.testbench.elements.SliderElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TreeElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class AbstractFieldElementSetValueReadOnlyIT extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test(expected = ReadOnlyException.class)
    public void testComboBox() {
        ComboBoxElement elem = $(ComboBoxElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testListSelectField() {
        ListSelectElement elem = $(ListSelectElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testNativeSelect() {
        NativeSelectElement elem = $(NativeSelectElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testOptionGroup() {
        OptionGroupElement elem = $(OptionGroupElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testTable() {
        TableElement elem = $(TableElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testTree() {
        TreeElement elem = $(TreeElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testTwinColSelect() {
        TwinColSelectElement elem = $(TwinColSelectElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testTextField() {
        TextFieldElement elem = $(TextFieldElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testTextArea() {
        TextAreaElement elem = $(TextAreaElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testPasswordField() {
        PasswordFieldElement elem = $(PasswordFieldElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testCheckBox() {
        CheckBoxElement elem = $(CheckBoxElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testDateField() {
        DateFieldElement elem = $(DateFieldElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testProgressBar() {
        ProgressBarElement elem = $(ProgressBarElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testRichTextArea() {
        RichTextAreaElement elem = $(RichTextAreaElement.class).first();
        testAbstractField(elem);
    }

    @Test(expected = ReadOnlyException.class)
    public void testSlider() {
        SliderElement elem = $(SliderElement.class).first();
        testAbstractField(elem);
    }

    // helper methods
    private void testAbstractField(AbstractFieldElement elem) {
        // The method should raise a ReadOnlyException, that's why
        // we don't care which value to set
        elem.setValue("");
    }
}
