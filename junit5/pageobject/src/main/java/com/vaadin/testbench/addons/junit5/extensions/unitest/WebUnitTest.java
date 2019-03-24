package com.vaadin.testbench.addons.junit5.extensions.unitest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import com.vaadin.testbench.addons.junit5.extensions.container.ServletContainerExtension;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ServletContainerExtension.class)
@ExtendWith(PageObjectInvocationContextProvider.class)
//@ExtendWith(VideoManagementExtension.class)
@ExtendWith(PreLoadTargetExtension.class)
@ExtendWith(PageObjectWebDriverCleanerExtension.class)
public @interface WebUnitTest {

//  Class navigate() default Object.class;

  String navigateAsString() default "";

  boolean preLoad() default true;

}
