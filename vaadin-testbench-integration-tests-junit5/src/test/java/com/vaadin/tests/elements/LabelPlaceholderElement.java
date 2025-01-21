/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import com.vaadin.testUI.LabelPlaceholder;
import com.vaadin.testbench.HasLabel;
import com.vaadin.testbench.HasPlaceholder;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

@Element(LabelPlaceholder.TAG)
public class LabelPlaceholderElement extends TestBenchElement
        implements HasLabel, HasPlaceholder {

    @Override
    public String getText() {
        return getPropertyString("textContent");
    }
}
