package com.vaadin.flow.component.orderedlayout.testbench.test;

import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(HorizontalLayoutView.NAV)
@Theme(Lumo.class)
public class HorizontalLayoutView extends AbstractView {

  public static final String DEFAULT = "default";
  public static final String NAV = "HorizontalLayout";

  public HorizontalLayoutView() {
    add(new TextField("First name"));
    HorizontalLayout layout = new HorizontalLayout();
    layout.setId(DEFAULT);
    layout.add(new TextField("First name"));
    layout.add(new TextField("Last name"));

    add(layout);
  }

}
