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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

public class ScrollIntoViewIT extends MultiBrowserTest {
    private TestBenchElement lastButton;
    private int initialY;

    @Before
    public void setUp() {
        // intentionally not overriding either getDeploymentPath() or
        // getUIClass() but relying on a matching UI class name in order to test
        // different ways of determining the deployment path

        openTestURL();
        lastButton = (TestBenchElement) findElements(By.className("v-button"))
                .get(29);
        initialY = lastButton.getLocation().getY();
    }

    @Test
    public void getText_scrollIntoViewEnabled_ElementIsScrolled() {
        assertFalse(lastButton.isDisplayed());
        assertEquals("Button 29", lastButton.getText());
        assertTrue(lastButton.isDisplayed());
        assertNotEquals(initialY, lastButton.getLocation().getY());
    }

    @Test
    public void click_scrollIntoViewEnabled_ElementIsScrolled() {
        assertFalse(lastButton.isDisplayed());
        lastButton.click();
        assertTrue(lastButton.isDisplayed());
        assertNotEquals(initialY, lastButton.getLocation().getY());
    }

    @Test
    public void getText_scrollIntoViewDisabled_ElementIsNotScrolled() {
        assertFalse(lastButton.isDisplayed());
        getCommandExecutor().setAutoScrollIntoView(false);
        assertNotEquals("Button 29", lastButton.getText());
        assertEquals(initialY, lastButton.getLocation().getY());
    }

    @Test
    @Ignore("This seems to fail on Firefox")
    public void click_scrollIntoViewDisabled_ElementIsNotScrolled() {
        assertFalse(lastButton.isDisplayed());
        getCommandExecutor().setAutoScrollIntoView(false);
        try {
            lastButton.click();
            fail("Expected an ElementNotInteractableException to be thrown");
        } catch (ElementNotInteractableException e) {
            assertEquals(initialY, lastButton.getLocation().getY());
        }
    }

    @Override
    public List<DesiredCapabilities> getBrowserConfiguration() {
        /**
         * IEDriver returns true in initial isDisplayed check and
         * hiddenButton.click() doesn't throw ElementNotVisibleException
         */
        return getBrowsersExcludingIE();
    }
}
