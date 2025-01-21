/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import com.vaadin.testbench.browser.BrowserExtension;

/**
 * Shorthand annotation for marking parameterized test methods in test class.
 * <p>
 * </p>
 * Combines {@link org.junit.jupiter.params.ParameterizedTest} annotated tests
 * with {@link BrowserExtension} to run browser test methods multiple times with
 * different arguments.
 * <p>
 * </p>
 * Unlike {@link BrowserTest}, this annotation supports only a single browser.
 * If the test class is configured to run on multiple browsers, the test methods
 * annotated with {@link ParameterizedBrowserTest} are disabled.
 * <p>
 * </p>
 * Arguments for test method should be provided in the same way as when
 * using @{@link ParameterizedTest} annotation.
 *
 * @see ParameterizedTest
 * @see BrowserExtension
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ParameterizedTest
@ExtendWith(BrowserExtension.class)
public @interface ParameterizedBrowserTest {

}
