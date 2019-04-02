package com.vaadin.testbench.tests.demo;

import static java.lang.String.valueOf;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends Composite<Div>  {

  public static final String BTN_CLICK_ME   =  "btn-click-me";
  public static final String LB_CLICK_COUNT = "lb-click-count";

  private final Button         btnClickMe   = new Button("click me");
  private final Span           lbClickCount = new Span("0");
  private final VerticalLayout layout       = new VerticalLayout(btnClickMe, lbClickCount);

  private int clickCount = 0;

  public MainView() {
    btnClickMe.setId(BTN_CLICK_ME);
    btnClickMe.addClickListener(event -> lbClickCount.setText(valueOf(++clickCount)));

    lbClickCount.setId(LB_CLICK_COUNT);

    getContent().add(layout);
  }
}
