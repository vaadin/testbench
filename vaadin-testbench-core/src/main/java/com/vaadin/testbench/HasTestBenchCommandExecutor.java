/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;

/**
 * Interface for classes that have access to a {@link TestBenchCommandExecutor}
 * instance. TestBenchCommandExecutor provides the implementation of TestBench's
 * client-control code.
 * 
 */
public interface HasTestBenchCommandExecutor extends HasSearchContext {

    /**
     * Return a reference to the related {@link TestBenchCommandExecutor}
     * instance.
     *
     * @return executor
     */
    public TestBenchCommandExecutor getCommandExecutor();

}