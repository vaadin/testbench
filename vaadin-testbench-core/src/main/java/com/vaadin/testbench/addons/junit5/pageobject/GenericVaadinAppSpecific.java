package com.vaadin.testbench.addons.junit5.pageobject;

public interface GenericVaadinAppSpecific extends PageObject {

    default String urlRestartApp() {
        return url() + "?restartApplication";
    }

    default String urlDebugApp() {
        return url() + "?debug";
    }

    default String urlSwitchToDebugApp() {
        return url() + "?debug&restartApplication";
    }

    default void switchToDebugMode() {
        getDriver().get(urlSwitchToDebugApp());
    }

    default void restartApplication() {
        getDriver().get(urlRestartApp());
    }
}
