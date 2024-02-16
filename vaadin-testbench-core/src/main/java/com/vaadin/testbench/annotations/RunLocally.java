/*
 * Copyright (C) 2000-2024 Vaadin Ltd
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

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.ParallelTest;

/**
 * Marks this testcase to run locally.<br>
 * Can receive a parameter to define which browser and version to run the test
 * on.<br>
 * <p>
 * You can also define a browser name/version to run on locally using the
 * {@code com.vaadin.testbench.Parameters.runLocally} system parameter.
 * <p>
 * This parameter will be used to instantiate the {@link WebDriver}.<br>
 * The value can be obtained through {@link ParallelTest#getRunLocallyBrowser()}
 * and {@link ParallelTest#getRunLocallyBrowserVersion()}
 * </p>
 * 
 * <p>
 * Usage:<br>
 * {@code @RunLocally(Browser.CHROME)}
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface RunLocally {
    public Browser value() default Browser.FIREFOX;

    public String version() default "";
}
