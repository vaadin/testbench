/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.parameters;

import java.util.function.Supplier;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

/**
 * <p>
 * Implementation of {@link ParameterResolver} that allows to access
 * {@link Capabilities} and {@link WebDriver} used in currently running test
 * instance.
 * </p>
 *
 * <p>
 * Usually used together with
 * {@link com.vaadin.testbench.capabilities.CapabilitiesExtension}.
 * </p>
 */
public class TestBenchTestInfoParameterResolver implements ParameterResolver {

    private final DefaultTestBenchTestInfo testBenchTestInfo;

    public TestBenchTestInfoParameterResolver(
            Supplier<Capabilities> capabilitiesSupplier,
            Supplier<WebDriver> webDriverSupplier) {
        testBenchTestInfo = new DefaultTestBenchTestInfo(capabilitiesSupplier,
                webDriverSupplier);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return (parameterContext.getParameter()
                .getType() == TestBenchTestInfo.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return testBenchTestInfo;
    }

    private static class DefaultTestBenchTestInfo implements TestBenchTestInfo {

        private final Supplier<Capabilities> capabilitiesSupplier;

        private final Supplier<WebDriver> webDriverSupplier;

        public DefaultTestBenchTestInfo(
                Supplier<Capabilities> capabilitiesSupplier,
                Supplier<WebDriver> webDriverSupplier) {
            this.capabilitiesSupplier = capabilitiesSupplier;
            this.webDriverSupplier = webDriverSupplier;
        }

        @Override
        public Capabilities getCapabilities() {
            return capabilitiesSupplier.get();
        }

        @Override
        public WebDriver getDriver() {
            return webDriverSupplier.get();
        }
    }

}
