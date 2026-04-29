/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.test.loadtest;

import org.junit.jupiter.api.BeforeEach;

import com.vaadin.testbench.BrowserTestBase;
import com.vaadin.testbench.loadtest.LoadTestItHelper;

/**
 * Base for IT scenarios. When the system property {@code k6.proxy.host} is
 * set (e.g., {@code k6.proxy.host=localhost:6000}), {@link LoadTestItHelper}
 * routes the browser through the recording proxy started by
 * {@code TestbenchRecordMojo}.
 */
public abstract class AbstractIT extends BrowserTestBase {

    @BeforeEach
    public void open() {
        setDriver(LoadTestItHelper.openWithProxy(getDriver(), getViewUrl()));
    }

    private String getViewUrl() {
        return LoadTestItHelper.getRootURL() + "/" + getViewName();
    }

    public abstract String getViewName();
}
