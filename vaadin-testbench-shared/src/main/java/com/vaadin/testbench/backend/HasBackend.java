/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend;

import com.vaadin.testbench.spi.Backend;

/**
 * A {@link org.openqa.selenium.WebDriver} that is not Selenium's, as a
 * {@link BrowserLauncher} returns it: {@code TestBench.createDriver} drives the
 * browser through its {@link Backend} rather than through Selenium.
 */
public interface HasBackend {

    Backend getBackend();
}
