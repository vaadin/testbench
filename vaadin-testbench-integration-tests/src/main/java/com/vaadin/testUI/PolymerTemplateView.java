package com.vaadin.testUI;

import com.vaadin.flow.model.TemplateModel;
import com.vaadin.router.Route;
import com.vaadin.ui.Tag;
import com.vaadin.ui.common.HtmlImport;
import com.vaadin.ui.html.Div;
import com.vaadin.ui.html.NativeButton;
import com.vaadin.ui.polymertemplate.PolymerTemplate;

@Route("PolymerTemplateView")
@Tag("polymer-template-view")
@HtmlImport("polymer-template-view.html")
public class PolymerTemplateView extends PolymerTemplate<TemplateModel> {

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
