/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.testbench.unit.ComponentTester;

public class HtmlComponentTester<T extends HtmlComponent>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public HtmlComponentTester(T component) {
        super(component);
    }

    /**
     * Get the title string set for the html component if available.
     *
     * @return title string
     * @throws IllegalStateException
     *             if not title has been set
     */
    public String getTitle() {
        ensureVisible();
        return getComponent().getTitle()
                .orElseThrow(() -> new IllegalStateException(
                        "No title set for " + getComponent().getClassName()));
    }

    /**
     * Get the recursive text for target element.
     *
     * @return recursive text of component
     * @throws IllegalStateException
     *             if component not visible
     */
    public String getText() {
        ensureVisible();
        return getComponent().getElement().getTextRecursively();
    }

}
