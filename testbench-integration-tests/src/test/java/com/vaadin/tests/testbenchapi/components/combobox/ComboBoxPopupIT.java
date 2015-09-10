package com.vaadin.tests.testbenchapi.components.combobox;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testUI.ComboBoxUI;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class ComboBoxPopupIT extends MultiBrowserTest {

    private ComboBoxElement comboBoxElement;

    @Override
    protected Class<?> getUIClass() {
        return ComboBoxUI.class;
    }

    @Before
    public void init() {
        openTestURL();
        comboBoxElement = $(ComboBoxElement.class).first();
    }

    @Test
    public void comboBoxPopup_popupOpen_popupFetchedSuccessfully() {
        comboBoxElement.openPopup();

        assertNotNull(comboBoxElement.getSuggestionPopup());
    }

    @Test
    public void comboBoxPopup_popupClosed_popupFetchedSuccessfully() {
        assertNotNull(comboBoxElement.getSuggestionPopup());
    }
}
