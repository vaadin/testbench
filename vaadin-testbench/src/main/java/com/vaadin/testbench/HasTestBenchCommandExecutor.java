/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
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
     * @return
     */
    public TestBenchCommandExecutor getTestBenchCommandExecutor();

}