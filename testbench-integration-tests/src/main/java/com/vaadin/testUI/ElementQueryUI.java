package com.vaadin.testUI;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ElementQueryUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(false);
        vl.setMargin(false);
        setContent(vl);
        for (int i = 0; i < 10; i++) {
            vl.addComponent(new Button("Button " + i));
        }

        vl.setSizeUndefined();
    }

}
