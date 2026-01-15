/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.WebDriver;

/**
 * <p>
 * Test classes annotated with RunOnHub will be run on the hub whose host is
 * defined by its value. Default value is "localhost".
 * </p>
 * 
 * <p>
 * This parameter will be used to instantiate the {@link WebDriver}.<br>
 * </p>
 * 
 * Usage:<br>
 * 
 * <b>@RunOnHub("hub.host.example.com");</b>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface RunOnHub {

    public String value() default "localhost";
}
