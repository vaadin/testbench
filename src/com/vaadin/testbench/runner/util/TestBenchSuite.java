package com.vaadin.testbench.runner.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * TestBenchSuite holds TestSuites of a specific kind for multiple browsers.
 * TestBenchSuite also holds TestResults for all browsers for the latest run.
 * Adding a new Browser-TestSuite to
 */
public class TestBenchSuite {

    /** The test suites. */
    private Map<String, TestSuite> testSuites;

    /** The test results. */
    private Map<String, TestResult> testResults;

    /** The suite tests. */
    private String[] suiteTests;

    /** The tests in suite. */
    private int testsInSuite;

    /** The suite name. */
    private String suiteName;

    /**
     * Instantiates a new test bench suite.
     */
    public TestBenchSuite() {
        testSuites = new HashMap<String, TestSuite>();
        testResults = new HashMap<String, TestResult>();
        testsInSuite = 0;
        suiteName = "";
    }

    /**
     * Add test suite.
     * 
     * @param browser
     *            Target browser
     * @param suite
     *            TestSuite
     * 
     * @return true, if successful
     */
    public boolean addTestSuite(String browser, TestSuite suite) {
        // If no tests in suite initialize all variables and add test suite
        if (testsInSuite == 0) {
            testsInSuite = suite.testCount();
            setTests(suite, browser);
            suiteName = suite.getName();
            testSuites.put(browser, suite);
        } else if (suite.testCount() == testsInSuite && browser.length() > 0) {
            // Else check that suite has correct amount of tests and a browser
            // is defined.

            Enumeration<Test> tests = suite.tests();
            // Check order of all tests in new test suite
            for (int i = 0; i < testsInSuite; i++) {
                String test = tests.nextElement().toString();
                // Remove browser name from test name so that names for tests
                // are the same for all browsers
                if (test.toString().contains(
                        browser.replaceAll("[^a-zA-Z0-9]", "_"))) {
                    test = test.substring(0, test.indexOf(browser.replaceAll(
                            "[^a-zA-Z0-9]", "_")) - 1);
                }
                // check that this test equals test in same position for
                // this TestBenchSuite
                if (!suiteTests[i].equalsIgnoreCase(test.toString())) {
                    System.err.println("Expected " + suiteTests[i]
                            + " and got " + test.toString());
                    System.err.println("Not adding test suite for " + browser);
                    return false;
                }
            }
            // Add new browser-suite to TestBenchSuite
            testSuites.put(browser, suite);
        } else {
            // Else print out error information and skip adding of suite.
            if (browser.length() > 0) {
                System.err
                        .println("Suite seems to be missing tests and will not be added.");
                System.err.println("Required tests consisted of:");
                printTestsToErr();
                System.err.println("Found " + suite.testCount() + " tests:");
                printTestsToErr(suite);
                System.err.println("Suite not added.");
            } else {
                System.err.println("No browser was defined for suite.");
                System.err.println("Suite not added.");
            }
            return false;
        }

        return true;
    }

    /**
     * Gets the suites.
     * 
     * @return the suites
     */
    public List<TestSuite> getSuites() {
        List<TestSuite> suites = new LinkedList<TestSuite>();
        for (String key : testSuites.keySet()) {
            suites.add(testSuites.get(key));
        }
        return suites;
    }

    /**
     * Set result for test run.
     * 
     * @param browser
     *            the browser
     * @param result
     *            the result
     * 
     * @return true, if successful
     */
    public boolean setResult(String browser, TestResult result) {
        testResults.put(browser, result);

        return true;
    }

    /**
     * Gets the test suite.
     * 
     * @param browser
     *            the browser
     * 
     * @return the test suite
     */
    public TestSuite getTestSuite(String browser) {
        if (testSuites.get(browser) == null) {
            System.err.println("No suite available for browser " + browser);
        }
        return testSuites.get(browser);
    }

    /**
     * Gets the result for browser.
     * 
     * @param browser
     *            the browser
     * 
     * @return the result
     */
    public TestResult getResult(String browser) {
        return testResults.get(browser);
    }

    /**
     * Gets the result for all browser.
     * 
     * @param browser
     *            the browser
     * 
     * @return the result
     */
    public Map<String, TestResult> getResults() {
        return testResults;
    }

    /**
     * Prints the result.
     * 
     * @param browser
     *            the browser
     */
    public void printResult(String browser) {
        TestResult result = testResults.get(browser);
        if (result != null) {
            IOFunctions.printResult(0, result);
        } else {
            System.out.println("No results for " + browser);
        }
    }

    /**
     * Gets the suite name.
     * 
     * @return the suite name
     */
    public String getSuiteName() {
        return suiteName;
    }

    /**
     * Sets the suite name.
     */
    public void setSuiteName(String name) {
        suiteName = name;
    }

    /**
     * Prints the suite for browser.
     * 
     * @param browser
     *            the browser
     */
    public void printSuiteForBrowser(String browser) {
        TestSuite suite = testSuites.get(browser);
        if (suite == null) {
            System.err.println("No suite for " + browser);
            return;
        }

        Enumeration<Test> e = suite.tests();
        while (e.hasMoreElements()) {
            System.out.println(e.nextElement().toString());
        }
    }

    public int getTestsInSuite() {
        return testsInSuite;
    }

    /**
     * Get tests for this suite.
     * 
     * @return String[] suite tests
     */
    public String[] getTests() {
        return suiteTests;
    }

    /**
     * Prints the tests in this Suite.
     */
    public void printTests() {
        if (suiteTests == null) {
            return;
        }

        System.out.println("Tests for " + suiteName);
        for (String test : suiteTests) {
            System.out.println(test);
        }
    }

    /**
     * Prints the browsers for which testSuites are available.
     */
    public void printBrowsers() {
        for (String key : testSuites.keySet()) {
            System.out.println(key);
        }
    }

    /**
     * Get tests for all browsers for this Suite
     * 
     * @return String[] of all keys
     */
    public String[] getBrowsers() {
        String[] keys = new String[testSuites.size()];

        int i = 0;

        for (String key : testSuites.keySet()) {
            keys[i++] = key;
        }

        return keys;
    }

    /**
     * Sets the tests.
     * 
     * @param suite
     *            TestSuite to take tests from
     * @param browser
     *            Browser name
     */
    private void setTests(TestSuite suite, String browser) {
        suiteTests = new String[suite.testCount()];
        Enumeration<Test> e = suite.tests();
        int i = 0;
        while (e.hasMoreElements()) {
            String test = e.nextElement().toString();
            // Remove browser name from test
            if (test.toString().contains(
                    browser.replaceAll("[^a-zA-Z0-9]", "_"))) {
                test = test.substring(0, test.indexOf(browser.replaceAll(
                        "[^a-zA-Z0-9]", "_")) - 1);
            }
            suiteTests[i++] = test;
        }
    }

    /**
     * Prints out the tests for this suite in order.
     */
    private void printTestsToErr() {
        if (suiteTests == null) {
            return;
        }

        for (String test : suiteTests) {
            System.err.println("\t" + test);
        }
    }

    /**
     * Prints out the tests in suite.
     * 
     * @param suite
     *            TestSuite for which to print out tests
     */
    private void printTestsToErr(TestSuite suite) {
        Enumeration<Test> e = suite.tests();
        while (e.hasMoreElements()) {
            System.err.println("\t" + e.nextElement().toString());
        }
    }
}
