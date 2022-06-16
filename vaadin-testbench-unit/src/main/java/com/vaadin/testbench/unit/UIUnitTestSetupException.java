/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
