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
 * The element's DOM node is gone from the page, as after a re-render or a
 * closed dialog. Element classes catch it to mean "closed":
 *
 * <pre>{@code
 * try {
 *     return getPropertyBoolean("opened");
 * } catch (StaleElementReferenceException e) {
 *     return false;
 * }
 * }</pre>
 */
public class StaleElementReferenceException extends RuntimeException {

    public StaleElementReferenceException(String message) {
        super(message);
    }

    public StaleElementReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
