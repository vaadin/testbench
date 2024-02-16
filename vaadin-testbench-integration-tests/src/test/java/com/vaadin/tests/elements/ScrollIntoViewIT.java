/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

public class ScrollIntoViewIT extends MultiBrowserTest {
    private TestBenchElement lastButton;
    private int initialY;

    @Before
    public void setUp() {
        openTestURL();
        lastButton = (TestBenchElement) findElements(By.className("v-button"))
                .get(29);
        initialY = lastButton.getLocation().getY();

    }

    @Test
    public void getText_scrollIntoViewEnabled_ElementIsScrolled() {
        Assert.assertFalse(lastButton.isDisplayed());
        Assert.assertEquals("Button 29", lastButton.getText());
        Assert.assertTrue(lastButton.isDisplayed());
        Assert.assertNotEquals(initialY, lastButton.getLocation().getY());
    }

    @Test
    public void click_scrollIntoViewEnabled_ElementIsScrolled() {
        Assert.assertFalse(lastButton.isDisplayed());
        lastButton.click();
        Assert.assertTrue(lastButton.isDisplayed());
        Assert.assertNotEquals(initialY, lastButton.getLocation().getY());
    }

    @Test
    public void getText_scrollIntoViewDisabled_ElementIsNotScrolled() {
        Assert.assertFalse(lastButton.isDisplayed());
        getCommandExecutor().setAutoScrollIntoView(false);
        Assert.assertNotEquals("Button 29", lastButton.getText());
        Assert.assertEquals(initialY, lastButton.getLocation().getY());
    }

    @Test
    public void click_scrollIntoViewDisabled_ElementIsNotScrolled() {
        Assert.assertFalse(lastButton.isDisplayed());
        getCommandExecutor().setAutoScrollIntoView(false);
        try {
            lastButton.click();
            Assert.fail("Expected an ElementNotVisibleException to be thrown");
        } catch (ElementNotVisibleException anElementNotVisibleException) {
            Assert.assertEquals(initialY, lastButton.getLocation().getY());
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
