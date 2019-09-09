package com.vaadin.testbench.tests.ui.element;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;

@Route(PolymerTemplateView.ROUTE)
@Tag("polymer-template-view")
@JsModule("./polymer-template-view.js")
public class PolymerTemplateView extends PolymerTemplate<TemplateModel> {

    public static final String ROUTE = "PolymerTemplateView";

    public PolymerTemplateView() {
        for (int i = 1; i < 6; i++) {
            NativeButton button = new NativeButton("Button " + i);
            button.getElement().setAttribute("id", "light-button-" + i);
            button.addClassName("button");
            button.addClassName("button-" + i);
            getElement().appendChild(new Div(button).getElement());
        }

        NativeButton slottedButton = new NativeButton(
                "Special Button (in Light DOM)");
        slottedButton.getElement().setAttribute("slot", "special-slot");
        slottedButton.getElement().setAttribute("id", "special-button");
        slottedButton.addClassName("button");
        slottedButton.addClassName("button-special-slot");
        getElement().appendChild(slottedButton.getElement());
    }
}
