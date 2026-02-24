/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.confirmdialog;

import java.util.NoSuchElementException;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.dom.Element;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for ConfirmDialog.
 
  * @deprecated Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.
  */
@Tests(ConfirmDialog.class)
@Deprecated(forRemoval = true, since = "10.1")
public class ConfirmDialogTester extends ComponentTester<ConfirmDialog> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public ConfirmDialogTester(ConfirmDialog component) {
        super(component);
    }

    public void open() {
        getComponent().open();
        roundTrip();
    }

    /**
     * Click the confirm button.
     */
    public void confirm() {
        ensureComponentIsUsable();

        ComponentUtil.fireEvent(getComponent(),
                new ConfirmDialog.ConfirmEvent(getComponent(), true));
        getComponent().close();
    }

    /**
     * Click cancel button.
     *
     * @throws IllegalStateException
     *             when cancel button is not enabled
     */
    public void cancel() {
        ensureComponentIsUsable();
        if (!Boolean.parseBoolean(getComponent().getElement()
                .getProperty("cancelButtonVisible"))) {
            throw new IllegalStateException("Cancel button is not available.");
        }
        ComponentUtil.fireEvent(getComponent(),
                new ConfirmDialog.CancelEvent(getComponent(), true));
        getComponent().close();
    }

    /**
     * Click reject button.
     *
     * @throws IllegalStateException
     *             when reject button is not enabled
     */
    public void reject() {
        ensureComponentIsUsable();
        if (!Boolean.parseBoolean(getComponent().getElement()
                .getProperty("rejectButtonVisible"))) {
            throw new IllegalStateException("Reject button is not available.");
        }
        ComponentUtil.fireEvent(getComponent(),
                new ConfirmDialog.RejectEvent(getComponent(), true));
        getComponent().close();
    }

    /**
     * Get the confirmation message text set to the component.
     *
     * @return confirmation message
     */
    public String getText() {
        return getComponent().getElement().getProperty("message");
    }

    /**
     * Get the header of the confirm dialog.
     *
     * @return current header of dialog
     */
    public String getHeader() {
        return getComponent().getElement().getProperty("header");
    }

    /**
     * Get the header element set to the confirm dialog.
     *
     * @return header element
     * @throws NoSuchElementException
     *             if no header element found
     */
    public Element getHeaderElement() {
        return getComponent().getElement().getChildren()
                .filter(elem -> "header".equals(elem.getAttribute("slot")))
                .findFirst().orElseThrow(() -> new IllegalStateException(
                        "No header element set"));
    }
}
