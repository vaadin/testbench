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
package com.vaadin.flow.component.datetimepicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
class DateTimePickerTesterTest extends BrowserlessTest {

    DateTimePickerView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(DateTimePickerView.class);
        view = navigate(DateTimePickerView.class);
    }

    @Test
    void invalidValue_overMaxDate_throwsIllegalArgument() {
        view.picker.setMax(
                LocalDateTime.of(LocalDate.of(1995, 1, 1), LocalTime.NOON));

        assertThrows(IllegalArgumentException.class,
                () -> test(view.picker).setValue(LocalDateTime
                        .of(LocalDate.of(1995, 1, 5), LocalTime.MIDNIGHT)));
    }

    @Test
    void invalidValue_underMinDate_throwsIllegalArgument() {
        view.picker.setMin(
                LocalDateTime.of(LocalDate.of(1995, 1, 5), LocalTime.NOON));

        assertThrows(IllegalArgumentException.class,
                () -> test(view.picker).setValue(LocalDateTime
                        .of(LocalDate.of(1995, 1, 1), LocalTime.MIDNIGHT)));
    }

    @Test
    void setValue_eventIsFired_valueIsSet() {

        AtomicReference<LocalDateTime> value = new AtomicReference<>(null);

        view.picker.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<DateTimePicker, LocalDateTime>>) event -> {
                    if (event.isFromClient()) {
                        value.compareAndSet(null, event.getValue());
                    }
                });

        final LocalDateTime newValue = LocalDateTime
                .of(LocalDate.of(1995, 1, 5), LocalTime.NOON);
        test(view.picker).setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }
}
