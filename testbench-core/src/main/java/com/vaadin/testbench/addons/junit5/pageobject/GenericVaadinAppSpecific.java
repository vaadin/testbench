package com.vaadin.testbench.addons.junit5.pageobject;

import java.util.function.Supplier;

public interface GenericVaadinAppSpecific extends PageObject {

  default Supplier<String> urlRestartApp() {
    return () -> url().get() + "?restartApplication";
  }

  default Supplier<String> urlDebugApp() {
    return () -> url().get() + "?debug";
  }

  default Supplier<String> urlSwitchToDebugApp() {
    return () -> url().get() + "?debug&restartApplication";
  }

  default void switchToDebugMode() {
    getDriver().get(urlSwitchToDebugApp().get());
  }

  default void restartApplication() {
    getDriver().get(urlRestartApp().get());
  }

}
