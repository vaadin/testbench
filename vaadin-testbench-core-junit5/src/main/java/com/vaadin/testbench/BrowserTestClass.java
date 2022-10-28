/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import com.vaadin.testbench.browser.MultipleBrowsersExtension;

/**
 * Shorthand annotation for enabling
 * {@code @ExtendWith(CapabilitiesInvocationContextProvider.class)} on test
 * class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MultipleBrowsersExtension.class)
public @interface BrowserTestClass {

}
