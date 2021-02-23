/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import org.openqa.selenium.SearchContext;

/**
 * Interface for classes providing a {@link SearchContext}, i.e. if a class can
 * provide a search context while not directly implementing the
 * {@link SearchContext} interface, this interface should be implemented
 *
 */
public interface HasSearchContext {

    /**
     * Get a reference or a new instance of the SearchContext applicable to this
     * class
     *
     * @return a {@link SearchContext} instance
     */
    public SearchContext getContext();

}
