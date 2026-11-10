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
 * Implemented by elements which support a label via its text, such as buttons.
 *
 * @since 9.3
 */
public interface HasLabelAsText {
    /**
     * Returns the element's visible text, which serves as its label.
     */
    String getText();

}
