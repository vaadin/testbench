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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;

import static org.junit.jupiter.api.Assertions.*;

class TextAreaWrapTest extends UIUnitTest {

    private TextAreaView view;

    @Override
    protected String scanPackage() {
        return getClass().getPackageName();
    }

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(TextAreaView.class);
        view = navigate(TextAreaView.class);
    }

    @Test
    public void readOnlyTextArea_isNotUsable() {
        view.textArea.setReadOnly(true);

        final TextAreaWrap<TextArea> ta_ = wrap(view.textArea);

        Assertions.assertFalse(ta_.isUsable(),
                "Read only TextArea shouldn't be usable");
    }

    @Test
    public void readOnlyTextArea_automaticWrapper_readOnlyIsCheckedInUsable() {
        view.textArea.setReadOnly(true);

        Assertions.assertFalse(wrap(view.textArea).isUsable(),
                "Read only TextArea shouldn't be usable");
    }

    @Test
    public void setTextAreaValue_eventIsFired_valueIsSet() {

        AtomicReference<String> value = new AtomicReference<>(null);

        view.textArea.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextArea, String>>) event -> {
                    value.compareAndSet(null, event.getValue());
                });

        final TextAreaWrap<TextArea> ta_ = wrap(view.textArea);
        final String newValue = "Test";
        ta_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

    @Test
    public void nonInteractableField_throwsOnSetValue() {

        view.textArea.getElement().setEnabled(false);
        final TextAreaWrap<TextArea> ta_ = wrap(view.textArea);

        Assertions.assertThrows(IllegalStateException.class,
                () -> ta_.setValue("fail"),
                "Setting value to a non interactable field should fail");
    }

    @Test
    void textAreaWithValidation_doNotPreventInvalid_doNotThrow() {
        // Only accept numbers
        view.textArea.setPattern("\\d*");

        final TextAreaWrap<TextArea> ta_ = wrap(view.textArea);
        final String faultyValue = "Invalid value, but doesn't throw";
        ta_.setValue(faultyValue);
        Assertions.assertEquals(faultyValue, view.textArea.getValue(),
                "Value should have been set.");
    }

    @Test
    public void textAreaWithPattern_patternIsValidated() {
        TextArea tf = view.textArea;
        tf.setPreventInvalidInput(true);
        // Only accept numbers
        tf.setPattern("\\d*");

        final TextAreaWrap<TextArea> ta_ = wrap(tf);
        ta_.setValue("1234");

        Assertions.assertEquals("1234", tf.getValue());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ta_.setValue("hello"),
                "Value should have been validated against pattern");
    }

    @Test
    public void textAreaWithMinLength_lengthIsChecked() {
        TextArea tf = view.textArea;
        tf.setPreventInvalidInput(true);
        tf.setMinLength(5);

        final TextAreaWrap<TextArea> ta_ = wrap(tf);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ta_.setValue("1234"),
                "Value should have been validated against minLength");
    }

    @Test
    public void textAreaWithMaxLength_lengthIsChecked() {
        TextArea tf = view.textArea;
        tf.setPreventInvalidInput(true);
        tf.setMaxLength(3);

        final TextAreaWrap<TextArea> ta_ = wrap(tf);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ta_.setValue("1234"),
                "Value should have been validated against maxLength");
    }

    @Test
    public void textAreaWithRequired_valueIsChecked() {
        TextArea tf = view.textArea;
        tf.setPreventInvalidInput(true);
        tf.setRequired(true);

        final TextAreaWrap<TextArea> ta_ = wrap(tf);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ta_.setValue(""),
                "Required field should not accept empty");
    }

}