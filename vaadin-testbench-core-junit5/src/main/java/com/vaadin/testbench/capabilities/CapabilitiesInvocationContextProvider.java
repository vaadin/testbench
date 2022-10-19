/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.capabilities;

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

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestNameSuffix;

/**
 * Provides support for running test against multiple browser capabilities.
 */
public class CapabilitiesInvocationContextProvider
        implements TestTemplateInvocationContextProvider, BeforeAllCallback {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
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
                    .singletonList(new CapabilitiesExtension(capabilities));
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
