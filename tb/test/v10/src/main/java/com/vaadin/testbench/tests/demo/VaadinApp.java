package com.vaadin.testbench.tests.demo;


import static java.lang.String.valueOf;

import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.testbench.addons.framework.ComponentIDGenerator;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class VaadinApp extends Composite<Div> implements HasLogger {

  public static final String BTN_CLICK_ME   = ComponentIDGenerator.buttonID().apply(VaadinApp.class, "btn-click-me");
  public static final String LB_CLICK_COUNT = ComponentIDGenerator.spanID().apply(VaadinApp.class, "lb-click-count");

  private final Button         btnClickMe   = new Button("click me");
  private final Span           lbClickCount = new Span("0");
  private final VerticalLayout layout       = new VerticalLayout(btnClickMe, lbClickCount);

  private int clickcount = 0;

  public VaadinApp() {
    btnClickMe.setId(BTN_CLICK_ME);
    btnClickMe.addClickListener(event -> lbClickCount.setText(valueOf(++clickcount)));

    lbClickCount.setId(LB_CLICK_COUNT);

    //set the main Component
    logger().info("setting now the main ui content..");
    getContent().add(layout);

  }
}
