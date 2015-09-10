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
package com.vaadin.testbench.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * Element API for the Window class.
 *
 * Note that parts of the Window element API has limitations on IE8 and Phantom.
 */
@ServerClass("com.vaadin.ui.Window")
public class WindowElement extends PanelElement {

    private static final String HEADER_CLASS = "v-window-header";
    private static final String RESTORE_BOX_CLASS = "v-window-restorebox";
    private static final String MAXIMIZE_BOX_CLASS = "v-window-maximizebox";
    private static final String CLOSE_BOX_CLASS = "v-window-closebox";

    /**
     * Clicks the close button of the window
     */
    public void close() {
        getCloseButton().click();
    }

    /**
     * Clicks the restore button of the window
     */
    public void restore() {
        if (isMaximized()) {
            getRestoreButton().click();
        } else {
            throw new IllegalStateException(
                    "Window is not maximized, cannot be restored.");
        }
    }

    /**
     * Check if this window is currently maximized
     */
    public boolean isMaximized() {
        return isElementPresent(By.className(RESTORE_BOX_CLASS));
    }

    /**
     * Clicks the maximize button of the window
     */
    public void maximize() {
        if (!isMaximized()) {
            getMaximizeButton().click();
        } else {
            throw new IllegalStateException(
                    "Window is already maximized, cannot maximize.");
        }
    }

    private WebElement getRestoreButton() {
        return findElement(By.className(RESTORE_BOX_CLASS));
    }

    private WebElement getMaximizeButton() {
        return findElement(By.className(MAXIMIZE_BOX_CLASS));
    }

    private WebElement getCloseButton() {
        return findElement(By.className(CLOSE_BOX_CLASS));
    }

    /**
     * @return the caption of the window
     */
    @Override
    public String getCaption() {
        return findElement(By.className(HEADER_CLASS)).getText();
    }

}
