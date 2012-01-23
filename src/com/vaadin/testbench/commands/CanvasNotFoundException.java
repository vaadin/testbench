/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.testbench.commands;

public class CanvasNotFoundException extends RuntimeException {

    public CanvasNotFoundException(String message) {
        super(message);
    }

    public CanvasNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
