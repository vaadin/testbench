package com.vaadin.testbench.elements;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines server side class name for an {@link AbstractElement}. This is used
 * when searching for Components with TestBench4 helper functions or
 * ComponentFinder. Element class must have this annotation defined. If
 * annotation is not present, ComponentFinder will throw an error.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerClass {

    String value();

}
