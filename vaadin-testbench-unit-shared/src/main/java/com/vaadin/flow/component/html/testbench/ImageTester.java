/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.Image;
import com.vaadin.testbench.unit.Tests;

@Tests(Image.class)
public class ImageTester extends HtmlClickContainer<Image> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public ImageTester(Image component) {
        super(component);
    }
}
