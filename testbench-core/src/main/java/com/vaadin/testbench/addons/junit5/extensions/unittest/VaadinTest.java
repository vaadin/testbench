package com.vaadin.testbench.addons.junit5.extensions.unittest;

import com.vaadin.testbench.addons.junit5.extensions.ConvertWebdriverTestExtension;
import com.vaadin.testbench.addons.junit5.extensions.container.ServletContainerExtension;
import com.vaadin.testbench.addons.junit5.extensions.unitest.PageObjectInvocationContextProvider;
import com.vaadin.testbench.addons.junit5.extensions.unitest.PageObjectWebDriverCleanerExtension;
import com.vaadin.testbench.addons.screenshot.FailedTestScreenhsotExtension;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ServletContainerExtension.class)
@ExtendWith(PageObjectInvocationContextProvider.class)
@ExtendWith(ConvertWebdriverTestExtension.class)
@ExtendWith(VaadinPreLoadTargetExtension.class)
@ExtendWith(FailedTestScreenhsotExtension.class)
@ExtendWith(PageObjectWebDriverCleanerExtension.class)
@TestTemplate
public @interface VaadinTest {

    String navigateAsString() default "";

    boolean preLoad() default true;
}
