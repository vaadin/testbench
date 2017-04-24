package com.vaadin.testUI;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class TestbenchElementTooltipUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        this.getTooltipConfiguration().setOpenDelay(2000);
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(false);
        vl.setMargin(false);
        setContent(vl);
        Button btn = new Button("Button");
        Button btn2 = new Button("Button2");
        btn.setDescription("Tooltip");
        btn2.setDescription("Tooltip2");
        vl.addComponents(btn, btn2);
    }

}
