/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.dialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "dialog", registerAtStartup = false)
public class DialogView extends Component implements HasComponents {

    Dialog dialog;
    Button button;

    public DialogView() {
        dialog = new Dialog();
        button = new Button();
        add(button);
    }
}
