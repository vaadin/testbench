/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to use to scan given packages for component wrappers outside the
 * default {@code com.vaadin.flow.component}.
 * <p/>
 * This makes adding custom component wrappers simpler as they can then use
 * package protected fields and methods.
 * 
 * @deprecated Replace the vaadin-testbench-unit dependency with
 *             browserless-test-junit6 and use the corresponding class from the
 *             com.vaadin.browserless package instead. This class will be
 *             removed in a future version.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Deprecated(forRemoval = true, since = "10.1")
public @interface ComponentTesterPackages {

    /**
     * Array of packages to scan for {@link ComponentTester} implementations.
     * <p/>
     * Implementation should use the {@link Tests} annotation to be used
     * automatically in the {@code wraps(Component)} method.
     *
     * @return String array of packages to scan
     */
    String[] value();
}
