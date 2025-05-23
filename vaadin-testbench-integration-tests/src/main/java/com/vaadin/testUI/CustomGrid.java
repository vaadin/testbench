package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;

public class CustomGrid extends Div {
    public CustomGrid() {
        getElement().setAttribute("hierarchical", false);
        setText(getClass().getSimpleName());
    }
}
