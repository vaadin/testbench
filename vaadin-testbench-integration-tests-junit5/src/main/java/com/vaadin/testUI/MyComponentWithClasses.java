/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;

public class MyComponentWithClasses extends Div {

    public MyComponentWithClasses() {
        addClassName("my-component-first");
        addClassName("my-component-with-classes");
        addClassName("my-component-last");
        setText(getClass().getSimpleName());
    }

}
