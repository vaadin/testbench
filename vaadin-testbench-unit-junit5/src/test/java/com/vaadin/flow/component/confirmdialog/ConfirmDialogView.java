/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.confirmdialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "confirm-dialog", registerAtStartup = false)
public class ConfirmDialogView extends Component implements HasComponents {

    ConfirmDialog dialog;

    public ConfirmDialogView() {
        dialog = new ConfirmDialog();
    }
}
