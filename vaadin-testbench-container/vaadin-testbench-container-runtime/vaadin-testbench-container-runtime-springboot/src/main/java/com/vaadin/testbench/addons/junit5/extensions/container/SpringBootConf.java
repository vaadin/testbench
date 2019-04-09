package com.vaadin.testbench.addons.junit5.extensions.container;

/*-
 * #%L
 * vaadin-testbench-container-runtime-springboot
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface SpringBootConf {

    /**
     * @return the class that is used to start the Spring Boot Application
     */
    Class<?> source();

    /**
     * Additional Application-Params that should be used for this test
     *
     * @return
     */
    String[] args() default {};
}
