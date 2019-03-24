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
    SearchContext getContext();

}
