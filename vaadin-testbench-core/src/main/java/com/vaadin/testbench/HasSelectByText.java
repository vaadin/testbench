/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
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
