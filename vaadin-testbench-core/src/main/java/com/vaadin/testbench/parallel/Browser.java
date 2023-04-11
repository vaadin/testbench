/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.RunLocally;

/**
 * Enumerates the most commonly used browsers. This enumeration can be used in
 * the {@link RunLocally} annotation
 */
public enum Browser {
    FIREFOX, CHROME, SAFARI, IE11, EDGE;

    private Browser() {
    }

    public DesiredCapabilities getDesiredCapabilities() {
        switch (this) {
        case CHROME:
            return BrowserUtil.chrome();
        case IE11:
            return BrowserUtil.ie11();
        case EDGE:
            return BrowserUtil.edge();
        case SAFARI:
            return BrowserUtil.safari();
        case FIREFOX:
        default:
            return BrowserUtil.firefox();
        }
    }
}