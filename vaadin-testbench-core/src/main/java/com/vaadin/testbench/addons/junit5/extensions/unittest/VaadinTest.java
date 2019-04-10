package com.vaadin.testbench.addons.junit5.extensions.unittest;

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

import com.vaadin.testbench.addons.junit5.extensions.ConvertWebdriverTestExtension;
import com.vaadin.testbench.addons.junit5.extensions.container.ServletContainerExtension;
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
