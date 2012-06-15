package com.vaadin.testbench;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.commands.TestBenchCommands;

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

        @Override
        public List<WebElement> findElements(SearchContext context) {
            return Arrays.asList(findElement(context));
        }

        @Override
        public WebElement findElement(SearchContext context) {
            if (context instanceof TestBenchCommands) {
                return ((TestBenchCommands) context)
                        .findElementByVaadinSelector(vaadinSelector);
            }
            return null;
        }

        @Override
        public String toString() {
            return "By.vaadin: " + vaadinSelector;
        }
    }

}
