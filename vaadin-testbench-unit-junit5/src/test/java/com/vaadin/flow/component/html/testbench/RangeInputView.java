/*
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.RangeInput;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "range-input", registerAtStartup = false)
public class RangeInputView extends Component implements HasComponents {

    RangeInput rangeInput = new RangeInput();

    public RangeInputView() {
        rangeInput.setMin(-20);
        rangeInput.setMax(20);
        add(rangeInput);
    }

}
