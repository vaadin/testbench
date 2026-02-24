/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class TextFieldTesterTest extends UIUnitTest {

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

        final TextFieldTester<TextField, String> tf_ = test(view.textField);

        Assertions.assertFalse(tf_.isUsable(),
                "Read only TextField shouldn't be usable");
    }

    @Test
    public void readOnlyTextField_automaticWrapper_readOnlyIsCheckedInUsable() {
        view.textField.setReadOnly(true);

        Assertions.assertFalse(test(view.textField).isUsable(),
                "Read only TextField shouldn't be usable");
    }

    @Test
    public void setTextFieldValue_eventIsFired_valueIsSet() {

        AtomicReference<String> value = new AtomicReference<>(null);

        view.textField.addValueChangeListener(
                (ValueChangeListener<ComponentValueChangeEvent<TextField, String>>) event -> {
                    if (event.isFromClient()) {
                        value.compareAndSet(null, event.getValue());
                    }
                });

        final TextFieldTester<TextField, String> tf_ = test(view.textField);
        final String newValue = "Test";
        tf_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

    @Test
    public void nonInteractableField_throwsOnSetValue() {

        view.textField.getElement().setEnabled(false);
        final TextFieldTester<TextField, String> tf_ = test(view.textField);

        Assertions.assertThrows(IllegalStateException.class,
                () -> tf_.setValue("fail"),
                "Setting value to a non interactable field should fail");
    }

    @Test
    void textFieldWithValidation_doNotPreventInvalid_doNotThrow() {
        // Only accept numbers
        view.textField.setPattern("\\d*");

        final TextFieldTester<TextField, String> tf_ = test(view.textField);
        final String faultyValue = "Invalid value, but doesn't throw";
        tf_.setValue(faultyValue);
        Assertions.assertEquals(faultyValue, view.textField.getValue(),
                "Value should have been set.");
    }

    @Test
    public void textFieldWithPattern_patternIsValidated() {
        TextField tf = view.textField;
        // Only accept numbers
        tf.setAllowedCharPattern("\\d*");

        final TextFieldTester<TextField, String> tf_ = test(tf);
        tf_.setValue("1234");

        Assertions.assertEquals("1234", tf.getValue());
        tf_.setValue("hello");
        Assertions.assertFalse(tf_.getComponent().isInvalid());
    }

    @Test
    public void textFieldWithMinLength_lengthIsChecked() {
        TextField tf = view.textField;
        tf.setMinLength(5);

        final TextFieldTester<TextField, String> tf_ = test(tf);
        tf_.setValue("1234");
        Assertions.assertTrue(tf_.getComponent().isInvalid());
    }

    @Test
    public void textFieldWithMaxLength_lengthIsChecked() {
        TextField tf = view.textField;
        tf.setMaxLength(3);

        final TextFieldTester<TextField, String> tf_ = test(tf);
        tf_.setValue("1234");
        Assertions.assertTrue(tf_.getComponent().isInvalid());
    }

    @Test
    public void textFieldWithRequired_valueIsChecked() {
        TextField tf = view.textField;
        tf.setRequired(true);

        final TextFieldTester<TextField, String> tf_ = test(tf);
        tf_.setValue("value1"); // must be value changed to trigger required
                                // validation
        tf_.setValue("");
        Assertions.assertTrue(tf_.getComponent().isInvalid());
    }

    @Test
    void testFieldsNullValue() {
        TextField tf = new TextField();
        EmailField ef = new EmailField();
        PasswordField pf = new PasswordField();
        BigDecimalField bdf = new BigDecimalField();

        getCurrentView().getElement().appendChild(tf.getElement(),
                ef.getElement(), pf.getElement(), bdf.getElement());

        TextFieldTester<TextField, String> tf_ = test(tf);
        TextFieldTester<EmailField, String> ef_ = test(ef);
        TextFieldTester<PasswordField, String> pf_ = test(pf);
        TextFieldTester<BigDecimalField, BigDecimal> bdf_ = test(bdf);

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

        TextFieldTester<TextField, String> tf_ = test(tf);
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

        TextFieldTester<TextField, String> tf_ = test(tf);
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

        TextFieldTester<TextField, String> tf_ = test(tf);

        Assertions.assertThrows(IllegalStateException.class, tf_::clear,
                "Clear should not be usable when clear button is not visible");
    }

    @Test
    void notUsableTextField_clear_throws() {
        TextField tf = new TextField();
        tf.setClearButtonVisible(true);
        tf.setEnabled(false);
        getCurrentView().getElement().appendChild(tf.getElement());

        TextFieldTester<TextField, String> tf_ = test(tf);

        Assertions.assertThrows(IllegalStateException.class, tf_::clear,
                "Clear should not be usable when text field is not usable");
    }

}
