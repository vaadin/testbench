/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import org.openqa.selenium.WebElement;

public interface HasCallFunction extends WebElement {

    /**
     * Invoke the given method on this element using the given arguments as
     * arguments to the method.
     *
     * @param methodName
     *            the method to invoke
     * @param args
     *            the arguments to pass to the method
     * @return the value returned by the method
     */
    public Object callFunction(String methodName, Object... args);

}
