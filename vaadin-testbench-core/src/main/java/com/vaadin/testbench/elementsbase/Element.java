package com.vaadin.testbench.elementsbase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Element {

    /**
     * The tag for the element.
     *
     * @return the tag for the element
     */
    String value();

}
