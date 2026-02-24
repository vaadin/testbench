/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.tester;

import com.vaadin.flow.component.HtmlContainer;

public abstract class HtmlClickContainer<T extends HtmlContainer>
        extends HtmlContainerTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public HtmlClickContainer(T component) {
        super(component);
    }
}
