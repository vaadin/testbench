/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend;

import org.jspecify.annotations.Nullable;

import com.vaadin.testbench.Capabilities;

/**
 * The browser the JUnit integration asks a {@link BrowserLauncher} for, as
 * {@code @RunLocally}, {@code @RunOnHub}, {@code @BrowserConfiguration}, Sauce
 * Labs and the {@code Parameters} describe it.
 *
 * @param hubUrl
 *            the Selenium hub to run the browser on, or {@code null} to run it
 *            on this machine
 */
public record BrowserSetup(Capabilities capabilities, @Nullable String hubUrl) {
}
