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
package com.vaadin.tests.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testUI.TwinColSelectUI;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

public class TwinColSelectIT extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return TwinColSelectUI.class;
    }

    @Test
    public void testSelenium4API() {
        openTestURL();

        List<WebElement> selectElements = findElements(By.tagName("select"));
        assertTrue(selectElements.size() >= 1);

        WebElement select = selectElements.get(0);
        assertTrue(select instanceof TestBenchElement);

        // none of these should throw UnsupportedOperationException
        assertEquals("true", select.getDomAttribute("multiple"));
        assertEquals("listbox", select.getAriaRole());
        assertEquals("", select.getAccessibleName());

        SearchContext shadowRoot = select.getShadowRoot();
        assertNotNull(shadowRoot);
        assertEquals("Unexpected shadow root content,", 0,
                shadowRoot.findElements(By.className("foo"))
                        .size());

        // finally test that the TwinColSelect has expected contents
        assertTrue(new Select(select).getOptions().size() >= 1);
    }
}
