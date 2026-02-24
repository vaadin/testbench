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
package com.vaadin.flow.component.timepicker;

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
class TimePickerTesterTest extends BrowserlessTest {

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
                    if (event.isFromClient()) {
                        value.compareAndSet(null, event.getValue());
                    }
                });

        final LocalTime newValue = LocalTime.NOON;
        pick_.setValue(newValue);

        Assertions.assertEquals(newValue, value.get());
    }

}
