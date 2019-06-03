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

import com.vaadin.testbench.addons.webdriver.BrowserType;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;
import com.vaadin.testbench.annotations.AnnotationHelper;
import com.vaadin.testbench.configuration.ConfigurationFinder;
import com.vaadin.testbench.configuration.TestConfiguration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

import static com.vaadin.testbench.addons.webdriver.BrowserDriverFunctions.createDrivers;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.storeWebDriver;

public class PageObjectInvocationContextProvider implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        final Collection<BrowserType> skippedBrowsers = context
                .getElement()
                .map(e -> AnnotationHelper.mergeSpecifiedParameters(
                        e, SkipBrowsers.class, SkipBrowsers::value, new HashSet<>()))
                .map(AnnotationHelper::flatten)
                .get();

        final TestConfiguration testConfiguration = ConfigurationFinder.findTestConfiguration();

        return createDrivers(testConfiguration, skippedBrowsers)
                .map(WebDriverTemplateInvocationContextImpl::new)
                .peek(po -> storeWebDriver(context, po.webdriver()))
                .map(TestTemplateInvocationContext.class::cast);
    }
}
