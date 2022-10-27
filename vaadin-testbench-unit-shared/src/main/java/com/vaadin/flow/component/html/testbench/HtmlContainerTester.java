/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
