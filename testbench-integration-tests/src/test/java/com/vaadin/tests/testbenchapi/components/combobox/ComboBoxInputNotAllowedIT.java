package com.vaadin.tests.testbenchapi.components.combobox;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testUI.ComboBoxInputNotAllowed;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class ComboBoxInputNotAllowedIT extends MultiBrowserTest {

    @Test(expected = NoSuchElementException.class)
    public void selectByTextComboBoxWithTextInputDisabled_invalidSelection() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.selectByText("Foobar");
    }

    @Test
    public void selectByTextComboBoxWithTextInputDisabled() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();

        String[] optionsToTest = new String[] {
                ComboBoxInputNotAllowed.ITEM_ON_FIRST_PAGE,
                ComboBoxInputNotAllowed.ITEM_ON_SECOND_PAGE,
                ComboBoxInputNotAllowed.ITEM_ON_LAST_PAGE,
                ComboBoxInputNotAllowed.ITEM_LAST_WITH_PARENTHESIS,
                ComboBoxInputNotAllowed.ITEM_ON_FIRST_PAGE };

        for (String option : optionsToTest) {
            cb.selectByText(option);
            Assert.assertEquals("Value is now: " + option,
                    $(LabelElement.class).last().getText());
            Assert.assertEquals(option, cb.getValue());
        }
    }
}
