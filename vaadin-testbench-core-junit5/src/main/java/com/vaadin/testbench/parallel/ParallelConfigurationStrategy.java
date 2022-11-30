/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;

import com.vaadin.testbench.Parameters;

/**
 * Custom configuration strategy using TestBench
 * {@link Parameters#getTestsInParallel()}.
 */
public class ParallelConfigurationStrategy implements
        ParallelExecutionConfiguration, ParallelExecutionConfigurationStrategy {

    private static final int MAX_CONCURRENT_TESTS;

    static {
        MAX_CONCURRENT_TESTS = Parameters.getTestsInParallel();
    }

    @Override
    public int getParallelism() {
        return MAX_CONCURRENT_TESTS;
    }

    @Override
    public int getMinimumRunnable() {
        return 0;
    }

    @Override
    public int getMaxPoolSize() {
        return MAX_CONCURRENT_TESTS;
    }

    @Override
    public int getCorePoolSize() {
        return MAX_CONCURRENT_TESTS;
    }

    @Override
    public int getKeepAliveSeconds() {
        return 30;
    }

    @Override
    public ParallelExecutionConfiguration createConfiguration(
            final ConfigurationParameters configurationParameters) {
        return this;
    }
}
