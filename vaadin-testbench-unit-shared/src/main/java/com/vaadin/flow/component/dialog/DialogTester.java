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

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

@Tests(Dialog.class)
public class DialogTester extends ComponentTester<Dialog> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public DialogTester(Dialog component) {
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
