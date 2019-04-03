package com.vaadin.testbench.addons.junit5.extensions.unitest;

import com.vaadin.testbench.addons.junit5.extensions.container.ServletContainerExtension;
import com.vaadin.testbench.addons.screenshot.FailedTestScreenhsotExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ServletContainerExtension.class)
@ExtendWith(PageObjectInvocationContextProvider.class)
//@ExtendWith(VideoManagementExtension.class)
@ExtendWith(FailedTestScreenhsotExtension.class)
@ExtendWith(PreLoadTargetExtension.class)
@ExtendWith(PageObjectWebDriverCleanerExtension.class)
public @interface WebUnitTest {

    String navigateAsString() default "";

    boolean preLoad() default true;
}
