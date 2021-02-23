/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
