package com.vaadin.testbench.addons.webdriver;

public enum BrowserTypes {

  CHROME("chrome"),
  FIREFOX("firefox"),
  OPERA("opera"),
  IE("ie"),
  EDGE("edge"),
  SAFARI("safari");

  private String browserName;

  BrowserTypes(String browserName) {
    this.browserName = browserName;
  }

  String browserName() { return browserName; }
}
