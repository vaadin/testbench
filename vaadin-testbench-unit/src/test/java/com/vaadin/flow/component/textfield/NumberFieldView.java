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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "number-fields", registerAtStartup = false)
public class NumberFieldView extends Component implements HasComponents {
    NumberField numberField;
    IntegerField integerField;

    public NumberFieldView() {
        numberField = new NumberField();
        integerField = new IntegerField();

        add(numberField, integerField);
    }

}
