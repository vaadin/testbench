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

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
public class TextFieldWrapTest extends UIUnitTest {

    TextFieldView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(TextFieldView.class);
        view = navigate(TextFieldView.class);
    }

    @Test
    public void readOnlyTextField_isNotUsable() {
        view.textField.setReadOnly(true);

        final TextFieldWrap<TextField, String> tf_ = wrap(view.textField);

        Assertions.assertFalse(tf_.isUsable(),
                "Read only TextField shouldn't be usable");
    }

    @Test
    public void readOnlyTextField_automaticWrapper_readOnlyIsCheckedInUsable() {
        view.textField.setReadOnly(true);

        Assertions.assertFalse(wrap(view.textField).isUsable(),
                "Read only TextField shouldn't be usable");
    }

    @Test
    public void setTextFieldValue_eventIsFired_valueIsSet() {

        AtomicReference<String> value = new AtomicReference<>(null);

        view.textField.addValueChangeListener(
                (ValueChangeListener<ComponentValueChangeEvent<TextField, String>>) event -> {
                    value.compareAndSet(null, event.getValue());
                });

        final TextFieldWrap<TextField, String> tf_ = wrap(view.textField);
        final String newValue = "Test";
        tf_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

    @Test
    public void nonInteractableField_throwsOnSetValue() {

        view.textField.getElement().setEnabled(false);
        final TextFieldWrap<TextField, String> tf_ = wrap(view.textField);

        Assertions.assertThrows(IllegalStateException.class,
                () -> tf_.setValue("fail"),
                "Setting value to a non interactable field should fail");
    }

    @Test
    void textFieldWithValidation_doNotPreventInvalid_doNotThrow() {
        // Only accept numbers
        view.textField.setPattern("\\d*");

        final TextFieldWrap<TextField, String> tf_ = wrap(view.textField);
        final String faultyValue = "Invalid value, but doesn't throw";
        tf_.setValue(faultyValue);
        Assertions.assertEquals(faultyValue, view.textField.getValue(),
                "Value should have been set.");
    }

    @Test
    public void textFieldWithPattern_patternIsValidated() {
        TextField tf = view.textField;
        tf.setPreventInvalidInput(true);
        // Only accept numbers
        tf.setPattern("\\d*");

        final TextFieldWrap<TextField, String> tf_ = wrap(tf);
        tf_.setValue("1234");

        Assertions.assertEquals("1234", tf.getValue());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tf_.setValue("hello"),
                "Value should have been validated against pattern");
    }

    @Test
    public void textFieldWithMinLength_lengthIsChecked() {
        TextField tf = view.textField;
        tf.setPreventInvalidInput(true);
        tf.setMinLength(5);

        final TextFieldWrap<TextField, String> tf_ = wrap(tf);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tf_.setValue("1234"),
                "Value should have been validated against minLength");
    }

    @Test
    public void textFieldWithMaxLength_lengthIsChecked() {
        TextField tf = view.textField;
        tf.setPreventInvalidInput(true);
        tf.setMaxLength(3);

        final TextFieldWrap<TextField, String> tf_ = wrap(tf);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tf_.setValue("1234"),
                "Value should have been validated against maxLength");
    }

    @Test
    public void textFieldWithRequired_valueIsChecked() {
        TextField tf = view.textField;
        tf.setPreventInvalidInput(true);
        tf.setRequired(true);

        final TextFieldWrap<TextField, String> tf_ = wrap(tf);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tf_.setValue(""),
                "Required field should not accept empty");
    }

    @Test
    void testFieldsNullValue() {
        TextField tf = new TextField();
        EmailField ef = new EmailField();
        PasswordField pf = new PasswordField();
        BigDecimalField bdf = new BigDecimalField();

        getCurrentView().getElement().appendChild(tf.getElement(),
                ef.getElement(), pf.getElement(), bdf.getElement());

        TextFieldWrap<TextField, String> tf_ = wrap(tf);
        TextFieldWrap<EmailField, String> ef_ = wrap(ef);
        TextFieldWrap<PasswordField, String> pf_ = wrap(pf);
        TextFieldWrap<BigDecimalField, BigDecimal> bdf_ = wrap(bdf);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tf_.setValue(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ef_.setValue(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> pf_.setValue(null));
        bdf_.setValue(null);
    }

    @Test
    void textFieldWithClearButton_clear_valueIsCleared() {
        TextField tf = new TextField();
        tf.setClearButtonVisible(true);
        tf.setValue("Some value");
        getCurrentView().getElement().appendChild(tf.getElement());

        TextFieldWrap<TextField, String> tf_ = wrap(tf);
        tf_.clear();

        Assertions.assertTrue(tf.isEmpty(), "Value should have cleared");
    }

    @Test
    void textFieldWithCustomEmptyValue_clear_valueIsCleared() {
        TextField tf = new TextField() {
            @Override
            public String getEmptyValue() {
                return "EMPTY";
            }
        };
        tf.setValue("Some value");
        tf.setClearButtonVisible(true);
        getCurrentView().getElement().appendChild(tf.getElement());

        TextFieldWrap<TextField, String> tf_ = wrap(tf);
        tf_.clear();

        Assertions.assertTrue(tf.isEmpty(), "Value should have cleared");
        Assertions.assertEquals("EMPTY", tf.getValue(),
                "Value should have cleared");
    }

    @Test
    void textFieldWithoutClearButton_clear_throws() {
        TextField tf = new TextField();
        tf.setClearButtonVisible(false);
        getCurrentView().getElement().appendChild(tf.getElement());

        TextFieldWrap<TextField, String> tf_ = wrap(tf);

        Assertions.assertThrows(IllegalStateException.class, tf_::clear,
                "Clear should not be usable when clear button is not visible");
    }

    @Test
    void notUsableTextField_clear_throws() {
        TextField tf = new TextField();
        tf.setClearButtonVisible(true);
        tf.setEnabled(false);
        getCurrentView().getElement().appendChild(tf.getElement());

        TextFieldWrap<TextField, String> tf_ = wrap(tf);

        Assertions.assertThrows(IllegalStateException.class, tf_::clear,
                "Clear should not be usable when text field is not usable");
    }

}
