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
 * Marks a method the backend-neutral implementation does not serve yet:
 *
 * <pre>{@code
 * public String getCssValue(String propertyName) {
 *     throw notYetImplemented("getCssValue");
 * }
 * }</pre>
 *
 * Always thrown, never swapped for a default value: a {@code null} or
 * {@code false} would let a test pass that should fail. Temporary, until
 * TestBench is off Selenium; {@code grep notYetImplemented} counts what is
 * left.
 */
public final class NotYetImplementedException
        extends UnsupportedOperationException {

    private NotYetImplementedException(String what) {
        super(what + " is not implemented yet");
    }

    /**
     * @param what
     *            the method or feature, as the message names it
     */
    public static NotYetImplementedException notYetImplemented(String what) {
        return new NotYetImplementedException(what);
    }
}
