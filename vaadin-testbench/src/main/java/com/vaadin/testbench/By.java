/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
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
