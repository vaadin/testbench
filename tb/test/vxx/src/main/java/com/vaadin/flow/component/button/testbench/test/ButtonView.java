package com.vaadin.flow.component.button.testbench.test;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(ButtonView.NAV)
@Theme(Lumo.class)
public class ButtonView extends AbstractView {

  public static final String TEXT = "text";
  public static final String NOTEXT = "notext";
  public static final String NAV = "Button";

  public ButtonView() {

    Button buttonWithoutText = new Button();
    buttonWithoutText.setId(NOTEXT);
    buttonWithoutText.addClickListener(e -> {
      log("Button without text clicked");
    });
    add(buttonWithoutText);

    Button buttonWithText = new Button("Text");
    buttonWithText.setId(TEXT);
    buttonWithText.addClickListener(e -> {
      log("Button with text clicked");
    });
    add(buttonWithText);
  }

}
