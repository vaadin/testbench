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

/**
 * Abstract class that provides useful Element finding functions. Extended in
 * {@link TestBenchElement} and {@link TestBenchTestCase}. These classes have
 * their own search contexts to manage scoping element searches where needed.
 */
public abstract class AbstractHasTestBenchCommandExecutor
        implements HasTestBenchCommandExecutor, HasElementQuery {

    @Override
    public <T extends TestBenchElement> ElementQuery<T> $(Class<T> clazz) {
        return new ElementQuery<>(clazz).context(getContext());
    }

    /**
     * Returns true if a component can be found.
     *
     * @param clazz
     *            TestBenchElement subclass representing a Vaadin component
     * @return true if component of given class can be found
     */
    public boolean isElementPresent(Class<? extends TestBenchElement> clazz) {
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
