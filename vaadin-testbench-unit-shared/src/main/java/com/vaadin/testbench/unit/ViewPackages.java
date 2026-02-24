/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to use to scan given packages for routes and error views.
 *
 * Packages can be defined by their fully-qualified name or by providing classes
 * that are members of them.
 *
 * If both {@link #classes()} and {@link #packages()} are empty, the scan is
 * assumed to be limited to the annotated class package.
 
  * @deprecated Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.
  */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Deprecated(forRemoval = true, since = "10.1")
public @interface ViewPackages {

    /**
     * Array of classes whose packages will be scanned for views
     *
     * @return Array of classes whose packages will be scanned for views
     */
    Class<?>[] classes() default {};

    /**
     * Array of packages to scan for views
     *
     * @return String array of packages to scan
     */
    String[] packages() default {};
}
