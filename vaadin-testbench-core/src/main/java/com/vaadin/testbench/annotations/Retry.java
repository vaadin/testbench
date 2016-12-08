package com.vaadin.testbench.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface Retry {
    int count() default 10;
}
