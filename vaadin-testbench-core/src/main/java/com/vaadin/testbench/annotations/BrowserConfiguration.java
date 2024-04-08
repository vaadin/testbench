/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * <p>
 * Methods annotated with BrowserConfiguration in test classes will be used to
 * configure the browsers that are to be used for test cases in that class. Each
 * test case is run on each of the browsers returned by the method.
 * </p>
 * 
 * <p>
 * Methods annotated with BrowserConfiguration annotation should return a <b>
 * {@link Collection}&lt;{@link DesiredCapabilities}&gt;</b>
 * </p>
 * 
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * &#064;BrowserConfiguration
 * public List&lt;DesiredCapabilities&gt; firefoxAndChromeConfiguration() {
 *     List&lt;DesiredCapabilities&gt; list = new ArrayList&lt;DesiredCapabilities&gt;();
 *     list.add(Browser.FIREFOX.getDesiredCapabilities());
 *     list.add(Browser.CHROME.getDesiredCapabilities());
 *     list.add(Browser.PHANTOMJS.getDesiredCapabilities());
 *     return list;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BrowserConfiguration {

}
