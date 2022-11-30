/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.HtmlContainer;

public class HtmlContainerTester<T extends HtmlContainer>
        extends HtmlComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public HtmlContainerTester(T component) {
        super(component);
    }

    /**
     * Get the text for target html component.
     *
     * @return text of component
     * @throws IllegalStateException
     *             if component not visible
     */
    @Override
    public String getText() {
        ensureVisible();
        return getComponent().getText();
    }
}
