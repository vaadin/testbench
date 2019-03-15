package com.vaadin.testbench.addons.webdriver;


/**
 * Enum out of the interface BrowserTyp from Selenium.
 *
 */
public enum BrowserTypes {

  FIREFOX("firefox"),
  FIREFOX_PROXY("firefoxproxy"),
  FIREFOX_CHROME("firefoxchrome"),
  GOOGLECHROME("googlechrome"),
  SAFARI("safari"),
  /**
   * @deprecated Use OPERA_BLINK
   */
  @Deprecated
  OPERA("opera"),
  OPERA_BLINK("operablink"),
  EDGE("MicrosoftEdge"),
  IEXPLORE("iexplore"),
  IEXPLORE_PROXY("iexploreproxy"),
  SAFARI_PROXY("safariproxy"),
  CHROME("chrome"),
  KONQUEROR("konqueror"),
  MOCK("mock"),
  IE_HTA("iehta"),

  ANDROID("android"),
  HTMLUNIT("htmlunit"),
  IE("internet explorer"),
  IPHONE("iPhone"),
  IPAD("iPad"),
  PHANTOMJS("phantomjs");


  private String browserName;

  BrowserTypes(String browserName) {
    this.browserName = browserName;
  }

  String browserName() { return browserName; }
}
