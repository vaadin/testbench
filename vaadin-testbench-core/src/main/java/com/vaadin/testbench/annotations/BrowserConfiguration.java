/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
 *     return list;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BrowserConfiguration {

}
