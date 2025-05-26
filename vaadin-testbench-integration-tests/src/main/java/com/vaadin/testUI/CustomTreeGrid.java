package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;

public class CustomTreeGrid extends Div {
    public CustomTreeGrid() {
        getElement().setAttribute("hierarchical", true);
        getElement().setAttribute("grid-element", true);
        setText(getClass().getSimpleName());
    }
}
