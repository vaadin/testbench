/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.textfield.TextFieldWrap;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.textfield.TextField;

public class TextFieldWrapTest extends UIUnitTest {

    @Override
    protected String scanPackage() {
        return "com.example";
    }

    @Test
    public void readOnlyTextField_isNotUsable() {
        TextField tf = new TextField();
        tf.setReadOnly(true);
        getCurrentView().getElement().appendChild(tf.getElement());

        final TextFieldWrap tf_ = $(TextFieldWrap.class, tf);

        Assertions.assertFalse(tf_.isUsable(),
                "Read only TextField shouldn't be usable");
    }

    @Test
    public void readOnlyTextField_automaticWrapper_readOnlyIsCheckedInUsable() {
        TextField tf = new TextField();
        tf.setReadOnly(true);
        getCurrentView().getElement().appendChild(tf.getElement());

        Assertions.assertFalse($(tf).isUsable(),
                "Read only TextField shouldn't be usable");
    }

    @Test
    public void setTextFieldValue_eventIsFired_valueIsSet() {
        TextField tf = new TextField();
        getCurrentView().getElement().appendChild(tf.getElement());

        AtomicReference<String> value = new AtomicReference<>(null);

        tf.addValueChangeListener(
                (ValueChangeListener<ComponentValueChangeEvent<TextField, String>>) event -> {
                    value.compareAndSet(null, event.getValue());
                });

        final TextFieldWrap tf_ = $(TextFieldWrap.class, tf);
        final String newValue = "Test";
        tf_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

    @Test
    public void nonInteractableField_throwsOnSetValue() {
        TextField tf = new TextField();
        getCurrentView().getElement().appendChild(tf.getElement());

        tf.getElement().setEnabled(false);
        final TextFieldWrap tf_ = $(TextFieldWrap.class, tf);

        Assertions.assertThrows(IllegalStateException.class,
                () -> tf_.setValue("fail"),
                "Setting value to a non interactable field should fail");
    }
}
