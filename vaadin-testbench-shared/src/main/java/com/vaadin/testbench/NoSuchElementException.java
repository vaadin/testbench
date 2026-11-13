/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

/**
 * A search found no element. A {@link Wait} ignores it and keeps polling.
 * <p>
 * Extends {@link java.util.NoSuchElementException}, which some element classes
 * throw for the same thing, so one {@code catch} covers both.
 */
public class NoSuchElementException extends java.util.NoSuchElementException {

    public NoSuchElementException(String message) {
        super(message);
    }

    public NoSuchElementException(String message, Throwable cause) {
        super(message, cause);
    }
}
