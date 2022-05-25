/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.accordion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "accordion", registerAtStartup = false)
public class AccordionView extends Component implements HasComponents {

    Accordion accordion;

    // for content testing
    Div redDiv, greenDiv;
    AccordionPanel redPanel, greenPanel, disabledPanel;

    public AccordionView() {

        accordion = new Accordion();

        redDiv = new Div();

        greenDiv = new Div();

        redPanel = accordion.add("Red", redDiv);
        greenPanel = accordion.add("Green", greenDiv);
        disabledPanel = accordion.add("Disabled", new Span("Disabled panel"));
        disabledPanel.setEnabled(false);

        add(accordion);
    }
}
