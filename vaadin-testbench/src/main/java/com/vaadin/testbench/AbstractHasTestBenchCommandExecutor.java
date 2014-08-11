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

import com.vaadin.testbench.elementsbase.AbstractElement;

/**
 * Abstract class that provides useful Element finding functions. Extended in
 * {@link TestBenchElement} and {@link TestBenchTestCase}. These classes have
 * their own search contexts to manage scoping element searches where needed.
 */
public abstract class AbstractHasTestBenchCommandExecutor implements
        HasTestBenchCommandExecutor {

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. If this function gets called through an element,
     * it uses the element as its search context. Otherwise the search context
     * is the driver.
     * 
     * @return an appropriate {@link ElementQuery} instance
     */
    public <T extends AbstractElement> ElementQuery<T> $(Class<T> clazz) {
        return new ElementQuery<T>(clazz).context(getContext());
    }

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. If this function gets called through an element,
     * it uses the element as its search context. Otherwise the search context
     * is the driver.
     * 
     * This search is not recursive and can find the given hierarchy only if it
     * can be found as direct children of given context. The same can be done
     * with {@code $(Foo.class).recursive(false) }
     * 
     * @return an appropriate {@link ElementQuery} instance
     */
    public <T extends AbstractElement> ElementQuery<T> $$(Class<T> clazz) {
        return new ElementQuery<T>(clazz).context(getContext())
                .recursive(false);
    }

    /**
     * Returns true if a component can be found.
     * 
     * @param clazz
     *            AbstractElement subclass representing a Vaadin component
     * @return true if component of given class can be found
     */
    public boolean isElementPresent(Class<? extends AbstractElement> clazz) {
        return !$(clazz).all().isEmpty();
    }

    /**
     * Returns true if a component can be found with given By selector.
     * 
     * @param by
     *            the selector used to find element
     * @return true if the component can be found
     */
    public boolean isElementPresent(org.openqa.selenium.By by) {
        return !getContext().findElements(by).isEmpty();
    }

}
