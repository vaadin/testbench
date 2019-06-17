package com.vaadin.testbench.elementsbase;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.testbench.ElementQuery;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the tag name of the element class.
 * <p>
 * Used when looking up elements using {@link ElementQuery}.
 */
@Inherited
@Target(TYPE)
@Retention(RUNTIME)
public @interface Element {

    /**
     * The tag for the element.
     *
     * @return the tag for the element
     */
    String value();
}
