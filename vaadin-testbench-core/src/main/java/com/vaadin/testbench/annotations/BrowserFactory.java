/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.testbench.parallel.DefaultBrowserFactory;
import com.vaadin.testbench.parallel.TestBenchBrowserFactory;

/**
 * <p>
 * {@link BrowserFactory} annotation is used to define which
 * {@link TestBenchBrowserFactory} implementation to use in a test.
 * </p>
 * <p>
 * {@link TestBenchBrowserFactory} should be implemented by another class, or
 * {@link DefaultBrowserFactory} should be extended if a different default
 * browser configuration is needed (for instance, to set the default version of
 * a specific browser).
 * </p>
 * Example:<br>
 * 
 * {@code @BrowserFactory(DefaultBrowserFactory.class)}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface BrowserFactory {
    public Class<?> value() default TestBenchBrowserFactory.class;
}
