package com.vaadin.testUI;

import com.vaadin.router.Route;
import com.vaadin.ui.html.Div;
import com.vaadin.ui.html.NativeButton;

@Route("ElementQueryView")
public class ElementQueryView extends Div {

    public ElementQueryView() {
        for (int i = 0; i < 10; i++) {
            add(new Div(new NativeButton("Button " + i)));
        }
    }

}
