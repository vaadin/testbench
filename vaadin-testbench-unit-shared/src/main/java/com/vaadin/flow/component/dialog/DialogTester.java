/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.dialog;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

@Tests(Dialog.class)
@Deprecated(forRemoval = true, since = "10.1")
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
    }
}
