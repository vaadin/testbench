/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.parallel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Sets the category of a test class.<br>
 * Categories can be excluded from running tests, or explicitly included.
 * </p>
 * <p>
 * If categories are explicitly included, only non-excluded tests with
 * explicitly included categories will be run.
 * </p>
 * <p>
 * To exclude categories, add a new system variable named "categories.exclude"
 * with the names of the categories excluded.<br>
 * To explicitly include categories, add a new system variable named
 * "categories.include" with the names of the categories included.
 * </p>
 * <p>
 * Usage:<br>
 *
 * {@code @TestCategory("NetworkTest")}
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface TestCategory {

    String value();

}
