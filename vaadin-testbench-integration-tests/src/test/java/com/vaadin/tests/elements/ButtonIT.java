/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import org.junit.Test;

import com.vaadin.flow.component.html.testbench.NativeButtonElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.tests.AbstractTB6Test;

public class ButtonIT extends AbstractTB6Test  {

    @Override
    protected Class<? extends Component> getTestView() {
        return ButtonView.class;
    }

    @Before
    public void openAndFindElement() {
        openTestURL();
    }

    @Test
    public void buttonClick_singleClick() {
        $(NativeButtonElement.class).id("test-button").click();
        assertEquals("Button single clicked: 1",
                $(SpanElement.class).id("single-click").getText());
        assertEquals("Button focused",
                $(SpanElement.class).id("focus-event").getText());
    }

    @Test
    public void buttonClick_doubleClick() {
        $(NativeButtonElement.class).id("test-button").doubleClick();
        assertEquals("Button double clicked: 2",
                $(SpanElement.class).id("double-click").getText());
        assertEquals("Button focused",
                $(SpanElement.class).id("focus-event").getText());
    }

}
