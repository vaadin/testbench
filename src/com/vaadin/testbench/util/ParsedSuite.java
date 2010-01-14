package com.vaadin.testbench.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Class for returning parsed test suite file contents.
 */
public class ParsedSuite {

    private String testName;
    private String path;
    private List<String> suiteTests;

    /**
     * Instantiates a new parsed suite.
     */
    public ParsedSuite() {
        testName = path = null;
        suiteTests = new LinkedList<String>();
    }

    /**
     * Get test name.
     * 
     * @return test name
     */
    public String getTestName() {
        return testName;
    }

    /**
     * Set test name.
     * 
     * @param testName
     *            new test name
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * Get path.
     * 
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set path.
     * 
     * @param path
     *            new path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get suite tests.
     * 
     * @return suite tests
     */
    public List<String> getSuiteTests() {
        return suiteTests;
    }

    /**
     * Set suite tests.
     * 
     * @param suiteTests
     *            new suite tests
     */
    public void setSuiteTests(List<String> suiteTests) {
        this.suiteTests = suiteTests;
    }

}
