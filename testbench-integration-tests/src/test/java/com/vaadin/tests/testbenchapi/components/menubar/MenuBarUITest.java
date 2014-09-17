/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.testbenchapi.components.menubar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

/**
 */
public class MenuBarUITest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    // Tests against bug #14568
    // @Test
    public void testClickTopLevelItemHavingSubmenuItemFocused() {
        MenuBarElement menuBar = $(MenuBarElement.class).first();

        menuBar.open("File");
        assertTrue(isItemVisible("Export.."));

        menuBar.open("Export..");
        assertTrue(isItemVisible("As PDF..."));

        menuBar.clickItem("File");
        assertFalse(isItemVisible("Export.."));
    }

    /**
     * Validates MenuBarElement general open(String) and clickItem(String)
     * features.
     */
    @Test
    public void testMenuBarOpenAndClick() {
        MenuBarElement menuBar = $(MenuBarElement.class).first();

        menuBar.clickItem("File");
        assertTrue(isItemVisible("Save As.."));

        menuBar.open("Export..");
        assertTrue(isItemVisible("As PDF..."));

        menuBar.open("Edit");
        assertFalse(isItemVisible("Save As.."));
        assertTrue(isItemVisible("Paste"));

        menuBar.clickItem("Edit");
        assertFalse(isItemVisible("Save As.."));
        assertFalse(isItemVisible("Paste"));

        menuBar.clickItem("Edit");
        assertFalse(isItemVisible("Save As.."));
        assertTrue(isItemVisible("Paste"));

        menuBar.open("Help");
        assertFalse(isItemVisible("Save As.."));
        assertFalse(isItemVisible("Paste"));

        menuBar.clickItem("File");
        assertTrue(isItemVisible("Save As.."));
    }

    /**
     * Validates menuBar.clickItem(String...) feature.
     */
    @Test
    public void testMenuBarClickPath() {
        MenuBarElement menuBar = $(MenuBarElement.class).first();
        menuBar.clickItem("File", "Export..");
        assertTrue(isItemVisible("As Doc..."));
    }

    /**
     * Tests whether the MenuBar selected and its items, are the correct ones or
     * not (when several MenuBar exist, possibly with ).
     */
    @Test
    public void testMenuBarSelector() {
        MenuBarElement menuBar = $(MenuBarElement.class).get(2);

        menuBar.open("File");
        assertTrue(isItemVisible("Open2"));
        menuBar.closeAll();

        menuBar = $(MenuBarElement.class).get(1);
        menuBar.clickItem("Edit2");
        assertTrue(isItemVisible("Cut"));
        menuBar.closeAll();

        menuBar = $(MenuBarElement.class).first();
        menuBar.open("File");
        assertTrue(isItemVisible("Open"));
    }

    private boolean isItemVisible(String item) {
        for (WebElement webElement : getItemCaptions()) {
            if (webElement.getText().equals(item)) {
                return true;
            }
        }
        return false;
    }

    private List<WebElement> getItemCaptions() {
        return findElements(By.className("v-menubar-menuitem-caption"));
    }
}
