/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
