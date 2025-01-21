/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

/**
 * Exception thrown by {@link BaseUIUnitTest} methods when the mock environment
 * has not been set up correctly.
 */
public class UIUnitTestSetupException extends RuntimeException {
    public UIUnitTestSetupException(String message) {
        super(message);
    }

    public UIUnitTestSetupException(String message, Throwable cause) {
        super(message, cause);
    }
}
