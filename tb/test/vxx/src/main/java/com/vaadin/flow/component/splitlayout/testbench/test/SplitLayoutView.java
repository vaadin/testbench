package com.vaadin.flow.component.splitlayout.testbench.test;

import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(SplitLayoutView.NAV)
@Theme(Lumo.class)
public class SplitLayoutView extends AbstractView {

  public static final String DEFAULT = "default";
  public static final String NAV = "SplitLayout";

  public SplitLayoutView() {
    add(new TextField("First name"));
    SplitLayout layout = new SplitLayout();
    layout.setId(DEFAULT);
    layout.addToPrimary(new TextField("First name"));
    layout.addToSecondary(new TextField("Last name"));

    add(layout);
  }

}
