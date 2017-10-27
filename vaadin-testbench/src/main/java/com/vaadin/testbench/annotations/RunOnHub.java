/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
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
