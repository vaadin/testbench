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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testUI.ElementAttributeUI;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

public class ElementAttributeIT extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ElementAttributeUI.class;
    }

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void testId() {
        TestBenchElement regularField = (TestBenchElement) findElement(
                By.id("regularField"));

        assertEquals("Unexpected id value,", "regularField",
                regularField.getId());
    }

    @Test
    public void testReadOnly() {
        TestBenchElement regularField = (TestBenchElement) findElement(
                By.id("regularField"));
        TestBenchElement readOnlyField = (TestBenchElement) findElement(
                By.id("readOnlyField"));

        assertFalse("Unexpected state, regular field should not be read-only",
                regularField.isReadOnly());
        assertTrue("Unexpected state, read-only field was not read-only",
                readOnlyField.isReadOnly());
    }

    @Test
    public void testClickFocus() {
        // test a couple of different components to rule out component-specific
        // click behavior
        TestBenchElement regularField = (TestBenchElement) findElement(
                By.id("regularField"));
        assertFalse("TextField should not be focused before it's clicked.",
                regularField.isFocused());
        regularField.click();
        assertTrue("TextField should be focused after it's clicked.",
                regularField.isFocused());

        TestBenchElement button = (TestBenchElement) findElement(
                By.id("button"));
        assertFalse("Button should not be focused before it's clicked.",
                button.isFocused());
        button.click();
        assertTrue("Button should be focused after it's clicked.",
                button.isFocused());
    }
}
