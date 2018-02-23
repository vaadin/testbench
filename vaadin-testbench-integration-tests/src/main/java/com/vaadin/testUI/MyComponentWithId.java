package com.vaadin.testUI;

import com.vaadin.ui.html.Div;

public class MyComponentWithId extends Div {

    public MyComponentWithId() {
        setId("my-component-with-id");
        setText(getClass().getSimpleName());
    }

}
