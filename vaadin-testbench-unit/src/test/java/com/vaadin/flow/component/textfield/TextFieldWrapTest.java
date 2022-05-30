/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.textfield;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.testbench.unit.UIUnitTest;

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

        final TextFieldWrap tf_ = wrap(TextFieldWrap.class, tf);

        Assertions.assertFalse(tf_.isUsable(),
                "Read only TextField shouldn't be usable");
    }

    @Test
    public void readOnlyTextField_automaticWrapper_readOnlyIsCheckedInUsable() {
        TextField tf = new TextField();
        tf.setReadOnly(true);
        getCurrentView().getElement().appendChild(tf.getElement());

        Assertions.assertFalse(wrap(tf).isUsable(),
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

        final TextFieldWrap tf_ = wrap(TextFieldWrap.class, tf);
        final String newValue = "Test";
        tf_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

    @Test
    public void nonInteractableField_throwsOnSetValue() {
        TextField tf = new TextField();
        getCurrentView().getElement().appendChild(tf.getElement());

        tf.getElement().setEnabled(false);
        final TextFieldWrap tf_ = wrap(TextFieldWrap.class, tf);

        Assertions.assertThrows(IllegalStateException.class,
                () -> tf_.setValue("fail"),
                "Setting value to a non interactable field should fail");
    }

    @Test
    void textFieldWithValidation_doNotPreventInvalid_doNotThrow() {
        TextField tf = new TextField();
        // Only accept numbers
        tf.setPattern("\\d*");
        getCurrentView().getElement().appendChild(tf.getElement());

        final TextFieldWrap<TextField, String> tf_ = wrap(tf);
        tf_.setValue("Invalid value, but doesn't throw");
    }

    @Test
    public void textFieldWithPattern_patternIsValidated() {
        TextField tf = new TextField();
        tf.setPreventInvalidInput(true);
        // Only accept numbers
        tf.setPattern("\\d*");
        getCurrentView().getElement().appendChild(tf.getElement());

        final TextFieldWrap<TextField, String> tf_ = wrap(tf);
        tf_.setValue("1234");

        Assertions.assertEquals("1234", tf.getValue());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tf_.setValue("hello"),
                "Value should have been validated against pattern");
    }

    @Test
    public void textFieldWithMinLength_lengthIsChecked() {
        TextField tf = new TextField();
        tf.setPreventInvalidInput(true);
        // Only accept numbers
        tf.setMinLength(5);
        getCurrentView().getElement().appendChild(tf.getElement());

        final TextFieldWrap<TextField, String> tf_ = wrap(tf);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tf_.setValue("1234"),
                "Value should have been validated against minLength");
    }

    @Test
    public void textFieldWithMaxLength_lengthIsChecked() {
        TextField tf = new TextField();
        tf.setPreventInvalidInput(true);
        // Only accept numbers
        tf.setMaxLength(3);
        getCurrentView().getElement().appendChild(tf.getElement());

        final TextFieldWrap<TextField, String> tf_ = wrap(tf);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tf_.setValue("1234"),
                "Value should have been validated against maxLength");
    }

    @Test
    public void textFieldWithRequired_valueIsChecked() {
        TextField tf = new TextField();
        tf.setPreventInvalidInput(true);
        // Only accept numbers
        tf.setRequired(true);
        getCurrentView().getElement().appendChild(tf.getElement());

        final TextFieldWrap<TextField, String> tf_ = wrap(tf);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tf_.setValue(""),
                "Required field should not accept empty");
    }

}
