/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.datetimepicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
class DateTimePickerWrapTest extends UIUnitTest {

    DateTimePickerView view;
    DateTimePickerWrap<DateTimePicker> pick_;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(DateTimePickerView.class);
        view = navigate(DateTimePickerView.class);
        pick_ = wrap(view.picker);
    }

    @Test
    void invalidValue_overMaxDate_throwsIllegalArgument() {
        view.picker.setMax(
                LocalDateTime.of(LocalDate.of(1995, 1, 1), LocalTime.NOON));

        assertThrows(IllegalArgumentException.class,
                () -> pick_.setValue(LocalDateTime.of(LocalDate.of(1995, 1, 5),
                        LocalTime.MIDNIGHT)));
    }

    @Test
    void invalidValue_underMinDate_throwsIllegalArgument() {
        view.picker.setMin(
                LocalDateTime.of(LocalDate.of(1995, 1, 5), LocalTime.NOON));

        assertThrows(IllegalArgumentException.class,
                () -> pick_.setValue(LocalDateTime.of(LocalDate.of(1995, 1, 1),
                        LocalTime.MIDNIGHT)));
    }

    @Test
    void setValue_eventIsFired_valueIsSet() {

        AtomicReference<LocalDateTime> value = new AtomicReference<>(null);

        view.picker.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<DateTimePicker, LocalDateTime>>) event -> {
                    value.compareAndSet(null, event.getValue());
                });

        final LocalDateTime newValue = LocalDateTime
                .of(LocalDate.of(1995, 1, 5), LocalTime.NOON);
        pick_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }
}
