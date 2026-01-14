/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
