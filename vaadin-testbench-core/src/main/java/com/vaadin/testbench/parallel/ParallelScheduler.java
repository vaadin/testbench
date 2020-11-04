/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.runners.model.RunnerScheduler;

/**
 * JUnit scheduler capable of running multiple tets in parallel. Each test is
 * run in its own thread. Uses an {@link ExecutorService} to manage the threads.
 */
public class ParallelScheduler implements RunnerScheduler {
    private final List<Future<Object>> fResults = new ArrayList<>();
    private ExecutorService fService;

    /**
     * Creates a parallel scheduler which will use the given executor service
     * when submitting test jobs.
     *
     * @param service
     *            The service to use for tests
     */
    public ParallelScheduler(ExecutorService service) {
        fService = service;
    }

    @Override
    public void schedule(final Runnable childStatement) {
        fResults.add(fService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                childStatement.run();
                return null;
            }
        }));
    }

    @Override
    public void finished() {
        for (Future<Object> each : fResults) {
            try {
                each.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
