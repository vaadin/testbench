/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;

/**
 * Interface for classes that have access to a {@link TestBenchCommandExecutor}
 * instance. TestBenchCommandExecutor provides the implementation of TestBench's
 * client-control code.
 *
 */
public interface HasTestBenchCommandExecutor {

    /**
     * Return a reference to the related {@link TestBenchCommandExecutor}
     * instance.
     *
     * @return
     */
    public TestBenchCommandExecutor getCommandExecutor();

}