/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
            return TestBenchDriverProxy.findElementsByVaadinSelector(
                    vaadinSelector, context);
        }
        
        /**
         * Returns a WebElement identified by a Vaadin ComponentFinder
         * selector.
         * 
         * @param context
         *            SearchContext for originating the search
         * @return First found WebElement
         */        
        @Override
        public WebElement findElement(SearchContext context) {
            return TestBenchDriverProxy.findElementByVaadinSelector(
                    vaadinSelector, context);
        }

        @Override
        public String toString() {
            return "By.vaadin: " + vaadinSelector;
        }
    }

}
