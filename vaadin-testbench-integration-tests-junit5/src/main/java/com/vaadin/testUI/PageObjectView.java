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
import com.vaadin.flow.router.Route;

@Route("PageObjectView")
public class PageObjectView extends Div {

    public PageObjectView() {
        Div someDiv = new Div();
        someDiv.setText("Some div");

        MyComponentWithId idView = new MyComponentWithId();
        MyComponentWithClasses classesView = new MyComponentWithClasses();

        Div anotherDiv = new Div();
        anotherDiv.setText("Some div");

        add(someDiv, idView, classesView, anotherDiv);
    }
}
