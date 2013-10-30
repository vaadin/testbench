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
     * @return
     */
    public TestBenchCommandExecutor getTestBenchCommandExecutor();

}