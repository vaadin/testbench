package com.vaadin.flow.component.common.testbench.test;

import com.vaadin.flow.component.html.Div;

public class AbstractView extends Div {
  private Log log;

  protected AbstractView() {
    log = new Log();
    add(log);
  }

  protected void log(String message) {
    log.log(message);
  }

  public static class Log extends Div {

    private int nr = 1;

    Log() {
      setWidth("100%");
      setHeight("5em");
      getElement().getStyle().set("overflow" , "auto");
      setId("log");
    }

    public void log(String message) {
      Div row = new Div();
      row.setText(nr++ + ". " + message);
      getElement().insertChild(0 , row.getElement());
    }

  }

}
