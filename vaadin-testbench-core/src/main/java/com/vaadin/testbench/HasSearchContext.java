/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
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
