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

import static org.junit.jupiter.api.Assertions.assertThrows;

@ViewPackages
class NumberFieldTesterTest extends UIUnitTest {

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
                    value.compareAndSet(null, event.getValue());
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
