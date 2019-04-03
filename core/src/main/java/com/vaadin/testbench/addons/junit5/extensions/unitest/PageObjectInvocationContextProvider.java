package com.vaadin.testbench.addons.junit5.extensions.unitest;

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
                .filter(method -> method.getAnnotation(SkipBrowsers.class) != null)
                .map((method) -> {
                    final SkipBrowsers annotation = method.getAnnotation(SkipBrowsers.class);
                    final BrowserTypes[] browserTypes = annotation.value();
                    return (browserTypes == null)
                            ? SkipBrowsers.ALL_BROWSERS
                            : browserTypes;
                })
                .map(Arrays::asList)
                .orElse(emptyList());

        return webDriverInstances(typesList)
                .map(e -> new WebDriverTemplateInvocationContextImpl(this, e))
                .peek(po -> {
                    storeWebDriver().accept(context, po.webdriver());
                })
                .map(e -> e);
    }
}
