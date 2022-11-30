/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

/**
 * Implemented by elements which support selecting an option by matching the
 * text shown to the user.
 */
public interface HasSelectByText {
    /**
     * Selects the first option matching the given text.
     *
     * @param text
     *            the text of the option to select
     */
    public void selectByText(String text);

    /**
     * Gets the text of the currently selected option.
     *
     * @return the text of the current option
     */
    public String getSelectedText();

}
