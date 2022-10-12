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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.annotations.RunLocally;

/**
 * Utility class used for running parallel tests.
 */
public class ParallelRunner {

    private static Logger getLogger() {
        return LoggerFactory.getLogger(ParallelRunner.class);
    }

    /**
     * This is the total limit of actual JUnit test instances run in parallel
     */
    private static final int MAX_CONCURRENT_TESTS;

    /**
     * This is static so it is shared by all tests running concurrently on the
     * same machine and thus can limit the number of threads in use.
     */
    private static final ExecutorService service;

    static {
        MAX_CONCURRENT_TESTS = Parameters.getTestsInParallel();
        service = Executors.newFixedThreadPool(MAX_CONCURRENT_TESTS);
    }

    private static boolean explicitInclusionIsUsed() {
        String include = System.getProperty("categories.include");

        return include != null && include.trim().length() > 0;
    }

    private static boolean categoryIsExcluded(Class<?> c) {
        String exclude = System.getProperty("categories.exclude");
        if (exclude != null && exclude.trim().length() > 0) {
            return hasCategoryFor(c, exclude.toLowerCase().trim());
        }

        return false;
    }

    private static boolean hasCategoryFor(Class<?> c, String searchString) {
        if (hasCategory(c)) {
            return searchString.contains(getCategory(c).toLowerCase());
        }

        return false;
    }

    private static boolean hasCategory(Class<?> c) {
        return c.getAnnotation(TestCategory.class) != null;
    }

    private static String getCategory(Class<?> c) {
        return c.getAnnotation(TestCategory.class).value();
    }

    static Browser getRunLocallyBrowserName(Class<?> testClass) {

        String runLocallyBrowserName = Parameters.getRunLocallyBrowserName();
        if (runLocallyBrowserName != null) {
            return Browser.valueOf(runLocallyBrowserName.toUpperCase());
        }
        RunLocally runLocally = testClass.getAnnotation(RunLocally.class);
        if (runLocally == null) {
            return null;
        }
        return runLocally.value();
    }

    static String getRunLocallyBrowserVersion(Class<?> testClass) {
        String runLocallyBrowserVersion = Parameters
                .getRunLocallyBrowserVersion();
        if (runLocallyBrowserVersion != null) {
            return runLocallyBrowserVersion;
        }

        RunLocally runLocally = testClass.getAnnotation(RunLocally.class);
        if (runLocally == null) {
            return "";
        }
        return runLocally.version();
    }

}
