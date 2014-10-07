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

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Notification")
public class NotificationElement extends AbstractElement {
    /**
     * Closes a notification
     *
     * @throws TimeoutException
     *             If a notification can not be closed and the timeout expires.
     */
    public void closeNotification() {
        click();
        WebDriverWait wait = new WebDriverWait(getDriver(), 10);
        wait.until(ExpectedConditions.not(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.className("v-Notification"))));

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

    /**
     * Returns description of the Notification element
     *
     * @return description of the Notification element
     */
    public String getDescription() {
        WebElement popup = findElement(By.className("popupContent"));
        WebElement caption = popup.findElement(By.tagName("p"));
        return caption.getText();
    }

    /**
     * Returns type of the Notification element
     *
     * @return type of the Notification element
     */
    public String getType() {
        // The info about notification type can be taken only from css rule of
        // the notification
        // To get type we search for css rules which represent notification type
        // This map maps css style rule to type of a notification
        HashMap<String, String> styleToTypeMap = initStyleToTypeMap();
        for (Map.Entry<String, String> entry : styleToTypeMap.entrySet()) {
            String notifType = entry.getKey();
            // Check notification has css style which describes notification
            // type
            if (getAttribute("class").contains(notifType)) {
                return entry.getValue();
            }
        }
        return "";
    }

    private HashMap<String, String> initStyleToTypeMap() {
        HashMap<String, String> styleToType = new HashMap<String, String>();
        styleToType.put("v-Notification-error", "error");
        styleToType.put("v-Notification-warning", "warning");
        styleToType.put("v-Notification-humanized", "humanized");
        styleToType.put("v-Notification-tray", "tray_notification");
        return styleToType;
    }
}
