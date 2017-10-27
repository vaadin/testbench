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
