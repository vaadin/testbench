/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.H5;
import com.vaadin.testbench.unit.Tests;

@Tests(H5.class)
public class H5Tester extends HtmlClickContainer<H5> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public H5Tester(H5 component) {
        super(component);
    }
}
