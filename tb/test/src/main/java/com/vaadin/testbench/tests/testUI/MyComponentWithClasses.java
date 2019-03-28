package com.vaadin.testbench.tests.testUI;

import com.vaadin.flow.component.html.Div;

public class MyComponentWithClasses extends Div {

    public MyComponentWithClasses() {
        addClassName("my-component-first");
        addClassName("my-component-with-classes");
        addClassName("my-component-last");
        setText(getClass().getSimpleName());
    }

}
