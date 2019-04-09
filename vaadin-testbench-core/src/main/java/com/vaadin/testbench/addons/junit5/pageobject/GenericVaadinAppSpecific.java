package com.vaadin.testbench.addons.junit5.pageobject;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

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
