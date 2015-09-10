package com.vaadin.tests.testbenchapi.components.combobox;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testUI.ComboBoxInputNotAllowed;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class ComboBoxInputNotAllowedIT extends MultiBrowserTest {

    @Test
    public void selectByTextTest() {
        openTestURL("theme=reindeer");
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        String expected = ComboBoxInputNotAllowed.SELECTED_ITEM;
        // select the item
        cb.selectByText(ComboBoxInputNotAllowed.SELECTED_ITEM);
        String actual = cb.getValue();
        Assert.assertEquals(expected, actual);
    }
}
