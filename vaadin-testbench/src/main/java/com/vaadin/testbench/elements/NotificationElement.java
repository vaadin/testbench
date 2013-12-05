package com.vaadin.testbench.elements;

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
}
