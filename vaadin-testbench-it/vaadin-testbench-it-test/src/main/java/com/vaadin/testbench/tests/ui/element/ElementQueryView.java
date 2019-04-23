package com.vaadin.testbench.tests.ui.element;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.Route;

@Route(ElementQueryView.ROUTE)
public class ElementQueryView extends Div {

    public static final String ROUTE = "ElementQueryView";

    public ElementQueryView() {
        getElement().getStyle().set("line-height", "12px");

        for (int i = 0; i < 10; i++) {
            NativeButton button = new NativeButton("Button " + i);
            button.getElement().getStyle().set("margin", "0");

            if (i == 2) {
                button.getElement().setAttribute("disabled", true);
            }

            button.getElement().addEventListener("custom123", e -> {
                Div div = new Div();
                div.setId("msg");
                div.setText("Event on " + button.getText());
                add(div);
            });
            add(new Div(button));

            if (i == 5) {
                button.getElement().setAttribute("boolean", true);
                button.getElement().setAttribute("string", "value");
            }
        }
    }
}
