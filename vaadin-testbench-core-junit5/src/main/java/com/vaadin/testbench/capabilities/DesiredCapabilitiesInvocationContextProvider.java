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
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestNameSuffix;

/**
 * Provides support for running test against multiple browser capabilities.
 */
public class DesiredCapabilitiesInvocationContextProvider
        implements TestTemplateInvocationContextProvider, BeforeAllCallback {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        BrowserUtil.setBrowserFactory(
                DesiredCapabilitiesUtil.getBrowserFactory(context));
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
            ExtensionContext context) {
        return DesiredCapabilitiesUtil.getDesiredCapabilities(context).stream()
                .map(dc -> new DesireCapabilitiesTestTemplateInvocationContext(
                        context, dc));
    }

    private class DesireCapabilitiesTestTemplateInvocationContext
            implements TestTemplateInvocationContext {

        private final ExtensionContext context;

        private final DesiredCapabilities desiredCapabilities;

        public DesireCapabilitiesTestTemplateInvocationContext(
                ExtensionContext context,
                DesiredCapabilities desiredCapabilities) {
            this.context = context;
            this.desiredCapabilities = desiredCapabilities;
        }

        @Override
        public String getDisplayName(int invocationIndex) {
            return String.format("%s[%s]",
                    context.getRequiredTestMethod().getName()
                            + getTestNameSuffix(context),
                    DesiredCapabilitiesUtil
                            .getUniqueIdentifier(desiredCapabilities));
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return Collections.singletonList(
                    new DesiredCapabilitiesExtension(desiredCapabilities));
        }

        private String getTestNameSuffix(ExtensionContext context) {
            Optional<TestNameSuffix> testNameSuffixProperty = AnnotationUtils
                    .findAnnotation(context.getRequiredTestClass(),
                            TestNameSuffix.class);
            return testNameSuffixProperty.map(testNameSuffix -> "-" + System
                    .getProperty(testNameSuffix.property())).orElse("");
        }

    }

}
