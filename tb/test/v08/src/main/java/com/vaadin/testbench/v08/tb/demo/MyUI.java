package com.vaadin.testbench.v08.tb.demo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class MyUI extends UI {
 @Override
  protected void init(VaadinRequest request) {

    //set the main Component
    setContent(new BasicTestUI());
  }

}
