package com.vaadin.testbench.tests.testUI;

import com.vaadin.flow.component.html.Div;

public class MyComponentWithId extends Div {

    public MyComponentWithId() {
        setId("my-component-with-id");
        setText(getClass().getSimpleName());
    }
}
