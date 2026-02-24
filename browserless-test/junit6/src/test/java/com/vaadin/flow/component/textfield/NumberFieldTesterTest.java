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

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.router.RouteConfiguration;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ViewPackages
class NumberFieldTesterTest extends BrowserlessTest {

    private NumberFieldView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(NumberFieldView.class);
        view = navigate(NumberFieldView.class);
    }

    @Test
    public void readOnlyNumberField_isNotUsable() {
        view.numberField.setReadOnly(true);

        final NumberFieldTester<NumberField, Double> nf_ = test(
                view.numberField);

        Assertions.assertFalse(nf_.isUsable(),
                "Read only NumberField shouldn't be usable");
    }

    @Test
    public void readOnlyNumberField_automatictester_readOnlyIsCheckedInUsable() {
        view.numberField.setReadOnly(true);

        Assertions.assertFalse(test(view.numberField).isUsable(),
                "Read only NumberField shouldn't be usable");
    }

    @Test
    public void setNumberFieldValue_eventIsFired_valueIsSet() {

        AtomicReference<Double> value = new AtomicReference<>(null);

        view.numberField.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<NumberField, Double>>) event -> {
                    if (event.isFromClient()) {
                        value.compareAndSet(null, event.getValue());
                    }
                });

        final NumberFieldTester<NumberField, Double> nf_ = test(
                view.numberField);
        final Double newValue = 15d;
        nf_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

    @Test
    public void setIntegerFieldValue_eventIsFired_valueIsSet() {

        AtomicReference<Integer> value = new AtomicReference<>(null);

        view.integerField.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<IntegerField, Integer>>) event -> {
                    value.compareAndSet(null, event.getValue());
                });

        final NumberFieldTester<IntegerField, Integer> inf_ = test(
                view.integerField);
        final Integer newValue = 15;
        inf_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

    @Test
    public void nonInteractableField_throwsOnSetValue() {

        view.numberField.getElement().setEnabled(false);
        view.integerField.getElement().setEnabled(false);
        final NumberFieldTester<NumberField, Double> nf_ = test(
                view.numberField);
        final NumberFieldTester<IntegerField, Integer> inf_ = test(
                view.integerField);

        Assertions.assertThrows(IllegalStateException.class,
                () -> nf_.setValue(12d),
                "Setting value to a non interactable number field should fail");
        Assertions.assertThrows(IllegalStateException.class,
                () -> inf_.setValue(12),
                "Setting value to a non interactable integer field should fail");
    }

    @Test
    public void maxValue_throwsExceptionForTooSmallValue() {
        view.numberField.setMax(10.0);

        final NumberFieldTester<NumberField, Double> nf_ = test(
                view.numberField);
        final Double newValue = 15d;

        assertThrows(IllegalArgumentException.class,
                () -> nf_.setValue(newValue));
    }

    @Test
    public void minValue_throwsExceptionForTooSmallValue() {
        view.numberField.setMin(20.0);

        final NumberFieldTester<NumberField, Double> nf_ = test(
                view.numberField);
        final Double newValue = 15d;

        assertThrows(IllegalArgumentException.class,
                () -> nf_.setValue(newValue));
    }

}
