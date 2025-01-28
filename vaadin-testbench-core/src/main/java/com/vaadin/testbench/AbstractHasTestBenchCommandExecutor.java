/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
     * @param <T>
     *            the type of the class
     * @param clazz
     *            AbstractElement subclass representing a Vaadin component
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
     * @param <T>
     *            the type of the class
     * @param clazz
     *            AbstractElement subclass representing a Vaadin component
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
