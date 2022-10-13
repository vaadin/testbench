package com.vaadin.testbench.capabilities;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestNameSuffix;

/**
 * Provides support for running test method for each defined
 * {@link DesiredCapabilities}.
 */
public class DesiredCapabilitiesInvocationContextProvider
        implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
            ExtensionContext context) {
        Collection<DesiredCapabilities> desiredCapabilitiesCollection = DesiredCapabilitiesUtil
                .getDesiredCapabilities(context);
        return desiredCapabilitiesCollection.stream()
                .map(dc -> createTestTemplateInvocationContext(dc, context));
    }

    private TestTemplateInvocationContext createTestTemplateInvocationContext(
            DesiredCapabilities desiredCapabilities, ExtensionContext context) {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return String.format("%s[%s]",
                        context.getRequiredTestMethod().getName()
                                + getTestNameSuffix(context),
                        getUniqueIdentifier(desiredCapabilities));
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                List<Extension> extensions = new ArrayList<>();
                extensions.add(
                        new DesiredCapabilitiesExtension(desiredCapabilities));
                return extensions;
            }
        };
    }

    private String getTestNameSuffix(ExtensionContext context) {
        TestNameSuffix testNameSuffixProperty = findAnnotation(
                context.getRequiredTestClass(), TestNameSuffix.class);
        if (testNameSuffixProperty != null) {
            return "-" + System.getProperty(testNameSuffixProperty.property());
        } else {
            return "";
        }
    }

    /**
     * Returns a string which uniquely (enough) identifies this browser. Used
     * mainly in screenshot names.
     */
    private static String getUniqueIdentifier(Capabilities capabilities) {
        String platform = BrowserUtil.getPlatform(capabilities);
        String browser = BrowserUtil.getBrowserIdentifier(capabilities);
        String version;
        if (capabilities == null) {
            version = "Unknown";
        } else {
            version = capabilities.getBrowserVersion();
        }
        return platform + "_" + browser + "_" + version;
    }

    /**
     * Finds the given annotation in the given class or one of its super
     * classes. Return the first found annotation
     *
     * @param searchClass
     * @param annotationClass
     * @return
     */
    private <T extends Annotation> T findAnnotation(Class<?> searchClass,
            Class<T> annotationClass) {
        if (searchClass == Object.class) {
            return null;
        }

        if (searchClass.getAnnotation(annotationClass) != null) {
            return searchClass.getAnnotation(annotationClass);
        }

        return findAnnotation(searchClass.getSuperclass(), annotationClass);
    }

}
