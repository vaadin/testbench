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
 * <p/>
 *
 * <p>
 * Methods annotated with BrowserConfiguration annotation should return a <b>
 * {@link Collection}&lt;{@link DesiredCapabilities}&gt;</b>
 * </p>
 *
 * <p>
 * Example:<br />
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
 *
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BrowserConfiguration {

}
