package com.vaadin.testbench.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.vaadin.testbench.runner.TestBenchRunner;
import com.vaadin.testbench.runner.util.TestBenchSuite;

public class AddTest extends TestCase {

    /**
     * Test creating 1 testSuite with 2 tests.
     * 
     * @throws Exception
     *             the exception
     */
    public void testadd_2_files() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> tests = new LinkedList<String>();
        tests.add("Money_tst.java");
        tests.add("tst3.html");

        tbr.createTestSuite(tests, "test", "add_2_files");
        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(2, tbr.getTestBenchSuites().get(0)
                .getTestsInSuite());
    }

    /**
     * Test creating 1 testSuite with 2 tests using null path
     */
    public void testadd_2_files_null_path() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> tests = new LinkedList<String>();
        tests.add("test/Money_tst.java");
        tests.add("test/tst3.html");

        tbr.createTestSuite(tests, null, "add_2_files_null_path");
        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(2, tbr.getTestBenchSuites().get(0)
                .getTestsInSuite());
    }

    /**
     * Test creating 1 testSuite with 2 tests using relative path to test files
     */
    public void testadd_file_relative_path() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> tests = new LinkedList<String>();
        tests.add("test/Money_tst.java");

        tbr.createTestSuite(tests, ".", "add_2_files_relative");
        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(1, tbr.getTestBenchSuites().get(0)
                .getTestsInSuite());
    }

    /**
     * Test creating 1 testSuite with 2 tests by parsing a testSuite file (.xml)
     * with relative path to test files not in sub directories
     */
    public void testadd_from_xml_file_relative_path() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        TestBenchSuite tbs = tbr.parseTestSuite("testSuiteRelativePath.xml",
                "relative");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(2, tbs.getTestsInSuite());
    }

    /**
     * Test creating 1 testSuite with 2 tests by parsing a testSuite file
     * (.html)
     */
    public void testadd_from_html_file() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        tbr.parseTestSuite("testSuite.html", ".");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(2, tbr.getTestBenchSuites().get(0)
                .getTestsInSuite());
    }

    /**
     * Test creating 1 testSuite with 2 tests by parsing a testSuite file (.xml)
     * with no path defined
     */
    public void testadd_from_xml_file() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        tbr.parseTestSuite("testSuite.xml", "test");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(2, tbr.getTestBenchSuites().get(0)
                .getTestsInSuite());
    }

    // 
    /**
     * Test that having a faulty path return to SuiteFile path and find test
     * files
     */
    public void testadd_from_xml_file_with_faulty_path() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();

        try {
            TestBenchSuite tbs = tbr.parseTestSuite("testSuiteFaultyPath.xml",
                    "test");
            Assert.assertEquals(1, tbr.getTestBenchSuites().size());
            Assert.assertEquals(2, tbs.getTestsInSuite());
        } catch (FileNotFoundException fnfe) {
            Assert
                    .fail("Faulty path definition is supposed to return to Suite path");
        }

    }

    /**
     * Create one test suite from multple files.
     */
    public void testcreate_one_test_suite_from_multple_files() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        TestBenchSuite tbs = tbr.parseFiles(new String[] { "Money_tst.java",
                "testSuite.xml", "Money_tst2.java" }, "test");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(4, tbs.getTestsInSuite());

        String[] tests = tbs.getTests();
        Assert.assertEquals("Money_tst", tests[0]);
        Assert.assertEquals("Money_tst", tests[1]);
        Assert.assertEquals("com.vaadin.automatedtests.tst3", tests[2]);
        Assert.assertEquals("Money_tst2", tests[3]);
    }

    /**
     * Test creating Suites for multiple browsers
     */
    public void testmultiple_browsers() throws Exception {
        Properties original = new Properties(System.getProperties());
        Properties p = new Properties(System.getProperties());
        // Add property com.vaadin.testbench.browsers to System properties
        p.setProperty("com.vaadin.testbench.browsers",
                "winxp-firefox35,winxp-ie7");
        System.setProperties(p);

        TestBenchRunner tbr = new TestBenchRunner();
        TestBenchSuite tbs = tbr.parseTestSuite("testSuite.xml", "test");

        // Check that only one suite has been created for 2 browsers
        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        // Check that suite has TestSuites for 2 browsers
        Assert.assertEquals(2, tbs.getBrowsers().length);
        // Check that suite has 2 tests for each TestSuite
        Assert.assertEquals(2, tbs.getTestsInSuite());

        // Set back starting System properties
        System.setProperties(original);
    }

    /**
     * Test making of test suite + ant script
     */
    public void testmake_test_suite() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> list = new LinkedList<String>();
        list.add("Money_tst.java");

        tbr.makeTestSuiteFiles(list, ".", "Make_test_suite");

        // Confirm that necessary files have been created
        File file = new File("test/build/Make_test_suite/build.xml");
        Assert.assertTrue(file.exists());
        file = new File("test/build/Make_test_suite/Money_tst.java");
        Assert.assertTrue(file.exists());
        file = new File(
                "test/build/Make_test_suite/Make_test_suite_winxp_firefox35_Suite.java");
        Assert.assertTrue(file.exists());
    }

    /**
     * Test making of test suite + ant script for multiple browsers
     */
    public void testmake_test_suite_multiple_browsers() throws Exception {
        Properties original = new Properties(System.getProperties());
        Properties p = new Properties(System.getProperties());
        p.setProperty("com.vaadin.testbench.browsers",
                "winxp-firefox35,winxp-ie7,winxp-safari4");
        System.setProperties(p);

        TestBenchRunner tbr = new TestBenchRunner();
        List<String> list = new LinkedList<String>();
        list.add("Money_tst.java");
        list.add("tst3.html");

        tbr.makeTestSuiteFiles(list, ".", "Make_test_suite_multiple");

        // Confirm that necessary files have been created
        File file = new File("test/build/Make_test_suite_multiple/build.xml");
        Assert.assertTrue(file.exists());
        file = new File("test/build/Make_test_suite_multiple/Money_tst.java");
        Assert.assertTrue(file.exists());
        file = new File(
                "test/build/Make_test_suite_multiple/Make_test_suite_multiple_winxp_firefox35_Suite.java");
        Assert.assertTrue(file.exists());
        file = new File(
                "test/build/Make_test_suite_multiple/Make_test_suite_multiple_winxp_ie7_Suite.java");
        Assert.assertTrue(file.exists());
        file = new File(
                "test/build/Make_test_suite_multiple/Make_test_suite_multiple_winxp_safari4_Suite.java");
        Assert.assertTrue(file.exists());
        file = new File(
                "test/build/Make_test_suite_multiple/com/vaadin/automatedtests/tst3_winxp_firefox35.java");
        Assert.assertTrue(file.exists());
        file = new File(
                "test/build/Make_test_suite_multiple/com/vaadin/automatedtests/tst3_winxp_ie7.java");
        Assert.assertTrue(file.exists());
        file = new File(
                "test/build/Make_test_suite_multiple/com/vaadin/automatedtests/tst3_winxp_safari4.java");
        Assert.assertTrue(file.exists());

        System.setProperties(original);
    }

    /**
     * Test runing main with flags -make and -p with testSuite.xml
     */
    public void testMain_with_xml_Suite() throws Exception {
        TestBenchRunner.main(new String[] { "-make", "-p", "test",
                "testSuite.xml" });
        File file = new File("test/build/MixedTests/build.xml");
        Assert.assertTrue(file.exists());
    }

    /**
     * Test runing main with flags -make and -p with testSuite.html
     */
    public void testMain_with_html_Suite() throws Exception {
        TestBenchRunner.main(new String[] { "-make", "-p", "test",
                "testSuite.html" });
        File file = new File("test/build/testSuite/build.xml");
        Assert.assertTrue(file.exists());
    }
}
