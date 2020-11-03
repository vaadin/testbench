/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
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