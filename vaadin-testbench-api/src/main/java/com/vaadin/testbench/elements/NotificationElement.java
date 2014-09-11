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

import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Notification")
public class NotificationElement extends AbstractElement {
    /**
     * Closes a notification
     * 
     * @return true if the notification was successfully closed.
     */
    @Override
    public boolean closeNotification() {
        click();
        try {
            // Wait for 5000 ms or until the element is no longer visible.
            int times = 0;
            while (isDisplayed() || times > 25) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
                times++;
            }
            return !isDisplayed();
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("no longer attached")) {
                // This was some other exception than a no longer attached
                // exception. Rethrow.
                throw e;
            }
            return true;
        }
    }

    /**
     * Returns the caption of the Notification element
     * 
     * @since
     * @return the caption of the Notification element
     */
    public String getCaption() {
        WebElement popup = findElement(By.className("popupContent"));
        WebElement caption = popup.findElement(By.tagName("h1"));
        return caption.getText();

    }
}
