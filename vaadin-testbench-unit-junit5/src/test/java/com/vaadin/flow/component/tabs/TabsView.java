/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.tabs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "tabs", registerAtStartup = false)
public class TabsView extends Component implements HasComponents {

    Tabs tabs;
    Tab details;
    Tab payment;
    Tab shipping;

    public TabsView() {
        details = new Tab("Details");
        payment = new Tab("Payment");
        shipping = new Tab("Shipping");

        tabs = new Tabs(details, payment, shipping);
        add(tabs);
    }
}
