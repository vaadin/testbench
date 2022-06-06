/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.dialog;

import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

@Wraps(Dialog.class)
public class DialogWrap extends ComponentWrap<Dialog> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public DialogWrap(Dialog component) {
        super(component);
    }

    /**
     * Open the dialog.
     */
    public void open() {
        getComponent().open();
        roundTrip();
    }

    /**
     * Close the dialog.
     */
    public void close() {
        getComponent().close();
        roundTrip();
    }
}
