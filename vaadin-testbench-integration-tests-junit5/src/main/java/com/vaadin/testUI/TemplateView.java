/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.Route;

@Route("TemplateView")
@Tag("template-view")
@JsModule("./template-view.ts")
public class TemplateView extends LitTemplate {

    public TemplateView() {
        for (int i = 1; i < 6; i++) {
            NativeButton button = new NativeButton("Button " + i);
            button.getElement().setAttribute("id", "light-button-" + i);
            button.getElement().setAttribute("theme", "light-theme");
            button.addClassName("button");
            button.addClassName("button-" + i);
            getElement().appendChild(new Div(button).getElement());
        }

        NativeButton slottedButton = new NativeButton(
                "Special Button (in Light DOM)");
        slottedButton.getElement().setAttribute("slot", "special-slot");
        slottedButton.getElement().setAttribute("id", "special-button");
        slottedButton.getElement().setAttribute("theme", "light-theme");
        slottedButton.addClassName("button");
        slottedButton.addClassName("button-special-slot");
        getElement().appendChild(slottedButton.getElement());
    }
}
