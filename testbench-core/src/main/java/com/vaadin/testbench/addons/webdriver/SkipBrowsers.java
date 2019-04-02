package com.vaadin.testbench.addons.webdriver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipBrowsers {

    BrowserTypes[] ALL_BROWSERS = new BrowserTypes[0];

    BrowserTypes[] value();
}
