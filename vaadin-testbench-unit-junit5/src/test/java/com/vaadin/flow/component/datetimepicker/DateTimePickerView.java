/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.datetimepicker;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "date-time", registerAtStartup = false)
public class DateTimePickerView extends Component implements HasComponents {
    DateTimePicker picker;

    public DateTimePickerView() {
        picker = new DateTimePicker();
        add(picker);
    }
}
