package com.vaadin.testbench.addons.junit5.extensions.unitest;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Copyright (C) ${year} Vaadin Ltd
 * 
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

import com.vaadin.testbench.addons.webdriver.BrowserTypes;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.vaadin.testbench.addons.webdriver.BrowserDriverFunctions.webDriverInstances;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.storeWebDriver;
import static java.util.Collections.emptyList;

public class PageObjectInvocationContextProvider implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        final List<BrowserTypes> typesList = context
                .getTestMethod()
                .filter(method -> method.isAnnotationPresent(SkipBrowsers.class))
                .map((method) -> method.getAnnotation(SkipBrowsers.class).value())
                .map(Arrays::asList)
                .orElse(emptyList());

        return webDriverInstances(typesList)
                .map(e -> new WebDriverTemplateInvocationContextImpl(this, e))
                .peek(po -> storeWebDriver(context, po.webdriver()))
                .map(TestTemplateInvocationContext.class::cast);
    }
}
