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

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;

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
        TextField tf = view.textField;
        tf.setReadOnly(true);

        final TextFieldWrap tf_ = wrap(TextFieldWrap.class, tf);

        Assertions.assertFalse(tf_.isUsable(),
                "Read only TextField shouldn't be usable");
    }

    @Test
    public void readOnlyTextField_automaticWrapper_readOnlyIsCheckedInUsable() {
        TextField tf = view.textField;
        tf.setReadOnly(true);

        Assertions.assertFalse(wrap(tf).isUsable(),
                "Read only TextField shouldn't be usable");
    }

    @Test
    public void setTextFieldValue_eventIsFired_valueIsSet() {
        TextField tf = view.textField;

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
        TextField tf = view.textField;

        tf.getElement().setEnabled(false);
        final TextFieldWrap tf_ = wrap(TextFieldWrap.class, tf);

        Assertions.assertThrows(IllegalStateException.class,
                () -> tf_.setValue("fail"),
                "Setting value to a non interactable field should fail");
    }
}
