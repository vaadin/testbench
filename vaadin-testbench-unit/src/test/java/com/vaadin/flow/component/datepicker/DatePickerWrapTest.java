/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.datepicker;

import java.time.LocalDate;
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
class DatePickerWrapTest extends UIUnitTest {

    DatePickerView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(DatePickerView.class);
        view = navigate(DatePickerView.class);
    }

    @Test
    void invalidValue_overMaxDate_throwsIllegalArgument() {
        view.picker.setMax(LocalDate.of(1995, 1, 1));

        assertThrows(IllegalArgumentException.class,
                () -> test(view.picker).setValue(LocalDate.of(1995, 1, 5)));
    }

    @Test
    void invalidValue_underMinDate_throwsIllegalArgument() {
        view.picker.setMin(LocalDate.of(1995, 1, 5));

        assertThrows(IllegalArgumentException.class,
                () -> test(view.picker).setValue(LocalDate.of(1995, 1, 1)));
    }

    @Test
    void setValue_eventIsFired_valueIsSet() {

        AtomicReference<LocalDate> value = new AtomicReference<>(null);

        view.picker.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>>) event -> {
                    value.compareAndSet(null, event.getValue());
                });

        final LocalDate newValue = LocalDate.of(1995, 1, 5);
        test(view.picker).setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

}
