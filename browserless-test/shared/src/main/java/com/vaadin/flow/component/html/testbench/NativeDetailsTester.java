/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.html.NativeDetails;
import com.vaadin.testbench.unit.Tests;

@Tests(NativeDetails.class)
public class NativeDetailsTester extends HtmlComponentTester<NativeDetails> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public NativeDetailsTester(NativeDetails component) {
        super(component);
    }

    /**
     * Get the summary component of this Details element.
     *
     * @return summary component
     */
    public NativeDetails.Summary getSummary() {
        ensureVisible();
        return getComponent().getSummary();
    }

    /**
     * Get the summary text.
     *
     * @return text in the summary element
     */
    public String getSummaryText() {
        ensureVisible();
        return getComponent().getSummaryText();
    }

    /**
     * Get details content if the details is opened.
     *
     * @return details content
     * @throws IllegalStateException
     *             if content is not displayed
     */
    public Component getContent() {
        ensureVisible();
        if (!getComponent().isOpen()) {
            throw new IllegalStateException("Details are not displayed.");
        }
        return getComponent().getContent();
    }

    /**
     * Toggle the open state of the component.
     */
    public void toggleContent() {
        ensureComponentIsUsable();
        getComponent().setOpen(!getComponent().isOpen());
        ComponentUtil.fireEvent(getComponent(),
                new NativeDetails.ToggleEvent(getComponent(), true));
    }
}
