/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.confirmdialog;

import java.util.NoSuchElementException;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementUtil;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for ConfirmDialog.
 */
@Wraps(ConfirmDialog.class)
public class ConfirmDialogWrap extends ComponentWrap<ConfirmDialog> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public ConfirmDialogWrap(ConfirmDialog component) {
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
        if (!Boolean.parseBoolean(
                getComponent().getElement().getProperty("cancel"))) {
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
        if (!Boolean.parseBoolean(
                getComponent().getElement().getProperty("reject"))) {
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
     * Get the header element se to the confirm dialog.
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
