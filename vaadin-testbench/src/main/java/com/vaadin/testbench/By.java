package com.vaadin.testbench;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;

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
         * Returns a single WebElement identified by a Vaadin ComponentFinder
         * selector, wrapped in a List, or an empty list if no elements were
         * found.
         */
        @Override
        public List<WebElement> findElements(SearchContext context) {
            WebElement e = findElement(context);
            if (e == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(e);
        }

        /**
         * Return a single WebElement identified by a Vaadin ComponentFinder
         * selector, or null, if no matching element was found.
         */
        @Override
        public WebElement findElement(SearchContext context) {
            return TestBenchCommandExecutor.findElementByVaadinSelector(
                    vaadinSelector, context);
        }

        @Override
        public String toString() {
            return "By.vaadin: " + vaadinSelector;
        }
    }

}
