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
import com.vaadin.testbench.configuration.TargetConfiguration;
import com.vaadin.testbench.configuration.Target;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static com.vaadin.testbench.TestBenchLogger.logger;
import static com.vaadin.testbench.addons.webdriver.BrowserDriverFunctions.createDrivers;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.storeWebDriver;

public class PageObjectInvocationContextProvider implements TestTemplateInvocationContextProvider {

    private static final String TESTBENCH_TARGET_CONFIGURATION = "testbench.target.configuration";

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

        final List<Target> targetBrowsers = findBrowserTargets();

        return createDrivers(() -> targetBrowsers, skippedBrowsers)
                .map(WebDriverTemplateInvocationContextImpl::new)
                .peek(po -> storeWebDriver(context, po.webdriver()))
                .map(TestTemplateInvocationContext.class::cast);
    }

    private List<Target> findBrowserTargets() {
        final String targetConfigurationSystemProperty = System.getProperty(TESTBENCH_TARGET_CONFIGURATION);
        if (targetConfigurationSystemProperty != null) {
            logger().debug("TargetBrowser implementation found via system property: "
                    + targetConfigurationSystemProperty);
            return instantiate(targetConfigurationSystemProperty).getBrowserTargets();
        }

        try (ScanResult scanResult = new ClassGraph().enableClassInfo().scan()) {
            final ClassInfoList targetConfiguration = scanResult
                    .getClassesImplementing(TargetConfiguration.class.getCanonicalName());

            if (targetConfiguration.size() == 0) {
                throw new IllegalStateException("No implementation of TargetConfiguration found");
            }

            if (targetConfiguration.size() > 1) {
                throw new IllegalStateException("Multiple implementations of TargetConfiguration found. " +
                        "Either ensure that only one implementation exist " +
                        "or specify the desired implementation by setting the system property '"
                        + TESTBENCH_TARGET_CONFIGURATION + "'");
            }

            final String targetConfigurationClassName = targetConfiguration.get(0).getName();
            logger().debug("TargetBrowser implementation found by class scanning: "
                    + targetConfigurationClassName);

            return instantiate(targetConfigurationClassName).getBrowserTargets();
        }
    }

    private TargetConfiguration instantiate(String fullyQualifiedTargetConfigurationClassName) {
        try {
            final Object config = Class.forName(fullyQualifiedTargetConfigurationClassName).newInstance();
            if (!(config instanceof TargetConfiguration)) {
                throw new IllegalArgumentException("The specified "
                        + TESTBENCH_TARGET_CONFIGURATION + " does not implement TargetConfiguration");
            }

            return ((TargetConfiguration) config);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(
                    "The specified " + TESTBENCH_TARGET_CONFIGURATION + " is not instantiatable");
        }
    }
}
