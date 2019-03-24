package com.vaadin.flow.component.orderedlayout.testbench.test;

import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(VerticalLayoutView.NAV)
@Theme(Lumo.class)
public class VerticalLayoutView extends AbstractView {

  public static final String DEFAULT = "default";
  public static final String NAV = "VerticalLayout";

  public VerticalLayoutView() {
    add(new TextField("First name"));
    VerticalLayout layout = new VerticalLayout();
    layout.setId(DEFAULT);
    layout.add(new TextField("First name"));
    layout.add(new TextField("Last name"));

    add(layout);
  }

}
