/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
