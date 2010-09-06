package com.vaadin.testbench.runner.tests;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.vaadin.testbench.runner.TestBenchRunner;
import com.vaadin.testbench.runner.util.TestBenchSuite;

public class ConnectingSuitesTest {

    // Create 2 TestSuites and put them into one TestSuite for
    // JUnit execution in ant. Assert order of tests.
    public static Test suite() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> tests = new LinkedList<String>();
        tests.add("Money_tst.java");
        tests.add("Money_tst2.java");

        // Create 2 test Suites. Second suite is the first suite in reverse
        // order.
        tbr.createTestSuite(tests, "test", "Suite Test");
        Collections.reverse(tests);
        tbr.createTestSuite(tests, "test", "Reverse Suite Test");

        // Create empty TestSuite
        TestSuite created = new TestSuite();

        // Get test suite for first Suite and browser winxp-firefox35
        TestSuite test = tbr.getTestBenchSuites().get(0).getTestSuite(
                "winxp-firefox35");

        // Add test from first suite to Empty TestSuite created
        Enumeration<Test> e = test.tests();
        while (e.hasMoreElements()) {
            created.addTest(e.nextElement());
        }

        // Get test suite for second Suite and browser winxp-firefox35
        test = tbr.getTestBenchSuites().get(1).getTestSuite("winxp-firefox35");

        // Append tests from second suite to TestSuite created
        e = test.tests();
        while (e.hasMoreElements()) {
            created.addTest(e.nextElement());
        }

        // Get TestBenchSuites and tests for TestSuite created
        List<TestBenchSuite> tbrss = tbr.getTestBenchSuites();
        e = created.tests();

        // Assert that tests are in the correct order
        for (TestBenchSuite testBenchSuite : tbrss) {
            for (String tst : testBenchSuite.getTests()) {
                Assert.assertEquals(tst, e.nextElement().toString());
            }
        }

        // Assert that there are no extra tests in TestSuite created
        Assert.assertEquals(false, e.hasMoreElements());

        // Return test suite to be run by ant.
        return created;
    }
}
