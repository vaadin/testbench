package com.vaadin.testbench.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.vaadin.testbench.runner.util.IOFunctions;
import com.vaadin.testbench.runner.util.ParsedSuite;
import com.vaadin.testbench.runner.util.ParserFunctions;
import com.vaadin.testbench.runner.util.TestBenchSuite;

/**
 * TestBenchRunner.
 */
public class TestBenchRunner {

    private static final String PACKAGE_DIR = "com/vaadin/automatedtests";

    private com.sun.tools.javac.Main javac;
    private TestResult result;
    private String[] browsers;
    String encoding;

    /** Flags */
    private boolean makeTests = false;
    private boolean connectTests = false;

    /** The test bench suites. */
    private List<TestBenchSuite> testBenchSuites;

    /**
     * Instantiates a new test bench runner.
     */
    public TestBenchRunner() {
        testBenchSuites = new LinkedList<TestBenchSuite>();

        javac = new com.sun.tools.javac.Main();
        browsers = new String[] { "winxp-firefox35" };
        encoding = "utf8";

        if (System.getProperty("com.vaadin.testbench.encoding") != null) {
            encoding = System.getProperty("com.vaadin.testbench.encoding");
        }
    }

    /**
     * Main method.
     * 
     * @param args
     *            [-options] (test files)
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            printHelp();
            System.exit(1);
        }

        // Create new TestBenchRunner
        TestBenchRunner tbr = new TestBenchRunner();

        String[] tests = null;
        String path = null;

        // Check given arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-path") || args[i].equals("-p")) {
                path = args[i + 1];
                i++;
            } else if (args[i].equals("-make") || args[i].equals("-m")) {
                tbr.setMakeTests(true);
            } else if (args[i].equals("-help") || args[i].equals("-h")) {
                printHelp();
                System.exit(0);
            } else if (args[i].equals("-connect") || args[i].equals("-c")) {
                tbr.setConnectTests(true);
            } else {
                tests = args[i].split(",");
            }
        }

        if (tests == null || tests.length == 0) {
            System.out.println("No tests found in arguments.");
            System.exit(1);
        }

        tbr.parseFiles(tests, path);
        // If not makeTests == true run tests
        if (!tbr.makeTests) {
            tbr.runTestSuites();
        }
    }

    /**
     * Print main commands/usage to system.err
     */
    private static void printHelp() {
        System.err.println("Usage: " + TestBenchRunner.class.getName()
                + " [-options] <test files>");
        System.err
                .println("\t<test files>\t - ',' separated files, note files will create one test suite\n");
        System.err.println("Options include:\t\t");
        System.err.println("\t-path [-p]\tBase path for files");
        System.err
                .println("\t-make [-m]\tCreates test suite java and build.xml");
        System.err
                .println("\t-connect [-c]\tConnect html tests that come after one another.");
        System.err.println("\t-help [-h]\tThis help");
    }

    /**
     * Define if a test suite should be made.
     * 
     * @param value
     *            boolean true/false
     */
    public void setMakeTests(boolean value) {
        makeTests = value;
    }

    /**
     * Define if html files after each other should be connected.
     * 
     * @param value
     *            boolean true/false
     */
    public void setConnectTests(boolean value) {
        connectTests = value;
    }

    /**
     * Sets the source file encoding to use during compilation
     * 
     * @param encode
     */
    public void setEncoding(String encode) {
        encoding = encode;
    }

    /**
     * Create test suite from given list. Creates one suite per browser. If
     * com.vaadin.testbench.browsers is not defined winxp-firefox35 is used.
     * 
     * @param tests
     *            List of test file names (.java, .html)
     * @param path
     *            Basepath for tests in list
     * @param testName
     *            Name for test suite
     * 
     * @return created TestBenchSuite
     */
    @SuppressWarnings( { "unchecked", "static-access" })
    public TestBenchSuite createTestSuite(List<String> tests, String path,
            String testName) throws Exception {
        // Get defined browsers if any.
        if (System.getProperty("com.vaadin.testbench.browsers") != null) {
            browsers = System.getProperty("com.vaadin.testbench.browsers")
                    .split(",");
        }

        path = normalizePath(path);

        // define build as ${path}/build and change if
        // com.vaadin.testbench.build defined
        String build = path + "build";
        if (System.getProperty("com.vaadin.testbench.build") != null) {
            build = normalizeBuild(System
                    .getProperty("com.vaadin.testbench.build"));
        }

        // Set build path
        File dir = new File(build);
        // Create build/ if not exist
        if (!dir.exists()) {
            dir.mkdir();
        }

        // Create TestBenchSuite for these tests and browsers
        TestBenchSuite tbs = new TestBenchSuite();
        // Create a TestSuite for each browser
        for (int j = 0; j < browsers.length; j++) {
            List<Class> classes = new LinkedList<Class>();

            // Iterate through all given tests
            for (String test : tests) {
                // Get file
                File file = new File(test);
                if (!file.exists()) {
                    file = new File(path + test);
                    if (!file.exists()) {
                        System.out.println("Searching for file " + test
                                + " for compilation.");
                        file = IOFunctions.getFile(test, file.getParentFile(),
                                0);
                    }
                }
                if (file == null) {
                    throw new FileNotFoundException("Couldn't locate file "
                            + test);
                }

                String fileType = "";
                String classname = "";
                try {
                    // Get file type of test (.html, .java)
                    fileType = file.getName().substring(
                            file.getName().lastIndexOf('.') + 1,
                            file.getName().length());
                    // Get class name for test
                    classname = file.getName().substring(0,
                            file.getName().lastIndexOf('.'));
                } catch (IndexOutOfBoundsException ioobe) {
                    System.err.println("Could not get file type for file "
                            + file.getName());
                    throw new UnsupportedOperationException(
                            "File without file type is not supported.");
                }

                if ("java".equals(fileType)) {
                    // Set compilation options
                    String[] options = new String[] {
                            "-encoding",
                            encoding,
                            "-cp",
                            System.getProperty("java.class.path", ".")
                                    + File.pathSeparator
                                    + file.getParentFile().getAbsolutePath(),
                            "-d", dir.getAbsolutePath(), file.getAbsolutePath() };
                    // Compile
                    int status = javac.compile(options);

                    // If compilation successful, load class dynamically and
                    // add to testSuite
                    if (status == 0) {
                        // Get path to class file
                        File classFile = IOFunctions.getFile(classname
                                + ".class", dir, 0);
                        if (classFile != null) {
                            classes.add(loadClassDynamically(dir, classFile,
                                    classname));
                        }
                    } else {
                        throw new RuntimeException(
                                "Compilation failed with status " + status);
                    }
                } else if ("html".equals(fileType)) {
                    // Check that html file is a test and not a suite
                    BufferedReader in = new BufferedReader(new FileReader(file));
                    try {
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            if (line.contains("<thead>")) {
                                break;
                            } else if (line.contains("a href=")) {
                                System.err
                                        .println("For html suites use parseTestSuite(String, String) or parseFiles(String[], String)");
                                throw new UnsupportedOperationException(
                                        "Html suite file is not supported in this method.");
                            }
                        }
                    } finally {
                        in.close();
                    }

                    File temp = new File(System.getProperty("java.io.tmpdir"));
                    if (System.getProperty("com.vaadin.testbench.temp") != null) {
                        build = System.getProperty("com.vaadin.testbench.temp");
                    }

                    com.vaadin.testbench.util.TestConverter
                            .runnerConvert(new String[] {
                                    temp.getAbsolutePath(), browsers[j],
                                    file.getAbsolutePath() });
                    // create files for each browser and add browser to name
                    classname = (classname + "_" + browsers[j]).replaceAll(
                            "[^a-zA-Z0-9]", "_");

                    file = IOFunctions.getFile(classname + ".java", new File(
                            temp.getAbsolutePath() + file.separator
                                    + PACKAGE_DIR), 0);

                    String[] options = new String[] { "-encoding", encoding,
                            "-cp", System.getProperty("java.class.path", "."),
                            "-d", dir.getAbsolutePath(), file.getAbsolutePath() };
                    int status = javac.compile(options);

                    // If compilation successful, load class dynamically and
                    // add to testSuite
                    if (status == 0) {
                        File classFile = IOFunctions.getFile(classname
                                + ".class", new File(dir + File.separator
                                + PACKAGE_DIR), 0);
                        if (classFile != null) {
                            classes.add(loadClassDynamically(dir, classFile,
                                    classname));
                        }
                    } else {
                        throw new RuntimeException(
                                "Compilation failed with status " + status);
                    }
                    temp = null;
                } else if ("xml".equals(fileType)) {
                    System.err
                            .println("For XML suites use parseTestSuite(String, String) or parseFiles(String[], String)");
                    throw new UnsupportedOperationException(
                            "XML suite file is not supported in this method.");
                }

            }

            Class[] testClasses = new Class[classes.size()];
            for (int i = 0; i < classes.size(); i++) {
                testClasses[i] = classes.get(i);
                classes.get(i).getName();
            }
            TestSuite suite = new TestSuite(testClasses);
            suite.setName(testName);

            // Test TestBenchSuite
            tbs.addTestSuite(browsers[j], suite);

        }
        testBenchSuites.add(tbs);
        return tbs;
    }

    /**
     * Load class dynamically.
     * 
     * @param dir
     *            Build directory
     * @param classFile
     *            target class file
     * @param classname
     *            classname
     * 
     * @return Class
     * 
     * @throws Exception
     *             ClassNotFoundException - If the class was not found
     */
    @SuppressWarnings("unchecked")
    private Class loadClassDynamically(File dir, File classFile,
            String classname) throws Exception {
        // Create custom class loader
        ClassLoader loader = new URLClassLoader(new URL[] { dir.toURL() });
        try {
            // Get file package if any
            String pkg = classFile.getParent().substring(
                    dir.getPath().length() + 1, classFile.getParent().length());
            pkg = pkg.replace("\\", ".").replace("/", ".");
            Class c = loader.loadClass(pkg + "." + classname);
            return c;
        } catch (IndexOutOfBoundsException e) {
            // If caught index out of bounds no package is
            // defined for file
            Class c = loader.loadClass(classname);
            return c;
        }
    }

    /**
     * Copy test files to path/testName and creates a Suite.java (for each
     * browser) and build.xml
     * 
     * @param tests
     *            List of test files (.java, .html)
     * @param path
     *            Base path to use (for files and where to create /testName/)
     * @param testName
     *            Name of testSuite
     */
    public void makeTestSuiteFiles(List<String> tests, String path,
            String testName) throws Exception {
        if (System.getProperty("com.vaadin.testbench.browsers") != null) {
            browsers = System.getProperty("com.vaadin.testbench.browsers")
                    .split(",");
        }

        path = normalizePath(path);

        String build = path
                + testName.replaceAll("[^a-zA-Z0-9]", "_").replace(" ", "_");
        if (System.getProperty("com.vaadin.testbench.build") != null) {
            build = normalizeBuild(System
                    .getProperty("com.vaadin.testbench.build")
                    + File.separator
                    + testName.replaceAll("[^a-zA-Z0-9]", "_")
                            .replace(" ", "_"));
        }

        // Set build path
        File dir = new File(build);
        // Create build/ if not exist
        if (!dir.exists()) {
            dir.mkdir();
        }

        // Create own suite for each browser
        for (int j = 0; j < browsers.length; j++) {
            // sanitize name
            String name = testName.replaceAll("[^a-zA-Z0-9]", "_").replace(" ",
                    "_")
                    + "_"
                    + browsers[j].replaceAll("[^a-zA-Z0-9]", "_")
                    + "_Suite";

            try {
                // Create file and write header
                BufferedWriter out = new BufferedWriter(new FileWriter(
                        new File(dir.getPath() + "/" + name + ".java")));
                out.write("import junit.framework.Test;\n");
                out.write("import junit.framework.TestSuite;\n\n");
                out.write("public class " + name + "{\n");
                out.write("public static Test suite(){\n");
                out.write("TestSuite suite = new TestSuite(\"" + name
                        + "\");\n");

                // Iterate through all given tests
                for (String test : tests) {
                    // Get file
                    File file = new File(test);
                    if (!file.exists()) {
                        file = new File(path + test);
                        if (!file.exists()) {
                            System.out.println("Searching for file " + test
                                    + " for making test Suite files.");
                            file = IOFunctions.getFile(test, new File(path), 0);
                        }
                    }

                    if (file == null) {
                        throw new FileNotFoundException("Couldn't locate file "
                                + test);
                    }

                    // Get file type of test (.html, .java)
                    String fileType = "";
                    String classname = "";
                    try {
                        fileType = file.getName().substring(
                                file.getName().lastIndexOf('.') + 1,
                                file.getName().length());
                        // Get class name for test
                        classname = file.getName().substring(0,
                                file.getName().lastIndexOf('.'));
                    } catch (IndexOutOfBoundsException ioobe) {
                        System.err.println("Could not get file type for file "
                                + file.getName());
                    }

                    if ("java".equals(fileType)) {
                        String filePackage = "";
                        try {
                            // Open file for reading
                            BufferedReader in = new BufferedReader(
                                    new FileReader(file));
                            String line = "";
                            // Search for possible package and add as path to
                            // classname if found
                            while ((line = in.readLine()) != null) {
                                if (line.contains("package")) {
                                    String[] pkg = line.split(" ");
                                    classname = pkg[1].substring(0, pkg[1]
                                            .length() - 1)
                                            + "." + classname + ".class";
                                    filePackage = pkg[1].substring(0, pkg[1]
                                            .length() - 1);
                                    filePackage = filePackage.replace(".", "/");
                                    break;
                                } else if (line.contains("public class")) {
                                    break;
                                }
                            }
                            in.close();

                            if (!classname.contains(".class")) {
                                classname = classname + ".class";
                            }

                            // add file to suite
                            out.write("suite.addTestSuite(" + classname
                                    + ");\n");

                            // Open and create folder path
                            File target = new File(dir.getPath() + "/"
                                    + filePackage + "/" + file.getName());

                            if (!target.getParentFile().exists()) {
                                target.getParentFile().mkdirs();
                            }
                            // Copy file to folder
                            IOFunctions.copyFile(file, target);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if ("html".equals(fileType)) {
                        // If .html file convert to .java file
                        com.vaadin.testbench.util.TestConverter
                                .runnerConvert(new String[] {
                                        dir.getAbsolutePath(), browsers[j],
                                        file.getAbsolutePath() });

                        out.write("suite.addTestSuite("
                                + PACKAGE_DIR.replace("\\", ".").replace("/",
                                        ".") + "." + classname + ".class);\n");
                    } else {
                        // Else close file and throw exception
                        System.err.println("Found unsupported file "
                                + file.getName());
                        System.err
                                .println("Supported files are .html and .java");

                        out.write("return suite;\n");
                        out.write("}\n");
                        out.write("}\n");
                        out.flush();
                        out.close();
                        throw new UnsupportedOperationException(
                                "Unsupported file.");
                    }
                    file = null;
                }
                // Write end of file and close.
                out.write("return suite;\n");
                out.write("}\n");
                out.write("}\n");
                out.flush();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Build ant file to run test suite with
        IOFunctions.buildAntFile(dir, testName);
        dir = null;
    }

    /**
     * Creates testSuite from .xml or .html suite file
     * 
     * @param file
     *            Suite file (.xml/.html)
     * @param path
     *            Base path to suite file/path to create suite files + build.xml
     * 
     * @return created TestBenchSuite
     * 
     * @throws Exception
     *             FileNotFoundException
     */
    public TestBenchSuite parseTestSuite(String file, String path)
            throws Exception {

        path = normalizePath(path);

        File testSuite = new File(file);
        if (!testSuite.exists()) {
            testSuite = new File(path + file);
            if (!testSuite.exists()) {
                System.out.println("Searching for file " + testSuite.getName()
                        + " to parse.");
                // If not found do a small search for file
                testSuite = IOFunctions.getFile(testSuite.getName(), testSuite
                        .getParentFile(), 0);
            }
        }

        if (testSuite == null) {
            System.err.println("Couldn't find given file. " + path + file);
            return null;
        }

        // Set initial path and test name from file name and path
        // path = testSuite.getParent();
        String title = testSuite.getName().substring(0,
                testSuite.getName().lastIndexOf('.'));

        // Get file type of test (.html, .java)
        String fileType = file.substring(file.lastIndexOf('.') + 1, file
                .length());
        if ("xml".equals(fileType)) {
            ParsedSuite result = ParserFunctions.readXmlFile(file, path);

            if (result.getTestName() != null) {
                title = result.getTestName();
            }

            path = testSuite.getParent();
            // Add path component defined in Suite file.
            if (result.getPath() != null) {
                if (!File.separator.equals(path.charAt(path.length() - 1))) {
                    path = path + File.separator;
                }
                // path has been checked in XML parser
                path = path + result.getPath();
            }

            if (makeTests) {
                makeTestSuiteFiles(result.getSuiteTests(), path, title);
            } else {
                return createTestSuite(result.getSuiteTests(), path, title);
            }
        } else if ("html".equals(fileType)) {
            title = "Suite";
            ParsedSuite result = ParserFunctions.readHtmlFile(file, path);

            path = normalizePath(testSuite.getParent());

            if (result.getTestName() != null) {
                title = result.getTestName();
            }
            // Combine tests to one and set new list to result
            result.setSuiteTests(ParserFunctions.combineTests(result
                    .getSuiteTests(), title, path));
            if (makeTests) {
                makeTestSuiteFiles(result.getSuiteTests(), path, title);
            } else {
                return createTestSuite(result.getSuiteTests(), path, title);
            }
        }
        return null;
    }

    /**
     * Parse a list of test files/suites.
     * 
     * @param files
     *            List of test files/suites.
     * @param path
     *            Base path to use
     * 
     * @return created TestBenchSuite
     * 
     * @throws Exception
     *             FileNotFoundException - if a file can't be found
     */
    public TestBenchSuite parseFiles(String[] files, String path)
            throws Exception {
        List<String> tests = new LinkedList<String>();

        path = normalizePath(path);

        for (String file : files) {
            if (file.contains(".java")) {
                File testFile = new File(file);
                if (!testFile.exists()) {
                    testFile = new File(path + file);
                    if (!testFile.exists()) {
                        System.out.println("Searching for file "
                                + testFile.getName() + " to add to tests.");
                        // If not found do a small search for file
                        testFile = IOFunctions.getFile(testFile.getName(),
                                testFile.getParentFile(), 0);
                    }
                }

                if (testFile == null) {
                    throw new FileNotFoundException("Could not find file "
                            + file);
                }
                tests.add(testFile.getAbsolutePath());
            } else if (file.contains(".html")) {

                File testFile = new File(file);
                if (!testFile.exists()) {
                    testFile = new File(path + file);
                    if (!testFile.exists()) {
                        System.out.println("Searching for file "
                                + testFile.getName() + " to parse.");
                        // If not found do a small search for file
                        testFile = IOFunctions.getFile(testFile.getName(),
                                testFile.getParentFile(), 0);
                    }
                }

                if (testFile == null) {
                    throw new FileNotFoundException("Could not find file "
                            + file);
                }

                BufferedReader in = new BufferedReader(new FileReader(testFile));
                try {
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        if (line.contains("<thead>")) {
                            tests.add(testFile.getAbsolutePath());
                            break;
                        } else if (line.contains("a href=")) {
                            // If more than 1 file add tests defined in
                            // testSuite to list of tests, else create suite
                            // from testSuite file
                            if (files.length > 1) {
                                String title = "Suite";
                                ParsedSuite result = ParserFunctions
                                        .readHtmlFile(file, path);
                                if (result.getTestName() != null) {
                                    title = result.getTestName();
                                }
                                if (!connectTests) {
                                    tests.addAll(ParserFunctions
                                            .combineTests(result
                                                    .getSuiteTests(), title,
                                                    path));
                                } else {
                                    tests.addAll(result.getSuiteTests());
                                }
                            } else {
                                return parseTestSuite(file, path);
                            }
                            break;
                        }
                    }
                } finally {
                    in.close();
                }

            } else if (file.contains(".xml")) {
                // If more than 1 file add tests defined in testSuite to
                // list of tests, else create suite from testSuite file
                if (files.length > 1) {
                    ParsedSuite result = ParserFunctions
                            .readXmlFile(file, path);

                    tests.addAll(result.getSuiteTests());
                } else {
                    return parseTestSuite(file, path);
                }
            }
        }
        if (tests.size() > 0) {
            if (connectTests) {
                tests = ParserFunctions.combineTests(tests, null, path);
            }
            if (makeTests) {
                makeTestSuiteFiles(tests, path, "test_collection");
            } else {
                return createTestSuite(tests, path, "test_collection");
            }
        }
        return null;
    }

    /**
     * Parse a list of test files/suites.
     * 
     * @param files
     *            List of test files/suites.
     * @param path
     *            Base path to use
     * 
     * @return created TestBenchSuite
     * 
     * @throws Exception
     *             FileNotFoundException - if a file can't be found
     */
    public TestBenchSuite parseFiles(List<String> files, String path)
            throws Exception {

        path = normalizePath(path);

        String[] tests = new String[files.size()];
        for (int i = 0; i < files.size(); i++) {
            tests[i] = files.get(i);
        }

        return parseFiles(tests, path);
    }

    /**
     * Run all testSuites in all TestBenchSuites.
     * 
     * @return True if all tests successful, false if any test failed.
     */
    public boolean runTestSuites() {
        // testResults.clear();
        for (TestBenchSuite tbs : testBenchSuites) {
            for (String key : tbs.getBrowsers()) {
                System.out.println("Browser  : " + key);
                tbs.setResult(key, runTestSuite(tbs.getTestSuite(key)));
            }
        }
        return true;
    }

    /**
     * Run all testSuites in TestBenchSuite.
     * 
     * @param tbs
     *            TestBenchSuite from which to run all testSuites
     * 
     * @return True if all tests successful, false if any test failed.
     */
    public boolean runTestSuites(TestBenchSuite tbs) {
        // testResults.clear();
        for (String key : tbs.getBrowsers()) {
            System.out.println("Browser  : " + key);
            tbs.setResult(key, runTestSuite(tbs.getTestSuite(key)));
        }
        return true;
    }

    /**
     * Run given test suite
     * 
     * @param testsuite
     *            Test suite to run
     */
    public TestResult runTestSuite(TestSuite testsuite) {
        System.out.println("Testsuite: " + testsuite.getName());

        result = new TestResult();

        Enumeration<Test> e = testsuite.tests();
        long startTime = System.currentTimeMillis();
        while (e.hasMoreElements()) {
            Test test = e.nextElement();
            testsuite.runTest(test, result);
            if (result.errorCount() > 0 || result.failureCount() > 0) {
                break;
            }
        }
        long stopTime = System.currentTimeMillis();
        long seconds = stopTime - startTime;

        IOFunctions.printResult(seconds, result);

        return result;
    }

    /**
     * Get results for run test suites
     * 
     * @return List<TestResult>
     */
    public Map<String, TestResult> getTestResults(TestBenchSuite suite) {
        return suite.getResults();
    }

    /**
     * Get test result for last run TestSuite for browser.
     * 
     * @return testResult
     */
    public TestResult getTestResult(String browser, TestBenchSuite suite) {
        return suite.getResult(browser);
    }

    /**
     * Get all TestBenchSuite objects for this TestBenchRunner.
     * 
     * @return testBenchSuites
     */
    public List<TestBenchSuite> getTestBenchSuites() {
        return testBenchSuites;
    }

    /**
     * Combine test suites for all browsers to one big test suite that can be
     * run in ant
     * 
     * @param suite
     *            TestBenchSuite whose suites to combine
     * @return combined suites for all browsers
     */
    public TestSuite getCombinedSuite(TestBenchSuite suite) {
        TestSuite combined = new TestSuite();
        String[] browsers = suite.getBrowsers();

        for (String browser : browsers) {
            Enumeration<Test> e = suite.getTestSuite(browser).tests();
            while (e.hasMoreElements()) {
                combined.addTest(e.nextElement());
            }
        }

        return combined;
    }

    /**
     * Get TestBenchSuite number suite
     * 
     * @param suite
     *            Number of suite to get
     * @return testBenchSuite
     */
    public TestBenchSuite getTestBenchSuite(int suite) {
        if (suite >= testBenchSuites.size()) {
            return null;
        }
        return testBenchSuites.get(suite);
    }

    /**
     * Clear lists.
     */
    public void clearTestSuites() {
        testBenchSuites.clear();
    }

    /**
     * Add ${user.dir} to path for absolute path.
     * 
     * @param path
     *            path to normalize
     * @return full path
     */
    private String normalizePath(String path) {
        // Set path to the current working directory if given path == null
        if (path == null || path.equals(".")) {
            path = System.getProperty("user.dir");
        } else if (!path.startsWith(System.getProperty("user.dir"))
                && (!path.startsWith("/") || !path.contains(":\\"))) {
            path = System.getProperty("user.dir") + File.separator + path;
        }
        // Check that path ends with fileseparator token for later use.
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }

        return path;
    }

    /**
     * Add ${user.dir} to path for absolute path.
     * 
     * @param build
     *            path to normalize
     * @return full path
     */
    private String normalizeBuild(String build) {
        if (!build.startsWith("/") || !build.contains(":\\")) {
            build = System.getProperty("user.dir") + File.separator + build;
        }
        return build;
    }
}
