/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2024 Vaadin Ltd
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
    FIREFOX, CHROME, SAFARI, IE8, IE9, IE10, IE11, EDGE, PHANTOMJS;

    private Browser() {
    }

    public DesiredCapabilities getDesiredCapabilities() {
        switch (this) {
        case CHROME:
            return BrowserUtil.chrome();
        case IE10:
            return BrowserUtil.ie10();
        case IE11:
            return BrowserUtil.ie11();
        case IE8:
            return BrowserUtil.ie8();
        case IE9:
            return BrowserUtil.ie9();
        case EDGE:
            return BrowserUtil.edge();
        case PHANTOMJS:
            return BrowserUtil.phantomJS();
        case SAFARI:
            return BrowserUtil.safari();
        case FIREFOX:
        default:
            return BrowserUtil.firefox();
        }
    }
}