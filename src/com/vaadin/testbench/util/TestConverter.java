package com.vaadin.testbench.util;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.vaadin.testbench.util.SeleniumHTMLTestCaseParser.Command;

public class TestConverter {

    private static Map<String, String> knownBrowsers = new HashMap<String, String>();
    static {
        /*
         * Self mappings are to avoid unnecessary unknown browser warnings if
         * user wants to use selenium id strings.
         */
        knownBrowsers.put("firefox", "*chrome");
        knownBrowsers.put("*chrome", "*chrome");
        knownBrowsers.put("ie", "*iexplore");
        knownBrowsers.put("*iexplore", "*iexplore");
        knownBrowsers.put("opera", "*opera");
        knownBrowsers.put("*opera", "*opera");
        knownBrowsers.put("safari", "*safari");
        knownBrowsers.put("*safari", "*safari");
        knownBrowsers.put("googlechrome", "*googlechrome");
        knownBrowsers.put("*googlechrome", "*googlechrome");
    }
    // "package {package};\n" + "\n"
    private static final String JAVA_HEADER = "import com.vaadin.testbench.testcase.AbstractVaadinTestCase;\n"
            + "import java.io.IOException;\n"
            + "import java.io.File;\n"
            + "import javax.imageio.ImageIO;\n"
            + "import com.vaadin.testbench.util.ImageUtil;\n"
            + "import com.vaadin.testbench.util.CurrentCommand;\n"
            + "import com.vaadin.testbench.util.BrowserUtil;\n"
            + "import com.vaadin.testbench.util.BrowserVersion;\n"
            + "\n"
            + "public class {class} extends AbstractVaadinTestCase {\n" + "\n";

    private static final String TEST_METHOD_HEADER = "private void {testMethodName}() throws Throwable {\n";
    private static final String TEST_METHOD_FOOTER = "}\n";

    private static final String JAVA_FOOTER = "}\n";

    // Flags to determine what to do during conversion.
    private static boolean screenshot = false;
    private static boolean firstScreenshot = true;
    private static boolean isOpera = false, isSafari = false, isChrome = false;
    private static boolean runner = false;

    // Path to file being converted.
    private static String filePath = "";
    private static String absoluteFilePath = "";

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: " + TestConverter.class.getName()
                    + " <output directory> <browsers> <html test files>");
            System.exit(1);
        }

        String outputDirectory = args[0];
        String browserString = args[1];
        String browsers[] = browserString.split(",");

        List<String> browserList = new ArrayList<String>();
        for (String browser : browsers) {
            browserList.add(browser);
        }

        System.out.println("Using output directory: " + outputDirectory);
        createIfNotExists(outputDirectory);

        // Write the tests to the java file
        for (int i = 2; i < args.length; i++) {
            if (browsers.length > 1) {
                List<String> pick = new ArrayList<String>();
                for (String browser : browsers) {
                    pick.add(browser);
                }
                browserList.clear();
                Random rnd = new Random(System.currentTimeMillis());
                while (!pick.isEmpty()) {
                    int position = Math.abs(rnd.nextInt() % pick.size());
                    String picked = pick.get(position);
                    if (picked != null) {
                        browserList.add(picked);
                        pick.remove(position);
                    }
                }
            }
            OutputStream out = null;

            String filename;
            try {
                filename = checkIfSuite(args[i]);
                System.out.println("Generating test " + getTestName(filename));

                String testName = getTestName(filename);
                // Create a java file for the tests
                out = createJavaFileForTest(testName, outputDirectory);

                // Create a test method for each requested browser
                // Only wrappers which set the browser and call the real test
                // method
                for (String browser : browserList) {
                    StringBuilder browserInit = new StringBuilder();
                    browserInit.append("public void test"
                            + getSafeName(browser) + "() throws Throwable{\n");

                    browserInit.append("setBrowserIdentifier(\"" + browser
                            + "\");\n");
                    browserInit.append("super.setUp();\n");

                    browserInit.append(getTestMethodName(testName) + "();");
                    browserInit.append("\n}\n\n");
                    out.write(browserInit.toString().getBytes());
                }

                try {
                    String testMethod = createTestMethod(filename, testName);
                    out.write(testMethod.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Write the footer to the browser test class.
                writeJavaFooter(out);
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private static String getTestMethodName(String testName) {
        return "internal_" + testName;
    }

    /**
     * Test converter for use with TestBenchRunner, gives less output.
     * 
     * @param args
     *            (output directory) (browsers) (html test files)
     */
    public static void runnerConvert(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: " + TestConverter.class.getName()
                    + " <output directory> <browsers> <html test files>");
            System.exit(1);
        }

        runner = true;

        String outputDirectory = args[0];
        String browserString = args[1];
        String browsers[] = browserString.split(",");

        File outputPath = new File(outputDirectory);
        if (!outputPath.exists()) {
            if (!outputPath.mkdirs()) {
                System.err.println("Could not create directory: "
                        + outputDirectory);
                System.exit(1);
            }
        }
        outputPath = new File(outputDirectory);
        if (!outputPath.exists()) {
            if (!outputPath.mkdirs()) {
                System.err.println("Could not create directory: "
                        + outputPath.getAbsolutePath());
            }
        }

        for (String browser : browsers) {
            // browserUnderConversion = browser;
            isOpera = isSafari = isChrome = false;

            OutputStream out = null;
            try {
                String browserId = knownBrowsers.get(browser.toLowerCase());
                if (browserId == null) {
                    if (browser.contains("Opera") || browser.contains("opera")) {
                        isOpera = true;
                    } else if (browser.contains("Safari")
                            || browser.contains("safari")) {
                        isSafari = true;
                    } else if (browser.contains("Google")
                            || browser.contains("google")) {
                        isChrome = true;
                    }
                } else {
                    if (browserId.equals("*opera")) {
                        isOpera = true;
                    } else if (browserId.equals("*safari")) {
                        isSafari = true;
                    } else if (browserId.equals("*googlechrome")) {
                        isChrome = true;
                    }
                }

                String filename = args[2];
                // Create a java file for holding all tests for this browser
                out = createJavaFile(getTestName(filename) + "_"
                        + browser.replaceAll("[^a-zA-Z0-9]", "_"), browser,
                        outputDirectory);

                try {
                    String testMethod = createTestMethod(filename,
                            getTestName(filename) + "_"
                                    + browser.replaceAll("[^a-zA-Z0-9]", "_"));
                    out.write(testMethod.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Write the footer to the browser test class.
                writeJavaFooter(out);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String checkIfSuite(String filename)
            throws FileNotFoundException, IOException {
        File testFile = new File(filename);
        // if (!testFile.exists()) {
        // System.out.println("Searching for file " + testFile.getName()
        // + " to parse.");
        // // If not found do a small search for file
        // testFile = IOFunctions.getFile(testFile.getName(), testFile
        // .getParentFile(), 0);
        // }

        if (testFile == null) {
            throw new FileNotFoundException("Could not find file " + filename);
        }

        BufferedReader in = new BufferedReader(new FileReader(testFile));
        try {
            String line = "";
            while ((line = in.readLine()) != null) {
                if (line.contains("<thead>")) {
                    return testFile.getAbsolutePath();
                } else if (line.contains("a href=")) {
                    ParsedSuite result = ParserFunctions.readHtmlFile(filename,
                            testFile.getParentFile().getAbsolutePath());

                    List<String> combined = ParserFunctions.combineTests(result
                            .getSuiteTests(), testFile.getName(), testFile
                            .getAbsolutePath());
                    if (combined.size() == 1) {
                        return combined.get(0);
                    }

                }
            }
        } catch (Exception e) {
            System.err.println("Parsing failed. Check stacktrace.");
            e.printStackTrace();
            System.exit(1);
        } finally {
            in.close();
        }
        return testFile.getAbsolutePath();
    }

    private static void writeJavaFooter(OutputStream out) throws IOException {
        String footer = getJavaFooter();
        out.write(footer.getBytes());

    }

    /**
     * Returns a sanitized name of the test. The test name is specified by its
     * filename, not by the test name inside the file.
     * 
     * @param filename
     *            Filename of the test
     * @return Sanitized name safe for use as a java method name
     */
    private static String getTestName(String filename) {
        File f = new File(filename);
        String testName = removeExtension(f.getName());

        // FIXME: Move to another location
        // Set path to file under conversion
        filePath = f.getParent();
        if (filePath == null) {
            filePath = "";
        } else if (!File.separator.equals(filePath
                .charAt(filePath.length() - 1))) {
            filePath = filePath + File.separator;
        }
        absoluteFilePath = f.getAbsolutePath();

        // Sanitize so it is a valid method name
        testName = testName.replaceAll("[^0-9a-zA-Z_]", "_");

        return testName;
    }

    private static OutputStream createJavaFileForTest(String testName,
            String outputDirectory) throws IOException {
        File outputFile = getJavaFile(testName, outputDirectory);
        System.out.println("Creating " + outputFile + " for " + testName);
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        outputStream.write(getJavaHeader(getSafeName(testName)));

        return outputStream;
    }

    private static OutputStream createJavaFile(String testName, String browser,
            String outputDirectory) throws IOException {
        File outputFile = getJavaFile(testName, outputDirectory);
        // System.out.println("Creating " + outputFile + " for " + browser
        // + " tests");
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        // FIXME This does no longer write a browser dependent header
        outputStream.write(getJavaHeader(testName));

        return outputStream;
    }

    private static String getSafeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private static void createIfNotExists(String directory) {
        File outputPath = new File(directory);
        if (!outputPath.exists()) {
            if (!outputPath.mkdirs()) {
                System.err.println("Could not create directory: " + directory);
                System.exit(1);
            } else {
                System.err.println("Created directory: " + directory);
            }
        }
    }

    private static String createTestMethod(String htmlFile, String testName)
            throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(htmlFile);
        String htmlSource = IOUtils.toString(fis);
        fis.close();

        htmlSource = htmlSource.replace("\"", "\\\"")
                .replaceAll("\\n", "\\\\n").replace("'", "\\'").replaceAll(
                        "\\r", "");

        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();

            List<Command> commands = parseTestCase(cx, scope, htmlSource);
            String testCaseMethod = createTestCaseMethod(testName, commands);
            return testCaseMethod;
            // System.out.println("Done");
        } finally {
            Context.exit();
        }

    }

    private static String createTestCaseMethod(String testName,
            List<Command> commands) {
        screenshot = false;
        firstScreenshot = true;
        String testCaseHeader = getTestCaseHeader(testName);
        String testCaseBody = convertTestCaseToJava(commands, testName);
        String testCaseFooter = getTestCaseFooter(testName);
        String currentCommand = "CurrentCommand cmd = new CurrentCommand(\""
                + testName + "\");\n";
        String versionDetector = "BrowserVersion browser = browserUtils.getBrowserVersion(selenium);\n"
                + "String mouseClickCommand = \"mouseClick\";\n"
                + "if (browser.isOpera() && browser.isOlderVersion(10,50)) {"
                + "     mouseClickCommand = \"mouseClickOpera\";\n" + "}\n";

        // Add these in the case a screenshot is wanted
        String windowFunctions = "doCommand(\"windowMaximize\", new String[] { \"\" });\n"
                + "doCommand(\"windowFocus\", new String[] { \"\" });\n"
                + "getCanvasPosition();\n";

        if (screenshot) {
            return testCaseHeader + currentCommand + versionDetector
                    + windowFunctions + "try{\n" + testCaseBody
                    + testCaseFooter;
        }

        return testCaseHeader + currentCommand + versionDetector + "try{\n"
                + testCaseBody + testCaseFooter;
    }

    private static String removeExtension(String name) {
        return name.replaceAll("\\.[^\\.]*$", "");
    }

    private static String getTestCaseHeader(String testName) {
        String header = TEST_METHOD_HEADER;
        header = header
                .replace("{testMethodName}", getTestMethodName(testName));

        return header;
    }

    private static String getTestCaseFooter(String testName) {
        // adding the softAssert so creating reference images throws a assert
        // failure at end of test
        String softAsserts = "if(!getSoftErrors().isEmpty()){\n"
                + "byte[] errors = new byte[2];\n"
                + "for(junit.framework.AssertionFailedError afe:getSoftErrors()){\n"
                + "if(afe.getMessage().contains(\"No reference found\")){\n"
                + "errors[0] = 1;\n"
                + "}else if(afe.getMessage().contains(\"differs from reference image\")){\n"
                + "errors[1] = 1;\n"
                + "}\n}\n"
                + "if(errors[0] == 1 && errors[1] == 1){\n"
                + "junit.framework.Assert.fail(\"Test was missing reference images and contained images with differences.\");\n"
                + "}else if(errors[0] == 1){\n"
                + "junit.framework.Assert.fail(\"Test was missing reference images.\");\n"
                + "}else if(errors[1] == 1){\n"
                + "junit.framework.Assert.fail(\"Test contained differences.\");\n"
                + "}else{\njunit.framework.Assert.fail(\"Image sizes differ.\");\n"
                + "}\n}\n";
        // if screenshot.onfail defined add try{ }catch( ){ }
        if ("true".equals(System
                .getProperty("com.vaadin.testbench.screenshot.onfail"))) {
            screenshot = true;
            softAsserts = "}catch(Throwable e){\n"
                    + "String statusScreen = selenium.captureScreenshotToString();\n"
                    + "String directory = System.getProperty(\"com.vaadin.testbench.screenshot.directory\");\n"
                    + "if (!File.separator.equals(directory.charAt(directory.length() - 1))) {\n"
                    + "directory = directory + File.separator;\n}\n"
                    + "File target = new File(directory + \"errors\");\n"
                    + "if(!target.exists()){\n"
                    + "target.mkdir();\n}\n"
                    + "try{\n"
                    + "ImageIO.write(ImageUtil.stringToImage(statusScreen), \"png\", new File(directory + \"errors/"
                    + testName
                    + "_failure_"
                    + "\"+ getBrowserIdentifier().replaceAll(\"[^a-zA-Z0-9]\", \"_\")+\""
                    + ".png\"));\n}catch(IOException ioe){\n"
                    + "ioe.printStackTrace();\n}\n"
                    + "throw new java.lang.AssertionError(cmd.getInfo() + \". Failure message = \" + e.getMessage());\n}\n"
                    + softAsserts;
        } else {
            softAsserts = "}catch(Throwable e){\nthrow new java.lang.AssertionError(cmd.getInfo() + \". Failure message = \" + e.getMessage());\n}\n"
                    + softAsserts;
        }
        String footer = TEST_METHOD_FOOTER;
        footer = footer.replace("{testName}", testName);

        if (screenshot) {
            return softAsserts + footer;
        }
        return "}catch(Throwable e){\nthrow new java.lang.AssertionError(cmd.getInfo() + \". Failure message = \" + e.getMessage());\n}\n"
                + footer;
    }

    private static byte[] getJavaHeader(String className) {
        String header = JAVA_HEADER;
        header = header.replace("{class}", className);
        // header = header.replace("{package}", getPackageName());

        return header.getBytes();
    }

    private static String getJavaFooter() {
        return JAVA_FOOTER;
    }

    private static File getJavaFile(String browser, String outputDirectory) {
        String filenameSafeBrowser = getSafeName(browser);

        File file = new File(filenameSafeBrowser);
        String filename = removeExtension(file.getName());

        File outputFile = new File(outputDirectory + File.separator + filename
                + ".java"); // + getPackageDir() + File.separator

        return outputFile;
    }

    private static String convertTestCaseToJava(List<Command> commands,
            String testName) {
        StringBuilder javaSource = new StringBuilder();

        for (Command command : commands) {
            if (command.getCmd().equals("screenCapture")) {
                String identifier = "";
                boolean first = true;
                // Get Value field for image name identifier
                for (String param : command.getParams()) {
                    if (!first) {
                        identifier = param;
                    }
                    first = false;
                }
                if (firstScreenshot) {
                    javaSource.append("pause(500);\n");
                    firstScreenshot = false;
                }

                javaSource.append("cmd.setCommand(\"screenCapture\", \""
                        + identifier + "\");\n");
                javaSource.append("validateScreenshot(\"" + testName
                        + "\", 0.025, \"" + identifier + "\");\n");
                screenshot = true;
            } else if (command.getCmd().equalsIgnoreCase("pause")) {
                String identifier = "";
                boolean first = true;
                // Get Target field for pause time
                for (String param : command.getParams()) {
                    if (first) {
                        identifier = param;
                    }
                    first = false;
                }
                javaSource.append("cmd.setCommand(\"pause\", \"" + identifier
                        + "\");\n");
                javaSource.append("pause(" + identifier + ");\n");
            } else if (command.getCmd().equalsIgnoreCase("pressSpecialKey")) {
                String value = "";
                String location = "";
                boolean first = true;
                for (String param : command.getParams()) {
                    if (first) {
                        /* Get the location target */
                        location = param.replace("\\", "\\\\");
                    } else {
                        /* get pressed key from keyCode or name */
                        if (param.contains("\\")) {
                            switch (Integer.parseInt(param.substring(param
                                    .lastIndexOf("\\")))) {
                            case 13:
                                value = "" + KeyEvent.VK_ENTER;
                                break;
                            case 37:
                                value = "" + KeyEvent.VK_LEFT;
                                break;
                            case 38:
                                value = "" + KeyEvent.VK_UP;
                                break;
                            case 39:
                                value = "" + KeyEvent.VK_RIGHT;
                                break;
                            case 40:
                                value = "" + KeyEvent.VK_DOWN;
                                break;
                            }
                        } else if ("UP".equalsIgnoreCase(param)) {
                            value = "" + KeyEvent.VK_UP;
                        } else if ("DOWN".equalsIgnoreCase(param)) {
                            value = "" + KeyEvent.VK_DOWN;
                        } else if ("LEFT".equalsIgnoreCase(param)) {
                            value = "" + KeyEvent.VK_LEFT;
                        } else if ("RIGHT".equalsIgnoreCase(param)) {
                            value = "" + KeyEvent.VK_RIGHT;
                        } else if ("ENTER".equalsIgnoreCase(param)) {
                            value = "" + KeyEvent.VK_ENTER;
                            // } else if ("BACKSPACE".equalsIgnoreCase(param)) {
                            // values.append("\\\\8\"");
                        }
                    }

                    first = false;
                }

                // If converting with runner result differs a little
                if (runner) {
                    /*
                     * Opera, Safari and GoogleChrome need the java native
                     * keypress
                     */
                    if (isOpera || isSafari || isChrome) {
                        javaSource
                                .append("cmd.setCommand(\"pressSpecialKey\", \""
                                        + value + "\");\n");
                        javaSource.append("selenium.focus(\"" + location
                                + "\");\n");
                        javaSource.append("selenium.keyPressNative(\"" + value
                                + "\");\n");
                    } else {

                        /* if enter VK_ENTER will give 10 instead of 13 */
                        if (Integer.parseInt(value) == 10) {
                            value = "13";
                        }

                        javaSource
                                .append("doCommand(\"pressSpecialKey\", new String[] { \""
                                        + location
                                        + "\", \"\\\\"
                                        + value
                                        + "\"});\n");
                    }
                } else {
                    // We don't know if opera/safari/chrome will be used
                    javaSource.append("cmd.setCommand(\"pressSpecialKey\", \""
                            + value + "\");\n");
                    javaSource
                            .append("if(browser.isSafari() || browser.isOpera() || browser.isChrome()){\n");
                    javaSource
                            .append("selenium.focus(\"" + location + "\");\n");
                    javaSource.append("selenium.keyPressNative(\"" + value
                            + "\");\n");
                    javaSource.append("} else {\n");

                    /* if enter VK_ENTER will give 10 instead of 13 */
                    if (Integer.parseInt(value) == 10) {
                        value = "13";
                    }

                    javaSource
                            .append("doCommand(\"pressSpecialKey\", new String[] { \""
                                    + location
                                    + "\", \"\\\\"
                                    + value
                                    + "\"});\n");
                    javaSource.append("}\n");
                }

            } else if (command.getCmd().equalsIgnoreCase("mouseClick")) {
                StringBuilder values = new StringBuilder();
                boolean first = true;
                String firstParam = "";
                for (String param : command.getParams()) {
                    if (first) {
                        /* get location */
                        firstParam = param;
                        values.append(param + "\", \"");
                    } else {
                        values.append(param);
                    }
                    first = false;
                }

                if (runner) {
                    if (isOpera) {
                        javaSource
                                .append("doCommand(\"mouseClickOpera\", new String[] {\""
                                        + values + "\"});\n");
                    } else {
                        javaSource
                                .append("doCommand(\"mouseClick\", new String[] {\""
                                        + values + "\"});\n}\n");
                    }
                } else {
                    // We don't know if opera will be used
                    javaSource
                            .append("cmd.setCommand(\"mouseClick\", \"\");\n");
                    javaSource
                            .append("doCommand(mouseClickCommand, new String[] {\""
                                    + values + "\"});\n");
                }
            } else if (command.getCmd().equalsIgnoreCase("verifyTextPresent")) {

                String identifier = "";
                boolean first = true;
                for (String param : command.getParams()) {
                    if (first) {
                        identifier = param;
                    }
                    first = false;
                }
                identifier = identifier.replace("\"", "\\\"").replaceAll("\\n",
                        "\\\\n");

                javaSource.append("cmd.setCommand(\"verifyTextPresent\", \""
                        + identifier + "\");\n");
                javaSource
                        .append("doCommand(\"verifyTextPresent\", new String[] {\""
                                + identifier + "\"});\n");
            } else if (command.getCmd().equalsIgnoreCase("assertTextPresent")) {

                String identifier = "";
                boolean first = true;
                for (String param : command.getParams()) {
                    if (first) {
                        identifier = param;
                    }
                    first = false;
                }
                identifier = identifier.replace("\"", "\\\"").replaceAll("\\n",
                        "\\\\n");

                javaSource.append("cmd.setCommand(\"assertTextPresent\", \""
                        + identifier + "\");\n");
                javaSource
                        .append("doCommand(\"assertTextPresent\", new String[] {\""
                                + identifier + "\"});\n");
            } else if (command.getCmd().equalsIgnoreCase("htmlTest")) {
                String locator = "";
                String value = "";
                boolean first = true;
                for (String param : command.getParams()) {
                    if (first) {
                        /* get locator */
                        locator = param.replace("\\", "\\\\");
                    } else {
                        /* get characters */
                        value = param;
                    }

                    first = false;
                }
                javaSource.append("cmd.resetCmdNr();\n");
                javaSource.append("cmd.setFile(\"" + value + "\");\n");
                javaSource.append("System.out.println(\"Start test " + value
                        + "\");");
            } else if (command.getCmd().equalsIgnoreCase("showTooltip")) {
                String locator = "";
                String value = "";
                boolean first = true;
                for (String param : command.getParams()) {
                    if (first) {
                        /* get locator */
                        locator = param.replace("\\", "\\\\");
                    } else {
                        /* get characters */
                        value = param;
                    }

                    first = false;
                }

                javaSource.append("cmd.setCommand(\"showTooltip\", \"\");\n");
                javaSource.append("doCommand(\"showTooltip\",new String[] {\""
                        + locator + "\", \"" + value + "\"});\n");
                javaSource.append("pause(700);\n");
            } else if (command.getCmd().equalsIgnoreCase("includeTest")) {
                // Loads another test, parses the commands, converts tests and
                // adds result to this TestCase
                String locator = "";
                String value = "";
                boolean first = true;

                for (String param : command.getParams()) {
                    if (first) {
                        /* get locator */
                        locator = param.replace("\\", "\\\\");
                    } else {
                        /* get target */
                        value = param.replace("\\", "\\\\");
                    }

                    first = false;
                }

                if (value.length() == 0) {
                    System.err.println("No file defined in Value field.");
                    System.err.println("Check includeTest command in "
                            + absoluteFilePath);
                    javaSource
                            .append("junit.framework.Assert.fail(\"No file defined in Value field.\");\n");
                } else {
                    // Try to load and parse test
                    // Get file absolutePath/relativeToIncludingFile/search from
                    // including file directory
                    File target = new File(value);
                    if (!target.exists()) {
                        target = new File(filePath + value);
                        if (!target.exists()) {
                            System.out
                                    .println("File not found resorting to search.");
                            target = getFile(target.getName(), new File(
                                    filePath));
                            if (target != null) {
                                System.out.println("Match found. Using "
                                        + target.getPath());
                            }
                        }
                    }
                    // If file not found add Assert.fail and print to System.err
                    if (target == null) {
                        target = new File(value);
                        javaSource
                                .append("junit.framework.Assert.fail(\"Couldn't find file "
                                        + target.getName() + "\");\n");
                        System.err.println("Failed to append test "
                                + target.getName());
                    } else {
                        // Save path to including file
                        String parentPath = filePath;
                        String absoluteParent = absoluteFilePath;
                        // Set path to this file
                        filePath = target.getParent();
                        if (filePath == null) {
                            filePath = "";
                        } else if (!File.separator.equals(filePath
                                .charAt(filePath.length() - 1))) {
                            filePath = filePath + File.separator;
                        }
                        absoluteFilePath = target.getAbsolutePath();

                        try {
                            // open and read target file
                            FileInputStream fis = new FileInputStream(target);
                            String htmlSource = IOUtils.toString(fis);
                            fis.close();

                            // sanitize source
                            htmlSource = htmlSource.replace("\"", "\\\"")
                                    .replaceAll("\\n", "\\\\n").replace("'",
                                            "\\'").replaceAll("\\r", "");

                            Context cx = Context.enter();

                            Scriptable scope = cx.initStandardObjects();

                            // Parse commands to a List
                            List<Command> newCommands = parseTestCase(cx,
                                    scope, htmlSource);
                            // Convert tests to Java
                            String tests = convertTestCaseToJava(newCommands,
                                    testName);
                            javaSource.append(tests);

                        } catch (Exception e) {
                            // if exception was caught put a assert fail to
                            // inform user of error.
                            System.err.println("Failed in appending test. "
                                    + e.getMessage());
                            if ("true".equalsIgnoreCase(System
                                    .getProperty("com.vaadin.testbench.debug"))) {
                                e.printStackTrace();
                            }
                            javaSource
                                    .append("junit.framework.Assert.fail(\"Insertion of test "
                                            + value
                                            + " failed with "
                                            + e.getMessage() + "\");");
                        } finally {
                            Context.exit();
                            // Set path back to calling file
                            filePath = parentPath;
                            absoluteFilePath = absoluteParent;
                        }
                    }
                }
            } else {

                javaSource.append("cmd.setCommand(\"" + command.getCmd()
                        + "\", \"\");\n");
                javaSource.append("doCommand(\"");
                javaSource.append(command.getCmd());
                javaSource.append("\",new String[] {");

                boolean first = true;
                for (String param : command.getParams()) {
                    if (!first) {
                        javaSource.append(",");
                    }
                    first = false;

                    javaSource.append("\"");
                    javaSource.append(param.replace("\"", "\\\"").replaceAll(
                            "\\n", "\\\\n"));
                    javaSource.append("\"");
                }
                javaSource.append("});\n");
            }
        }

        return javaSource.toString();
    }

    private static File getFile(String test, File buildPath) {
        File found = null;
        for (File file : buildPath.listFiles()) {
            if (file.isDirectory()) {
                found = getFile(test, file);
                if (found != null) {
                    return found;
                }
            } else if (file.isFile()) {
                if (file.getName().equals(test)) {
                    return file;
                }
            }
        }
        return found;
    }

    private static List<Command> parseTestCase(Context cx, Scriptable scope,
            String htmlSource) throws IOException {
        List<Command> commands = new ArrayList<Command>();

        cx.evaluateString(scope, "function load(a){}", "dummy-load", 1, null);
        cx
                .evaluateString(
                        scope,
                        "this.log = [];this.log.info = function log() {}; var log = this.log;",
                        "dummy-log", 1, null);

        loadScript("tools.js", scope, cx);
        loadScript("xhtml-entities.js", scope, cx);
        loadScript("html.js", scope, cx);
        loadScript("testCase.js", scope, cx);

        // htmlSource = htmlSource.replace("\\\n", "\\\\\n");
        cx.evaluateString(scope, "var src='" + htmlSource + "';",
                "htmlSourceDef", 1, null);
        cx.evaluateString(scope,
                "var testCase =  new TestCase();parse(testCase,src); ",
                "testCaseDef", 1, null);
        cx.evaluateString(scope, "var cmds = [];"
                + "var cmdList = testCase.commands;"
                + "for (var i=0; i < cmdList.length; i++) {"
                + "       var cmd = testCase.commands[i];"
                + "      if (cmd.type == 'command') {"
                + "              cmds.push(cmd);" + "      }" + "}" + "",
                "testCaseDef", 1, null);

        Object testCase = scope.get("cmds", scope);
        if (testCase instanceof NativeArray) {
            NativeArray arr = (NativeArray) testCase;
            for (int i = 0; i < arr.getLength(); i++) {
                NativeObject o = (NativeObject) arr.get(i, scope);
                Object target = o.get("target", scope);
                Object command = o.get("command", scope);
                Object value = o.get("value", scope);
                commands.add(new Command((String) command, (String) target,
                        (String) value));
            }
        }
        return commands;
    }

    private static void loadScript(String scriptName, Scriptable scope,
            Context cx) throws IOException {
        URL res = TestConverter.class.getResource(scriptName);
        cx.evaluateReader(scope, new InputStreamReader(res.openStream()),
                scriptName, 1, null);

    }

}
