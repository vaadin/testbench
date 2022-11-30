/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.vaadin.testbench.browser.MultipleBrowsersExtension;

/**
 * Shorthand annotation for marking test methods in test class.
 * <p>
 * Combines JUnit 5 {@code @TestTemplate} together with
 * {@code @ExtendWith(CapabilitiesInvocationContextProvider.class)}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
@ExtendWith(MultipleBrowsersExtension.class)
public @interface BrowserTest {

}
