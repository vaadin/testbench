package com.vaadin.testUI;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route("SVGView")
public class SVGView extends Div {

    private Div placeholder;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        add(placeholder = new Div());

        placeholder.getElement().setProperty("innerHTML",
                "<svg height=\"100\" width=\"100\">\n"
                        + "  <circle id='ball' cx=\"50\" cy=\"50\" r=\"40\" stroke=\"black\" stroke-width=\"3\" fill=\"red\" />\n"
                        + "  Sorry, your browser does not support inline SVG."
                        + "</svg>");
        attachEvent.getUI().getPage().executeJavaScript(
                "document.getElementById('ball').addEventListener('click', function() {document.body.innerText='clicked';})");
    }
}
