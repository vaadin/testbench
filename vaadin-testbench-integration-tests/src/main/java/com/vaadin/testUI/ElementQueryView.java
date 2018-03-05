package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.Route;

@Route("ElementQueryView")
public class ElementQueryView extends Div {

    public ElementQueryView() {
        for (int i = 0; i < 10; i++) {
            NativeButton button = new NativeButton("Button " + i);
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
