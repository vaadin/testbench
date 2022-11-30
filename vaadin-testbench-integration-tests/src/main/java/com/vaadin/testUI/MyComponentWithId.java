/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;

public class MyComponentWithId extends Div {

    public MyComponentWithId() {
        setId("my-component-with-id");
        setText(getClass().getSimpleName());
    }

}
