/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.timepicker;

import java.time.LocalTime;
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
class TimePickerWrapTest extends UIUnitTest {

    TimePickerView view;
    TimePickerTester<TimePicker> pick_;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(TimePickerView.class);
        view = navigate(TimePickerView.class);
        pick_ = test(view.picker);
    }

    @Test
    void invalidValue_overMaxDate_throwsIllegalArgument() {
        view.picker.setMax(LocalTime.NOON);

        assertThrows(IllegalArgumentException.class,
                () -> pick_.setValue(LocalTime.of(13, 30)));
    }

    @Test
    void invalidValue_underMinDate_throwsIllegalArgument() {
        view.picker.setMin(LocalTime.NOON);

        assertThrows(IllegalArgumentException.class,
                () -> pick_.setValue(LocalTime.of(10, 0)));
    }

    @Test
    void setValue_eventIsFired_valueIsSet() {

        AtomicReference<LocalTime> value = new AtomicReference<>(null);

        view.picker.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TimePicker, LocalTime>>) event -> {
                    value.compareAndSet(null, event.getValue());
                });

        final LocalTime newValue = LocalTime.NOON;
        pick_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

}
