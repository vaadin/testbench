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
import com.vaadin.testbench.spi.BrowserBackend;

/**
 * A backend module's way in: starts browsers for the JUnit integration, and
 * puts the backend under a driver a test built with the module's library.
 * {@link BackendChoice} finds each with {@link java.util.ServiceLoader}, so a
 * backend module lists its launcher in
 * {@code META-INF/services/com.vaadin.testbench.backend.BrowserLauncher}.
 */
public interface BrowserLauncher {

    /** As {@link com.vaadin.testbench.Parameters#getBackend()} names it. */
    String getBackendName();

    /**
     * A new browser, isolated from any other this launcher started.
     *
     * @throws UnsupportedOperationException
     *             if this backend cannot run the browser the setup asks for,
     *             saying why
     */
    BrowserBackend launch(BrowserSetup setup) throws Exception;

    /**
     * @return the backend over a driver of this launcher's library, or
     *         {@code null} when the driver is not one
     */
    @Nullable
    BrowserBackend wrap(Object driver);

    /** What {@link #wrap(Object)} takes, for an error message. */
    String describeDrivers();

    /**
     * @return a copy of capabilities in this launcher's library's type, or
     *         {@code null} when they are not of it
     */
    @Nullable
    Capabilities toCapabilities(Object capabilities);
}
