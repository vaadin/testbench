/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.testbench.testcase;

public class TestFailedException extends RuntimeException {
    public TestFailedException(String message) {
        super(message);
    }

    public TestFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
