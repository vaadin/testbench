package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;

public class CustomGrid extends Div {
    public CustomGrid() {
        getElement().setAttribute("hierarchical", false);
        getElement().setAttribute("grid-element", true);
        setText(getClass().getSimpleName());
    }
}
