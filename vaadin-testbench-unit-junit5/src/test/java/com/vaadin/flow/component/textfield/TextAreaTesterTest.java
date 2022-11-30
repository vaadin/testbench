/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class TextAreaWrapTest extends UIUnitTest {

    private TextAreaView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(TextAreaView.class);
        view = navigate(TextAreaView.class);
    }

    @Test
    public void readOnlyTextArea_isNotUsable() {
        view.textArea.setReadOnly(true);

        final TextAreaTester<TextArea> ta_ = test(view.textArea);

        Assertions.assertFalse(ta_.isUsable(),
                "Read only TextArea shouldn't be usable");
    }

    @Test
    public void readOnlyTextArea_automaticWrapper_readOnlyIsCheckedInUsable() {
        view.textArea.setReadOnly(true);

        Assertions.assertFalse(test(view.textArea).isUsable(),
                "Read only TextArea shouldn't be usable");
    }

    @Test
    public void setTextAreaValue_eventIsFired_valueIsSet() {

        AtomicReference<String> value = new AtomicReference<>(null);

        view.textArea.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextArea, String>>) event -> {
                    value.compareAndSet(null, event.getValue());
                });

        final TextAreaTester<TextArea> ta_ = test(view.textArea);
        final String newValue = "Test";
        ta_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

    @Test
    public void nonInteractableField_throwsOnSetValue() {

        view.textArea.getElement().setEnabled(false);
        final TextAreaTester<TextArea> ta_ = test(view.textArea);

        Assertions.assertThrows(IllegalStateException.class,
                () -> ta_.setValue("fail"),
                "Setting value to a non interactable field should fail");
    }

    @Test
    void textAreaWithValidation_doNotPreventInvalid_doNotThrow() {
        // Only accept numbers
        view.textArea.setAllowedCharPattern("\\d*");

        final TextAreaTester<TextArea> ta_ = test(view.textArea);
        final String faultyValue = "Invalid value, but doesn't throw";
        ta_.setValue(faultyValue);
        Assertions.assertEquals(faultyValue, view.textArea.getValue(),
                "Value should have been set.");
    }

    @Test
    public void textAreaWithPattern_patternIsValidated() {
        TextArea tf = view.textArea;
        // Only accept numbers
        tf.setPattern("\\d*");

        final TextAreaTester<TextArea> ta_ = test(tf);
        ta_.setValue("1234");

        Assertions.assertEquals("1234", tf.getValue());
        Assertions.assertFalse(ta_.getComponent().isInvalid());
    }

    @Test
    public void textAreaWithMinLength_lengthIsChecked() {
        TextArea tf = view.textArea;
        tf.setMinLength(5);

        final TextAreaTester<TextArea> ta_ = test(tf);
        ta_.setValue("1234");
        Assertions.assertTrue(ta_.getComponent().isInvalid());
    }

    @Test
    public void textAreaWithMaxLength_lengthIsChecked() {
        TextArea tf = view.textArea;
        tf.setMaxLength(3);

        final TextAreaTester<TextArea> ta_ = test(tf);
        ta_.setValue("1234");
        Assertions.assertTrue(ta_.getComponent().isInvalid());
    }

    @Test
    public void textAreaWithRequired_valueIsChecked() {
        TextArea tf = view.textArea;
        tf.setRequired(true);

        final TextAreaTester<TextArea> ta_ = test(tf);
        ta_.setValue("value1"); // must be value changed to trigger required
                                // validation
        ta_.setValue("");
        Assertions.assertTrue(ta_.getComponent().isInvalid());
    }

}
