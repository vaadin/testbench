/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.util.AnnotationUtils;
import org.openqa.selenium.Capabilities;

import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.BrowserTestClass;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestNameSuffix;

/**
 * Provides support for running test methods using multiple browsers.
 */
public class MultipleBrowsersExtension
        implements TestTemplateInvocationContextProvider, BeforeAllCallback {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return AnnotationUtils.isAnnotated(context.getRequiredTestClass(),
                BrowserTestClass.class)
                || AnnotationUtils.isAnnotated(context.getRequiredTestMethod(),
                        BrowserTest.class);
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        BrowserUtil
                .setBrowserFactory(CapabilitiesUtil.getBrowserFactory(context));
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
            ExtensionContext context) {
        return CapabilitiesUtil.getDesiredCapabilities(context).stream().map(
                dc -> new CapabilitiesTestTemplateInvocationContext(context,
                        dc));
    }

    private static class CapabilitiesTestTemplateInvocationContext
            implements TestTemplateInvocationContext {

        private final ExtensionContext context;

        private final Capabilities capabilities;

        public CapabilitiesTestTemplateInvocationContext(
                ExtensionContext context, Capabilities capabilities) {
            this.context = context;
            this.capabilities = capabilities;
        }

        @Override
        public String getDisplayName(int invocationIndex) {
            return String.format("%s[%s]",
                    context.getRequiredTestMethod().getName()
                            + getTestNameSuffix(context),
                    CapabilitiesUtil.getUniqueIdentifier(capabilities));
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return Collections
                    .singletonList(new BrowserExtension(capabilities));
        }

        private String getTestNameSuffix(ExtensionContext context) {
            Optional<TestNameSuffix> testNameSuffixProperty = AnnotationUtils
                    .findAnnotation(context.getRequiredTestClass(),
                            TestNameSuffix.class);
            return testNameSuffixProperty
                    .map(testNameSuffix -> "-"
                            + System.getProperty(testNameSuffix.property()))
                    .orElse("");
        }

    }

}
