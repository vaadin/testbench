package com.vaadin.testbench.addons.junit5.extensions.container;

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
