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

import com.vaadin.flow.component.Component;

/**
 * Wrapper annotation for indicating which components a wrapper implementation
 * supports.
 * <p/>
 * This is used for automatically selecting a wrapper implementation for a given
 * component.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Tests {

    /**
     * Array of the classes that is wrapped by the annotated
     * {@link ComponentTester}
     *
     * @return {@link Component} classes that can be wrapped
     */
    Class<? extends Component>[] value() default {};

    String[] fqn() default {};
}
