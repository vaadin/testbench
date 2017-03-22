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

import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

public abstract class By extends org.openqa.selenium.By {

    /**
     * Finds an element using a Vaadin selector
     * 
     * @param vaadinSelector
     *            the selector string.
     * @return an element or null if none found.
     */
    public static org.openqa.selenium.By vaadin(final String vaadinSelector) {
        if (vaadinSelector == null || "".equals(vaadinSelector)) {
            throw new IllegalArgumentException(
                    "Cannot find elements with a null or empty selector.");
        }

        return new ByVaadin(vaadinSelector);
    }

    public static class ByVaadin extends org.openqa.selenium.By {
        private final String vaadinSelector;

        public ByVaadin(String vaadinSelector) {
            this.vaadinSelector = vaadinSelector;
        }

        /**
         * Returns a list of WebElements identified by a Vaadin ComponentFinder
         * selector.
         * 
         * @param context
         *            SearchContext for originating the search
         * @return List of found WebElements
         */
        @Override
        public List<WebElement> findElements(SearchContext context) {
            return TestBenchDriverProxy
                    .findElementsByVaadinSelector(vaadinSelector, context);
        }

        /**
         * Returns a WebElement identified by a Vaadin ComponentFinder selector.
         * 
         * @param context
         *            SearchContext for originating the search
         * @return First found WebElement
         */
        @Override
        public WebElement findElement(SearchContext context) {
            return TestBenchDriverProxy
                    .findElementByVaadinSelector(vaadinSelector, context);
        }

        @Override
        public String toString() {
            return "By.vaadin: " + vaadinSelector;
        }
    }

}
