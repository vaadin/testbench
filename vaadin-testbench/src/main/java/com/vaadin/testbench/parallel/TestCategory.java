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
