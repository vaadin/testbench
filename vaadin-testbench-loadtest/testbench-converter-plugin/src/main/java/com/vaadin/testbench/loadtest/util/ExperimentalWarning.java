/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Logs a one-time warning that the load testing support is experimental.
 */
public final class ExperimentalWarning {

    private static final AtomicBoolean LOGGED = new AtomicBoolean(false);
    private static final Logger log = Logger
            .getLogger(ExperimentalWarning.class.getName());

    private ExperimentalWarning() {
    }

    /**
     * Logs a warning that Vaadin load testing support is experimental. The
     * message is only logged once per JVM lifetime.
     */
    public static void log() {
        if (LOGGED.compareAndSet(false, true)) {
            log.warning(
                    "NOTE: Vaadin load testing support is experimental and may change in future releases.");
        }
    }
}
