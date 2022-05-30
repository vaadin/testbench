/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to use to scan given packages for component wrappers outside the
 * default {@code com.vaadin.flow.component}.
 * <p/>
 * This makes adding custom component wrappers simpler as they can then use
 * package protected fields and methods.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentWrapPackages {

    /**
     * Array of packages to scan for {@link ComponentWrap} implementations.
     * <p/>
     * Implementation should use the {@link Wraps} annotation to be used
     * automatically in the {@code wraps(Component)} method.
     *
     * @return String array of packages to scan
     */
    String[] value();
}
