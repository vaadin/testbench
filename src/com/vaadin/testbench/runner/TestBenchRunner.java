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
import com.vaadin.testbench.runner.util.ParserFunctions;

public class TestBenchRunner {

    private static final String PACKAGE_DIR = "com/vaadin/automatedtests";

    /** The test suites. */
    private List<TestSuite> testSuites;
    private List<TestResult> testResults;
    private TestResult testResult;
    private com.sun.tools.javac.Main javac;
    private TestResult result;
    private String[] browsers;
    private static boolean makeTests = false;

    public TestBenchRunner() {
        testSuites = new LinkedList<TestSuite>();
        testResults = new LinkedList<TestResult>();
        javac = new com.sun.tools.javac.Main();
        browsers = new String[] { "winxp-firefox35" };
    }

    /**
     * Main method.
     * 
     * @param args
     *            [-options] (test suites)
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            printHelp();
            System.exit(1);
        }

        String[] tests = null;
        String path = null;

        // Check given arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")) {
                path = args[i + 1];
                i++;
            } else if (args[i].equals("-make")) {
                makeTests = true;
            } else if (args[i].equals("-help")) {
                printHelp();
                System.exit(0);
            } else if (args[i].equals("-singleSuite")) {
                // TODO: create single suite of mixed files (tests and
                // testSuites)
            } else {
                tests = args[i].split(",");
            }
        }

        if (tests == null || tests.length == 0) {
            System.out.println("No tests found in arguments.");
            System.exit(1);
        }

        // Create new TestBenchRunner
        TestBenchRunner tbr = new TestBenchRunner();
        try {
            tbr.parseFiles(tests, path);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        // If not makeTests == true run tests
        if (!makeTests) {
            tbr.runTestSuites();
        }
    }

    /**
     * Print main commands/usage to system.err
     */
    private static void printHelp() {
        System.err.println("Usage: " + TestBenchRunner.class.getName()
                + " [-options] <test files>");
        System.err.println("\t<test suites>\t - ',' separated files\n");
        System.err.println("Options include:\t\t");
        System.err.println("\t-p\tBase path for files");
        System.err.println("\t-make\tCreates test suite java and build.xml");
        System.err.println("\t-help\tThis help");
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
     */
    @SuppressWarnings( { "unchecked" })
    public void createTestSuite(List<String> tests, String path, String testName)
            throws Exception {
        if (System.getProperty("com.vaadin.testbench.browsers") != null) {
            browsers = System.getProperty("com.vaadin.testbench.browsers")
                    .split(",");
        }

        if (path == null) {
            path = System.getProperty("user.dir");
        }
        // Check that path ends with fileseparator token for later use.
        if (!File.separator.equals(path.charAt(path.length() - 1))) {
            path = path + File.separator;
        }

        String build = path + "build";
        if (System.getProperty("com.vaadin.testbench.build") != null) {
            build = System.getProperty("com.vaadin.testbench.build");
        }

        // Set build path
        File dir = new File(build);
        // Create build/ if not exist
        if (!dir.exists()) {
            dir.mkdir();
        }

        for (int j = 0; j < browsers.length; j++) {
            List<Class> classes = new LinkedList<Class>();

            // Iterate through all given tests
            for (String test : tests) {
                // Get file
                File file = new File(test);
                if (!file.exists()) {
                    file = new File(path + test);
                    if (!file.exists()) {
                        file = IOFunctions.getFile(test, new File(path), 0);
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
                            "-cp",
                            System.getProperty("java.class.path", ".") + ";"
                                    + file.getParent(), "-d",
                            dir.getAbsolutePath(), file.getAbsolutePath() };
                    // // Compile
                    int status = javac.compile(options);

                    // If compilation successfull, load class dynamically and
                    // add to testSuite
                    if (status == 0) {
                        // Get path to class file
                        File classFile = IOFunctions.getFile(classname
                                + ".class", dir, 0);
                        if (classFile != null) {
                            ClassLoader loader = new URLClassLoader(
                                    new URL[] { dir.toURL() });
                            try {
                                String pkg = classFile.getParent().substring(
                                        dir.getPath().length() + 1,
                                        classFile.getParent().length());
                                pkg = pkg.replace("\\", ".").replace("/", ".");
                                Class c = loader.loadClass(pkg + "."
                                        + classname);
                                classes.add(c);
                            } catch (IndexOutOfBoundsException e) {
                                Class c = loader.loadClass(classname);
                                classes.add(c);
                            }
                        }
                    } else {
                        throw new RuntimeException(
                                "Compilation failed with status " + status);
                    }
                } else if ("html".equals(fileType)) {
                    // Parse and compile TestBench test saved as .html
                    // File temp = new File(path + "temp");
                    // temp.deleteOnExit();
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

                    String[] options = new String[] { "-cp",
                            System.getProperty("java.class.path", "."), "-d",
                            dir.getAbsolutePath(), file.getAbsolutePath() };
                    int status = javac.compile(options);

                    if (status == 0) {
                        File classFile = IOFunctions.getFile(classname
                                + ".class", new File(dir + File.separator
                                + PACKAGE_DIR), 0);
                        if (classFile != null) {
                            ClassLoader loader = new URLClassLoader(
                                    new URL[] { dir.toURL() });
                            try {
                                String pkg = classFile.getParent().substring(
                                        dir.getPath().length() + 1,
                                        classFile.getParent().length());
                                pkg = pkg.replace("\\", ".").replace("/", ".");
                                Class c = loader.loadClass(pkg + "."
                                        + classname);
                                classes.add(c);
                            } catch (IndexOutOfBoundsException e) {
                                Class c = loader.loadClass(classname);
                                classes.add(c);
                            }
                        }
                    } else {
                        throw new RuntimeException(
                                "Compilation failed with status " + status);
                    }
                    temp = null;
                }

            }

            Class[] testClasses = new Class[classes.size()];
            for (int i = 0; i < classes.size(); i++) {
                testClasses[i] = classes.get(i);
            }
            TestSuite suite = new TestSuite(testClasses);
            suite.setName(testName + "_" + browsers[j]);
            // Add created test suite to List
            testSuites.add(suite);
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

        if (path == null) {
            path = System.getProperty("user.dir");
        }
        // Check that path ends with fileseparator token for later use.
        if (!File.separator.equals(path.charAt(path.length() - 1))) {
            path = path + File.separator;
        }

        String build = path
                + testName.replaceAll("[^a-zA-Z0-9]", "_").replace(" ", "_");
        if (System.getProperty("com.vaadin.testbench.build") != null) {
            build = System.getProperty("com.vaadin.testbench.build")
                    + File.separator
                    + testName.replaceAll("[^a-zA-Z0-9]", "_")
                            .replace(" ", "_");
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

                        com.vaadin.testbench.util.TestConverter
                                .runnerConvert(new String[] {
                                        dir.getAbsolutePath(), browsers[j],
                                        file.getAbsolutePath() });

                        out.write("suite.addTestSuite("
                                + PACKAGE_DIR.replace("\\", ".").replace("/",
                                        ".") + "." + classname + ".class);\n");
                    } else {
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
                out.write("return suite;\n");
                out.write("}\n");
                out.write("}\n");
                out.flush();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
     */
    @SuppressWarnings("unchecked")
    public void parseTestSuite(String file, String path) throws Exception {
        if (path == null) {
            path = System.getProperty("user.dir");
        }
        // Check that path ends with fileseparator token for later use.
        if (!File.separator.equals(path.charAt(path.length() - 1))) {
            path = path + File.separator;
        }

        File testSuite = new File(file);
        if (!testSuite.exists()) {
            testSuite = new File(path + file);
            if (!testSuite.exists()) {
                // If not found do a small search for file
                testSuite = IOFunctions.getFile(testSuite.getName(), testSuite
                        .getParentFile(), 0);
            }
        }

        if (testSuite == null) {
            System.err.println("Couldn't find given file.");
            return;
        }

        // Set initial path and test name from file name and path
        // path = testSuite.getParent();
        String title = testSuite.getName().substring(0,
                testSuite.getName().lastIndexOf('.'));

        // Get file type of test (.html, .java)
        String fileType = file.substring(file.lastIndexOf('.') + 1, file
                .length());
        if ("xml".equals(fileType)) {
            try {
                Map<String, Object> result = ParserFunctions.readXmlFile(file,
                        path);
                if (result.get("title") != null) {
                    title = (String) result.get("title");
                }

                path = testSuite.getParentFile().getAbsolutePath();

                if (makeTests) {
                    makeTestSuiteFiles((List<String>) result.get("tests"),
                            path, title);
                } else {
                    createTestSuite((List<String>) result.get("tests"), path,
                            title);
                }
            } catch (FileNotFoundException e) {
                throw e;
            }
        } else if ("html".equals(fileType)) {
            try {
                Map<String, Object> result = ParserFunctions.readHtmlFile(file,
                        path);
                path = testSuite.getParentFile().getAbsolutePath();
                if (makeTests) {
                    makeTestSuiteFiles((List<String>) result.get("tests"),
                            path, title);
                } else {
                    createTestSuite((List<String>) result.get("tests"), path,
                            title);
                }
            } catch (FileNotFoundException e) {
                throw e;
            }
        }
    }

    /**
     * Parse a list of test files/suites
     * 
     * @param files
     *            List of test files/suites.
     * @param path
     *            Base path to use
     */
    @SuppressWarnings("unchecked")
    public void parseFiles(String[] files, String path) throws Exception {
        List<String> tests = new LinkedList<String>();

        for (String file : files) {
            if (file.contains(".java")) {
                tests.add(file);
            } else if (file.contains(".html")) {
                if (path == null) {
                    path = System.getProperty("user.dir");
                }
                // Check that path ends with fileseparator token for later use.
                if (!File.separator.equals(path.charAt(path.length() - 1))) {
                    path = path + File.separator;
                }

                File testFile = new File(file);
                if (!testFile.exists()) {
                    testFile = new File(path + file);
                    if (!testFile.exists()) {
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
                String line = "";
                while ((line = in.readLine()) != null) {
                    if (line.contains("<thead>")) {
                        tests.add(file);
                        break;
                    } else if (line.contains("a href=")) {
                        parseTestSuite(file, path);
                        break;
                    }
                }

            } else if (file.contains(".xml")) {
                parseTestSuite(file, path);
            }
        }
        if (tests.size() > 0) {
            if (makeTests) {
                makeTestSuiteFiles(tests, path, "test_collection");
            } else {
                createTestSuite(tests, path, "test_collection");
            }
        }
    }

    /**
     * Run all testSuites
     */
    public void runTestSuites() {
        testResults.clear();
        for (int i = 0; i < testSuites.size(); i++) {
            testResults.add(runTestSuite(i));
        }
    }

    /**
     * Run specified test suite in list
     * 
     * @param testsuite
     *            Position of test suite to run
     */
    public TestResult runTestSuite(int testsuite) {
        testResults.clear();
        try {
            TestSuite suite = testSuites.get(testsuite);
            System.out.println("Running: " + suite.getName());

            result = new TestResult();

            Enumeration<Test> e = suite.tests();
            long startTime = System.currentTimeMillis();
            while (e.hasMoreElements()) {
                Test test = e.nextElement();
                suite.runTest(test, result);
                if (result.errorCount() > 0 || result.failureCount() > 0) {
                    break;
                }
            }
            long stopTime = System.currentTimeMillis();
            int seconds = (int) (stopTime - startTime) / 1000;

            IOFunctions.printResult(seconds, result);

            testResult = result;
        } catch (IndexOutOfBoundsException ioobe) {
            System.err.println("No test suite on index " + testsuite);
            System.err.println("Only " + testSuites.size()
                    + " test suites available.");
            for (int i = 0; i < testSuites.size(); i++) {
                System.err.println("\t" + i + "\t"
                        + testSuites.get(i).getName());
            }
        }
        return result;
    }

    /**
     * Get list of all defined test suites
     * 
     * @return List<TestSuite>
     */
    public List<TestSuite> getTestSuites() {
        return testSuites;
    }

    /**
     * Get results for run test suites
     * 
     * @return List<TestResult>
     */
    public List<TestResult> getTestResults() {
        return testResults;
    }

    /**
     * Get test result for last runTestSuite
     * 
     * @return testResult
     */
    public TestResult getTestResult() {
        return testResult;
    }

    /**
     * Clear lists (TestSuites and corresponding TestResults)
     */
    public void clearTestSuites() {
        testSuites.clear();
        testResults.clear();
    }
}
