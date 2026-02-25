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
