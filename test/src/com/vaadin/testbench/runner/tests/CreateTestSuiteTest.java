package com.vaadin.testbench.runner.tests;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;

import com.vaadin.testbench.runner.TestBenchRunner;
import com.vaadin.testbench.runner.util.TestBenchSuite;

public class CreateTestSuiteTest {

    // Test creation of one test suite for JUnit execution in ant
    public static Test suite() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();

        List<String> tests = new LinkedList<String>();
        tests.add("Money_tst.java");
        tests.add("Money_tst2.java");

        TestBenchSuite suite = tbr.createTestSuite(tests, "test", "Suite Test");

        // Assert that we got a TestBenchSuite
        Assert.assertTrue("Creating test suite should not return null",
                suite != null);

        return suite.getTestSuite("winxp-firefox35");
    }
}
