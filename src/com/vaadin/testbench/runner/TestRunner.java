package com.vaadin.testbench.runner;
//package com.vaadin.testingtools.runner;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.util.List;
//
//import org.apache.commons.io.IOUtils;
//
//import com.vaadin.testingtools.runner.SeleniumHTMLTestCaseParser.Command;
//import com.vaadin.testingtools.testcase.AbstractVaadinTestCase;
//
///**
// * A TestRunner for HTML test cases.
// * 
// * This code is EXPERIMENTAL.
// * 
// */
//public class TestRunner extends AbstractVaadinTestCase {
//
//    private static final String TESTCASE_FILENAME = "testcase";
//
//    public void testHtml() throws Exception {
//        String file = System.getProperty(TESTCASE_FILENAME);
//        if (file == null || file.equals("")) {
//            throw new IllegalArgumentException(
//                    "Missing test case file name. Use -D" + TESTCASE_FILENAME
//                            + "=testcase");
//        }
//
//        File f = new File(file);
//        if (!f.exists()) {
//            throw new FileNotFoundException("Test case " + file + " not found");
//        }
//
//        String testCase = IOUtils.toString(new FileInputStream(f));
//
//        List<Command> commands = SeleniumHTMLTestCaseParser
//                .parseTestCase(testCase);
//        for (Command command : commands) {
//            testBase.getVaadinSelenium().doCommand(command.getCmd(),
//                    command.getParams());
//        }
//
//    }
//}
