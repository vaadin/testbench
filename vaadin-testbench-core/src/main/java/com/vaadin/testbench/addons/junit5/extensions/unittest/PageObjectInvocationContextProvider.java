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
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.vaadin.testbench.addons.webdriver.BrowserDriverFunctions.webDriverInstances;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.storeWebDriver;

public class PageObjectInvocationContextProvider implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        final Set<BrowserType> typesList = context
                .getElement()
                .map(e -> collectSkippedBrowsers(e, new HashSet<>()))
                .get();

        return webDriverInstances(typesList)
                .map(WebDriverTemplateInvocationContextImpl::new)
                .peek(po -> storeWebDriver(context, po.webdriver()))
                .map(TestTemplateInvocationContext.class::cast);
    }

    private Set<BrowserType> collectSkippedBrowsers(AnnotatedElement element, Set<BrowserType> output) {
        if (element == null || element == Object.class) {
            return output;
        }

        final SkipBrowsers annotation = element.getAnnotation(SkipBrowsers.class);

        if (annotation != null) {
            output.addAll(Arrays.asList(annotation.value()));
        }

        final AnnotatedElement parent = element instanceof Method
                ? ((Method) element).getDeclaringClass()
                : ((Class) element).getSuperclass();

        return collectSkippedBrowsers(parent, output);
    }
}
