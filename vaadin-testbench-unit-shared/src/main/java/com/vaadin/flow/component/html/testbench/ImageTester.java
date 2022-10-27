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
