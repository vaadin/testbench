/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.tabs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "tabsheet", registerAtStartup = false)
public class TabSheetView extends Component implements HasComponents {

    TabSheet tabs;
    Tab details;
    Span detailsContent = new Span("Details contents");
    Tab payment;
    Span paymentContent = new Span("Payment contents");

    Tab shipping;
    Span shippingContent = new Span("Shipping contents");

    public TabSheetView() {
        tabs = new TabSheet();
        details = tabs.add(new Tab("Details"), detailsContent);
        payment = tabs.add("Payment", paymentContent);
        shipping = tabs.add("Shipping", shippingContent);
        add(tabs);
    }
}
