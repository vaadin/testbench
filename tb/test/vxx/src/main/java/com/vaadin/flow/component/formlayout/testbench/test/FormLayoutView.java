package com.vaadin.flow.component.formlayout.testbench.test;

import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(FormLayoutView.NAV)
@Theme(Lumo.class)
public class FormLayoutView extends AbstractView {

  public static final String DEFAULT = "default";
  public static final String NAV = "FormLayout";

  public FormLayoutView() {
    add(new TextField("First name"));
    FormLayout layout = new FormLayout();
    layout.setId(DEFAULT);
    layout.add(new TextField("First name"));
    layout.add(new TextField("Last name"));

    add(layout);
  }

}
