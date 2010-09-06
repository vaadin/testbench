package com.vaadin.testbench.runner.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.vaadin.testbench.Parameters;
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

        TestBenchSuite tbs = tbr.createTestSuite(tests, "test", "add_2_files");
        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(2, tbr.getTestBenchSuite(0).getTestsInSuite());
        Assert.assertEquals(tbs, tbr.getTestBenchSuite(0));
    }

    /**
     * Test creating 1 testSuite with 2 tests using null path
     */
    public void testadd_2_files_null_path() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> tests = new LinkedList<String>();
        tests.add("test/Money_tst.java");
        tests.add("test/tst3.html");

        TestBenchSuite tbs = tbr.createTestSuite(tests, null,
                "add_2_files_null_path");
        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(2, tbr.getTestBenchSuite(0).getTestsInSuite());
        Assert.assertEquals(tbs, tbr.getTestBenchSuite(0));
    }

    /**
     * Test that html test connecting functions.
     */
    public void testtest_connecting() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        tbr.setConnectTests(true);
        String[] tests = new String[] { "tst.html", "tst3.html",
                "Money_tst.java", "tst.html", "Money_tst2.java", "tst3.html",
                "tst.html", "tst3.html" };

        TestBenchSuite tbs = tbr.parseFiles(tests, "test");
        // Assert that the 8 tests become 5 tests
        Assert.assertEquals(5, tbs.getTestsInSuite());
        String[] testList = tbs.getTests();
        // Assert all tests and their order
        Assert.assertEquals("com.vaadin.automatedtests.test", testList[0]
                .substring(0, testList[0].indexOf("_")));
        Assert.assertTrue("Money_tst".equals(testList[1]));
        // Assert that a single file doesn't get combined to a test_{hash}.html
        Assert.assertTrue("com.vaadin.automatedtests.tst".equals(testList[2]));
        Assert.assertTrue("Money_tst2".equals(testList[3]));
        Assert.assertEquals("com.vaadin.automatedtests.test", testList[4]
                .substring(0, testList[0].indexOf("_")));
    }

    /**
     * Test creating 1 testSuite with 2 tests using relative path to test files
     */
    public void testadd_file_relative_path() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> tests = new LinkedList<String>();
        tests.add("test/Money_tst.java");

        TestBenchSuite tbs = tbr.createTestSuite(tests, ".",
                "add_1_file_relative");
        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(1, tbr.getTestBenchSuite(0).getTestsInSuite());
        Assert.assertEquals(tbs, tbr.getTestBenchSuite(0));
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

    public void testadd_from_relative_xml_file_relative_path() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        TestBenchSuite tbs = tbr.parseTestSuite(
                "relative/testSuiteRelativePath.xml", ".");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(2, tbs.getTestsInSuite());
    }

    /**
     * Test creating 1 testSuite with 2 tests by parsing a testSuite file
     * (.html)
     */
    public void testadd_from_html_file() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        TestBenchSuite tbs = tbr.parseTestSuite("testSuite.html", "test");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(1, tbr.getTestBenchSuite(0).getTestsInSuite());
        Assert.assertEquals(tbs, tbr.getTestBenchSuite(0));
    }

    /**
     * Test creating 1 testSuite with 2 tests by parsing a testSuite file
     * (.html)
     */
    public void testadd_from_html_file_with_undefined_title() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        TestBenchSuite tbs = tbr.parseTestSuite("undefinedSuiteName.html",
                "test");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(1, tbr.getTestBenchSuite(0).getTestsInSuite());
        Assert.assertEquals(tbs, tbr.getTestBenchSuite(0));
    }

    /**
     * Test creating 1 testSuite with 2 tests by parsing a testSuite file
     * (.html) with connectTests = true
     */
    public void testadd_from_html_file_with_connect_flag() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        tbr.setConnectTests(true);
        TestBenchSuite tbs = tbr.parseTestSuite("testSuite.html", "test");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(1, tbr.getTestBenchSuite(0).getTestsInSuite());
        Assert.assertEquals(tbs, tbr.getTestBenchSuite(0));
    }

    /**
     * Test creating tests by parsing a testSuite file (.html) with .java files
     * in it (only supports .html files)
     */
    public void testadd_from_faulty_html_file() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        try {
            tbr.parseTestSuite("testSuiteFaulty.html", "test");
            Assert.fail("Parser accepted .html test suite with .java files.");
        } catch (UnsupportedOperationException e) {
            Assert.assertTrue(tbr.getTestBenchSuites().isEmpty());
        }
    }

    /**
     * Test giving a testSuite file (.html) to createTestSuite that only
     * supports test files (.java, .html)
     */
    public void testsend_html_suite_to_create_test_suite() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> tests = new LinkedList<String>();
        tests.add("testSuite.html");
        try {
            tbr.createTestSuite(tests, "test", "Wrong test files");
            Assert.fail("Suite file not recognized.");
        } catch (UnsupportedOperationException e) {
            Assert.assertTrue(tbr.getTestBenchSuites().isEmpty());
        }
    }

    public void testsend_html_suite_with_path() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> tests = new LinkedList<String>();
        tests.add("test/testSuite.html");

        tbr.parseFiles(tests, ".");
    }

    /**
     * Test giving a testSuite file (.xml) to createTestSuite that only supports
     * test files (.java, .html)
     */
    public void testsend_XML_suite_to_create_test_suite() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> tests = new LinkedList<String>();
        tests.add("testSuite.xml");
        try {
            tbr.createTestSuite(tests, "test", "Wrong test files");
            Assert.fail("Suite file not recognized.");
        } catch (UnsupportedOperationException e) {
            Assert.assertTrue(tbr.getTestBenchSuites().isEmpty());
        }
    }

    /**
     * Test creating 1 testSuite with 2 tests by parsing a testSuite file (.xml)
     * with no path defined
     */
    public void testadd_from_xml_file() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        TestBenchSuite tbs = tbr.parseTestSuite("testSuite.xml", "test");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(2, tbr.getTestBenchSuite(0).getTestsInSuite());
        Assert.assertEquals(tbs, tbr.getTestBenchSuite(0));
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
                "testSuite.xml", "tst.html", "Money_tst2.java" }, "test");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(5, tbs.getTestsInSuite());

        String[] tests = tbs.getTests();
        Assert.assertEquals("Money_tst", tests[0]);
        Assert.assertEquals("Money_tst", tests[1]);
        Assert.assertEquals("com.vaadin.automatedtests.tst3", tests[2]);
        Assert.assertEquals("com.vaadin.automatedtests.tst", tests[3]);
        Assert.assertEquals("Money_tst2", tests[4]);
    }

    /**
     * Create one test suite from multple files.
     */
    public void testcreate_one_test_suite_from_multple_files_html()
            throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();

        TestBenchSuite tbs = tbr.parseFiles(new String[] { "Money_tst.java",
                "testSuite.html", "tst.html", "Money_tst2.java" }, "test");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(4, tbs.getTestsInSuite());

        String[] tests = tbs.getTests();
        Assert.assertEquals("Money_tst", tests[0]);
        Assert.assertEquals("com.vaadin.automatedtests.Money_Sampler_",
                tests[1].substring(0, tests[1].lastIndexOf("_")));
        Assert.assertEquals("com.vaadin.automatedtests.tst", tests[2]);
        Assert.assertEquals("Money_tst2", tests[3]);
    }

    /**
     * Test creating 1 testSuite with 2 tests by parsing a testSuite file
     * (.html)
     */
    public void testparseFiles_with_list_of_files() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        List<String> tests = new LinkedList<String>();
        tests.add("Money_tst.java");
        tests.add("testSuite.html");
        tests.add("tst.html");

        TestBenchSuite tbs = tbr.parseFiles(tests, "test");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(3, tbr.getTestBenchSuite(0).getTestsInSuite());
        Assert.assertEquals(tbs, tbr.getTestBenchSuite(0));

        String[] tbsTests = tbs.getTests();

        Assert.assertEquals("Money_tst", tbsTests[0]);
        Assert.assertEquals("com.vaadin.automatedtests.Money_Sampler_",
                tbsTests[1].substring(0, tbsTests[1].lastIndexOf("_")));
        Assert.assertEquals("com.vaadin.automatedtests.tst", tbsTests[2]);

    }

    /**
     * Create one test suite from multple files.
     */
    public void testone_test_suite_from_multple_files_html_and_xml()
            throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        TestBenchSuite tbs = tbr.parseFiles(new String[] { "Money_tst.java",
                "testSuite.html", "tst.html", "testSuite.xml",
                "Money_tst2.java" }, "test");

        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        Assert.assertEquals(6, tbs.getTestsInSuite());

        String[] tests = tbs.getTests();
        Assert.assertEquals("Money_tst", tests[0]);
        Assert.assertEquals("com.vaadin.automatedtests.Money_Sampler_",
                tests[1].substring(0, tests[1].lastIndexOf("_")));
        Assert.assertEquals("com.vaadin.automatedtests.tst", tests[2]);
        Assert.assertEquals("Money_tst", tests[3]);
        Assert.assertEquals("com.vaadin.automatedtests.tst3", tests[4]);
        Assert.assertEquals("Money_tst2", tests[5]);
    }

    /**
     * Test creating 1 testSuite with 2 tests by parsing a testSuite file
     * (.html)
     */
    public void testget_suite_for_one_browser() throws Exception {
        TestBenchRunner tbr = new TestBenchRunner();
        TestBenchSuite tbs = tbr.parseTestSuite("test/testSuite.html", ".");

        Assert.assertEquals(tbs.getTestSuite("winxp-firefox35"), tbs
                .getTestSuite());
    }

    /**
     * Test creating Suites for multiple browsers
     */
    public void testmultiple_browsers() throws Exception {
        Properties original = new Properties(System.getProperties());
        Properties p = new Properties(System.getProperties());
        // Add property com.vaadin.testbench.browsers to System properties
        p.setProperty(Parameters.BROWSER_STRING, "winxp-firefox35,winxp-ie7");
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
     * Test creating Suites for multiple browsers
     */
    public void testmultiple_browsers_using_combined() throws Exception {
        Properties original = new Properties(System.getProperties());
        Properties p = new Properties(System.getProperties());
        // Add property com.vaadin.testbench.browsers to System properties
        p.setProperty(Parameters.BROWSER_STRING, "winxp-firefox35,winxp-ie7");
        System.setProperties(p);

        TestBenchRunner tbr = new TestBenchRunner();
        TestBenchSuite tbs = tbr.parseTestSuite("testSuite.xml", "test");

        // Check that only one suite has been created for 2 browsers
        Assert.assertEquals(1, tbr.getTestBenchSuites().size());
        // Check that suite has TestSuites for 2 browsers
        Assert.assertEquals(2, tbs.getBrowsers().length);
        // Check that suite has 2 tests for each TestSuite
        Assert.assertEquals(2, tbs.getTestsInSuite());

        TestSuite suite = tbr.getCombinedSuite(tbs);
        List<TestSuite> tbrss = tbs.getSuites();
        Enumeration<junit.framework.Test> e = suite.tests();

        // Assert that tests are in the correct order
        for (TestSuite testSuite : tbrss) {
            Enumeration<junit.framework.Test> tests = testSuite.tests();
            while (tests.hasMoreElements()) {
                Assert.assertEquals(tests.nextElement().toString(), e
                        .nextElement().toString());
            }
        }
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

        tbr.makeTestSuiteFiles(list, "test", "Make_test_suite");

        // Confirm that necessary files have been created
        File file = new File(System.getProperty("user.dir") + File.separator
                + "test/build/Make_test_suite/build.xml");
        Assert.assertTrue(file.exists());
        file = new File(System.getProperty("user.dir") + File.separator
                + "test/build/Make_test_suite/Money_tst.java");
        Assert.assertTrue(file.exists());
        file = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "test/build/Make_test_suite/Make_test_suite_winxp_firefox35_Suite.java");
        Assert.assertTrue(file.exists());
    }

    /**
     * Test making of test suite + ant script for multiple browsers
     */
    public void testmake_test_suite_multiple_browsers() throws Exception {
        Properties original = new Properties(System.getProperties());
        Properties p = new Properties(System.getProperties());
        p.setProperty(Parameters.BROWSER_STRING,
                "winxp-firefox35,winxp-ie7,winxp-safari4");
        System.setProperties(p);

        TestBenchRunner tbr = new TestBenchRunner();
        List<String> list = new LinkedList<String>();
        list.add("Money_tst.java");
        list.add("tst3.html");

        tbr.makeTestSuiteFiles(list, "test", "Make_test_suite_multiple");

        // Confirm that necessary files have been created
        File file = new File(System.getProperty("user.dir") + File.separator
                + "test/build/Make_test_suite_multiple/build.xml");
        Assert.assertTrue(file.exists());
        file = new File(System.getProperty("user.dir") + File.separator
                + "test/build/Make_test_suite_multiple/Money_tst.java");
        Assert.assertTrue(file.exists());
        file = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "test/build/Make_test_suite_multiple/Make_test_suite_multiple_winxp_firefox35_Suite.java");
        Assert.assertTrue(file.exists());
        file = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "test/build/Make_test_suite_multiple/Make_test_suite_multiple_winxp_ie7_Suite.java");
        Assert.assertTrue(file.exists());
        file = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "test/build/Make_test_suite_multiple/Make_test_suite_multiple_winxp_safari4_Suite.java");
        Assert.assertTrue(file.exists());
        file = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "test/build/Make_test_suite_multiple/com/vaadin/automatedtests/tst3_winxp_firefox35.java");
        Assert.assertTrue(file.exists());
        file = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "test/build/Make_test_suite_multiple/com/vaadin/automatedtests/tst3_winxp_ie7.java");
        Assert.assertTrue(file.exists());
        file = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "test/build/Make_test_suite_multiple/com/vaadin/automatedtests/tst3_winxp_safari4.java");
        Assert.assertTrue(file.exists());

        System.setProperties(original);
    }

    /**
     * Test runing main with flags -make and -p with testSuite.xml
     */
    public void testMain_with_xml_Suite() throws Exception {
        TestBenchRunner.main(new String[] { "-make", "-p", "test",
                "testSuite.xml" });
        File file = new File(System.getProperty("user.dir") + File.separator
                + "test/build/MixedTests/build.xml");
        Assert.assertTrue(file.exists());
    }

    /**
     * Test runing main with flags -make and -p with testSuite.html
     */
    public void testMain_with_html_Suite() throws Exception {
        TestBenchRunner.main(new String[] { "-make", "-p", "test",
                "testSuite.html" });
        File file = new File(System.getProperty("user.dir") + File.separator
                + "test/build/Money_Sampler/build.xml");
        Assert.assertTrue(file.exists());
    }
}
