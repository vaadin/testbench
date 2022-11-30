/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.datepicker;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "date", registerAtStartup = false)
public class DatePickerView extends Component implements HasComponents {
    DatePicker picker;

    public DatePickerView() {
        picker = new DatePicker();
        add(picker);
    }
}
