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

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.vaadin.testbench.Parameters;
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

    // Empty setUp() is needed to prevent super.setUp from being executed in the
    // setup phase
    private static final String JAVA_HEADER = "package {package};\n\n"
            + "import com.vaadin.testbench.testcase.AbstractVaadinTestCase;\n"
            + "import java.io.IOException;\n"
            + "import java.io.File;\n"
            + "import javax.imageio.ImageIO;\n"
            + "import com.vaadin.testbench.util.ImageUtil;\n"
            + "import com.vaadin.testbench.util.CurrentCommand;\n"
            + "import com.vaadin.testbench.util.BrowserUtil;\n"
            + "import com.vaadin.testbench.util.BrowserVersion;\n\n"
            + "public class {class} extends AbstractVaadinTestCase {\n\n"
            + "private static final String[] error_messages = { \"was missing reference images\","
            + "\"contained differences\", \"contained images with differing sizes containing differences\", \"contained images with differing sizes\", \"\" "
            + "};\n\n" + "public void setUp(){\n}\n\n";;

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

    private static String windowInitFunctions = "doCommand(\"windowMaximize\", new String[] { \"\" });\n"
            + "doCommand(\"windowFocus\", new String[] { \"\" });\n"
            + "getBrowserAndCanvasDimensions();\n";;

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: " + TestConverter.class.getName()
                    + " <output directory> <browsers> <html test files>");
            System.exit(1);
        }
        try {
            // init ParameterUtil
            ParameterUtil.getInstance();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(1);
        }

        String outputDirectory = args[0];
        String browsers[] = args[1].split(",");

        System.out.println("Using output directory: " + outputDirectory);
        createIfNotExists(outputDirectory);

        // Write the tests to the java file
        for (int i = 2; i < args.length; i++) {
            OutputStream out = null;

            String filename;
            try {
                for (String browser : browsers) {
                    // browserUnderConversion = browser;
                    isOpera = isSafari = isChrome = false;

                    // Check if browser is opera, safari or chrome
                    checkBrowser(browser);

                    filename = getTestInputFilename(args[i]);

                    String testName = getTestName(filename);
                    String packageName = getJavaPackageName(testName, browser);
                    System.out.println("Generating test " + testName + " for "
                            + browser + " in " + packageName);

                    // Create a java file for the test
                    out = createJavaFileForTest(testName, packageName, browser,
                            outputDirectory);

                    /* Create a test method for the browser. */
                    StringBuilder browserInit = new StringBuilder();
                    browserInit.append("public void test"
                            + getSafeName(browser) + "() throws Throwable{\n");

                    browserInit.append("setBrowserIdentifier(\"" + browser
                            + "\");\n");
                    browserInit.append("super.setUp();\n");

                    browserInit.append(getTestMethodName(testName) + "();");
                    browserInit.append("\n}\n\n");
                    out.write(browserInit.toString().getBytes());

                    try {
                        String testMethod = createTestMethod(filename, testName);
                        out.write(testMethod.getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Write the footer to the browser test class.
                    writeJavaFooter(out);
                }
            } catch (Exception e1) {
                // Rethrow all exceptions. The conversion succeeds only if all
                // tests are found and can be converted.
                throw e1;
            }
        }
    }

    private static void checkBrowser(String browser) {
        String browserId = knownBrowsers.get(browser.toLowerCase());
        if (browserId == null) {
            if (browser.contains("Opera") || browser.contains("opera")) {
                isOpera = true;
            } else if (browser.contains("Safari") || browser.contains("safari")) {
                isSafari = true;
            } else if (browser.contains("Google") || browser.contains("google")) {
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
        String browserIdentifiers[] = browserString.split(",");

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

        for (String browserIdentifier : browserIdentifiers) {
            // browserUnderConversion = browser;
            isOpera = isSafari = isChrome = false;

            OutputStream out = null;
            try {
                // Check if browser is opera, safari or chrome
                checkBrowser(browserIdentifier);

                String filename = args[2];

                // Create a java file for holding all tests for this browser
                String safeBrowserIdentifier = getSafeName(browserIdentifier);
                String testName = getTestName(filename) + "_"
                        + safeBrowserIdentifier;

                out = createJavaFile(testName, browserIdentifier,
                        getJavaPackageName(testName, browserIdentifier),
                        outputDirectory);

                try {
                    String testMethod = createTestMethod(filename, testName);
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

    private static String getTestInputFilename(String TestBenchHTMLFile)
            throws FileNotFoundException, IOException {

        File testFile = new File(TestBenchHTMLFile);

        if (testFile == null) {
            throw new FileNotFoundException("Could not find file "
                    + TestBenchHTMLFile);
        }

        BufferedReader in = new BufferedReader(new FileReader(testFile));
        try {
            String line = "";
            while ((line = in.readLine()) != null) {
                if (line.contains("<thead>")) {
                    return testFile.getAbsolutePath();
                } else if (line.contains("a href=")) {
                    ParsedSuite result = ParserFunctions.readHtmlFile(
                            TestBenchHTMLFile, testFile.getParentFile()
                                    .getAbsolutePath());

                    List<String> combined = ParserFunctions.combineTests(
                            result.getSuiteTests(),
                            getTestName(testFile.getName()),
                            testFile.getAbsolutePath());
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
        } else if (!File.separator
                .equals(filePath.charAt(filePath.length() - 1))) {
            filePath = filePath + File.separator;
        }
        absoluteFilePath = f.getAbsolutePath();

        // Sanitize so it is a valid method name
        testName = testName.replaceAll("[^0-9a-zA-Z_]", "_");

        return testName;
    }

    private static OutputStream createJavaFileForTest(String testName,
            String packageName, String browserIdentifier, String outputDirectory)
            throws IOException {
        File outputFile = getJavaFile(testName, packageName, outputDirectory);
        System.out.println("Creating " + outputFile + " for " + testName);
        createIfNotExists(outputFile.getParent());
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        outputStream.write(getJavaHeader(getSafeName(testName), packageName));

        return outputStream;
    }

    private static OutputStream createJavaFile(String testName,
            String browserIdentifier, String packageName, String outputDirectory)
            throws IOException {
        File outputFile = getJavaFile(testName, packageName, outputDirectory);

        createIfNotExists(outputFile.getParent());
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        // FIXME This does no longer write a browser dependent header
        outputStream.write(getJavaHeader(testName, packageName));

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
                .replaceAll("\\n", "\\\\n").replace("'", "\\'")
                .replaceAll("\\r", "");

        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();

            List<Command> commands = parseTestCase(cx, scope, htmlSource);
            String testCaseMethod = createTestCaseMethod(testName, commands);
            return testCaseMethod;
        } finally {
            Context.exit();
        }

    }

    private static String createTestCaseMethod(String testName,
            List<Command> commands) throws FileNotFoundException {
        screenshot = false;
        firstScreenshot = true;
        String testCaseHeader = getTestCaseHeader(testName);
        String testCaseBody = convertTestCaseToJava(commands, testName);
        String testCaseFooter = getTestCaseFooter(testName);
        String currentCommand = "CurrentCommand cmd = new CurrentCommand(\""
                + testName + "\");\n";
        String versionDetector = "BrowserVersion browser = getBrowserVersion();\n"
                + "String mouseClickCommand = \"mouseClick\";\n"
                + "if (browser.isOpera() && browser.isOlderVersion(10,50)) {\n"
                + "\tmouseClickCommand = \"mouseClickOpera\";\n" + "}\n";

        // Add these in the case a screenshot is wanted
        if (screenshot) {
            return testCaseHeader + currentCommand + versionDetector
                    + windowInitFunctions + "try{\n" + testCaseBody
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
                + "StringBuilder message = new StringBuilder();\n"
                + "byte[] errors = new byte[5];\n"

                + "for(junit.framework.AssertionFailedError afe:getSoftErrors()){\n"
                + "if(afe.getMessage().contains(\"No reference found\")){\n"
                + "errors[0] = 1;\n"
                + "}else if(afe.getMessage().contains(\"differs from reference image\")){\n"
                + "errors[1] = 1;\n"
                + "}else if(afe.getMessage().contains(\"Images differ and\")){\n"
                + "errors[2] = 1;\n"
                + "}else if(afe.getMessage().contains(\"Images are of different size\")){\n"
                + "errors[3] = 1;\n" + "} else {\n" + "errors[4] = 1;\n"
                + "error_messages[4] = afe.getMessage();\n}\n}\n\n"

                + "boolean add_and = false;\n"
                + "message.append(\"Test \");\n\n"

                + "for(int i = 0; i < 5; i++){\n" + "if(errors[i] == 1){\n"
                + "if(add_and){\n" + "message.append(\" and \");\n" + "}\n"
                + "message.append(error_messages[i]);\n" + "add_and = true;\n"
                + "}\n" + "}\n\n"

                + "junit.framework.Assert.fail(message.toString());\n" + "}\n";
        // if screenshot.onfail defined add try{ }catch( ){ }
        if (!Parameters.isCaptureScreenshotOnFailure()) {
            softAsserts = "}catch(Throwable e){\nthrow new java.lang.AssertionError(cmd.getInfo() + \". Failure message = \" + e.getMessage());\n}\n"
                    + softAsserts;
        } else {
            screenshot = true;
            softAsserts = "}catch(Throwable e){\n"
                    + "String statusScreen = selenium.captureScreenshotToString();\n"
                    + "String directory = getScreenshotDirectory();\n"
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
        }
        String footer = TEST_METHOD_FOOTER;
        footer = footer.replace("{testName}", testName);

        if (screenshot) {
            return softAsserts + footer;
        }
        return "}catch(Throwable e){\nthrow new java.lang.AssertionError(cmd.getInfo() + \". Failure message = \" + e.getMessage());\n}\n"
                + footer;
    }

    private static byte[] getJavaHeader(String className, String packageName) {
        String header = JAVA_HEADER;
        header = header.replace("{class}", className);
        header = header.replace("{package}", packageName);

        return header.getBytes();
    }

    private static String getJavaPackageName(String testName, String browserName) {
        return testName + "." + getSafeName(browserName);
    }

    private static String getJavaFooter() {
        return JAVA_FOOTER;
    }

    private static File getJavaFile(String testName, String packageName,
            String outputDirectory) {
        String safeFilename = getSafeName(testName);

        File file = new File(safeFilename);
        String filename = removeExtension(file.getName());

        if (packageName.length() > 0) {
            // Add packagename to the filename
            filename = packageName.replace('.', File.separatorChar)
                    + File.separatorChar + filename;
        }
        File outputFile = new File(outputDirectory + File.separator + filename
                + ".java"); // + getPackageDir() + File.separator

        return outputFile;
    }

    private static String convertTestCaseToJava(List<Command> commands,
            String testName) throws FileNotFoundException {
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
                boolean ctrl, alt, shift;
                ctrl = alt = shift = false;
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
                        } else if (param.contains("up")) {
                            value = "" + KeyEvent.VK_UP;
                        } else if (param.contains("down")) {
                            value = "" + KeyEvent.VK_DOWN;
                        } else if (param.contains("left")) {
                            value = "" + KeyEvent.VK_LEFT;
                        } else if (param.contains("right")) {
                            value = "" + KeyEvent.VK_RIGHT;
                        } else if (param.contains("enter")) {
                            value = "" + KeyEvent.VK_ENTER;
                            // } else if ("BACKSPACE".equalsIgnoreCase(param)) {
                            // values.append("\\\\8\"");
                        } else if (param.contains("tab")) {
                            value = "" + KeyEvent.VK_TAB;
                        }
                        if (param.contains("shift")) {
                            shift = true;
                        }
                        if (param.contains("alt")) {
                            alt = true;
                        }
                        if (param.contains("ctrl")) {
                            ctrl = true;
                        }

                        // if native keypress is not used give text string to
                        // doCommand
                        if (!isOpera && !isSafari && !isChrome) {
                            value = param;
                        }
                    }

                    first = false;
                }

                javaSource.append("cmd.setCommand(\"pressSpecialKey\", \""
                        + value + "\");\n");
                /*
                 * Opera, Safari and GoogleChrome need the java native keypress
                 */
                if (isOpera || isSafari || isChrome) {
                    javaSource
                            .append("selenium.focus(\"" + location + "\");\n");

                    appendKeyModifierDown(javaSource, ctrl, alt, shift);

                    javaSource.append("selenium.keyPressNative(\"" + value
                            + "\");\n");

                    appendKeyModifierUp(javaSource, ctrl, alt, shift);
                } else {
                    javaSource
                            .append("doCommand(\"pressSpecialKey\", new String[] { \""
                                    + location
                                    + "\", \"\\\\"
                                    + value
                                    + "\"});\n");
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

                javaSource.append("cmd.setCommand(\"mouseClick\", \"\");\n");

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
                    includeTest(javaSource, value, testName);
                }
            } else if (command.getCmd().equals("open")) {
                javaSource.append("try {");
                writeDoCommand(command, javaSource);
                javaSource.append("} catch (Exception e) {");
                javaSource
                        .append("System.out.println(\"Open failed, retrying\");");
                javaSource
                        .append("selenium.stop(); selenium.start(); clearDimensions();");

                javaSource.append(windowInitFunctions);

                writeDoCommand(command, javaSource);
                javaSource.append("}");

                // Workaround for #5295
                // javaSource.append("pause(200);");
                javaSource
                        .append("doCommand(\"waitForVaadin\",new String[] {\"\",\"\"});\n");
            } else {
                writeDoCommand(command, javaSource);
            }

        }

        return javaSource.toString();
    }

    private static void writeDoCommand(Command command, StringBuilder javaSource)
            throws FileNotFoundException {
        javaSource.append("cmd.setCommand(\"" + command.getCmd()
                + "\", \"\");\n");
        javaSource.append("doCommand(\"");
        javaSource.append(command.getCmd());
        javaSource.append("\",new String[] {");

        String locator = "";
        String value = "";

        boolean first = true;
        for (String param : command.getParams()) {
            if (first) {
                /* get locator */
                locator = param.replace("\\", "\\\\");
            } else {
                /* get target */
                value = param.replace("\\", "\\\\").replace("\n", "\\n");
            }

            first = false;
        }

        javaSource.append("\"" + locator);
        javaSource.append("\",\"" + ParameterUtil.getInstance().get(value));
        javaSource.append("\"});\n");
        if (command.getCmd().endsWith("AndWait")) {
            javaSource
                    .append("doCommand(\"waitForVaadin\", new String[]{\"\", \"\"});\n");
        }
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
        cx.evaluateString(
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

    private static void includeTest(StringBuilder javaSource, String value,
            String testName) {

        // Try to load and parse test
        // Get file absolutePath/relativeToIncludingFile/search from
        // including file directory
        File target = new File(value);
        if (!target.exists()) {
            target = new File(filePath + value);
            if (!target.exists()) {
                System.out.println("File not found resorting to search.");
                target = getFile(target.getName(), new File(filePath));
                if (target != null) {
                    System.out
                            .println("Match found. Using " + target.getPath());
                }
            }
        }
        // If file not found add Assert.fail and print to System.err
        if (target == null) {
            target = new File(value);
            javaSource
                    .append("junit.framework.Assert.fail(\"Couldn't find file "
                            + target.getName() + "\");\n");
            System.err.println("Failed to append test " + target.getName());
        } else {
            // Save path to including file
            String parentPath = filePath;
            String absoluteParent = absoluteFilePath;
            // Set path to this file
            filePath = target.getParent();
            if (filePath == null) {
                filePath = "";
            } else if (!File.separator
                    .equals(filePath.charAt(filePath.length() - 1))) {
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
                        .replaceAll("\\n", "\\\\n").replace("'", "\\'")
                        .replaceAll("\\r", "");

                Context cx = Context.enter();

                Scriptable scope = cx.initStandardObjects();

                // Parse commands to a List
                List<Command> newCommands = parseTestCase(cx, scope, htmlSource);
                // Convert tests to Java
                String tests = convertTestCaseToJava(newCommands, testName);
                javaSource.append(tests);

            } catch (Exception e) {
                // if exception was caught put a assert fail to
                // inform user of error.
                System.err.println("Failed in appending test. "
                        + e.getMessage());
                if (Parameters.isDebug()) {
                    e.printStackTrace();
                }
                javaSource
                        .append("junit.framework.Assert.fail(\"Insertion of test "
                                + value
                                + " failed with "
                                + e.getMessage()
                                + "\");");
            } finally {
                Context.exit();
                // Set path back to calling file
                filePath = parentPath;
                absoluteFilePath = absoluteParent;
            }
        }
    }

    private static void appendKeyModifierDown(StringBuilder javaSource,
            boolean ctrl, boolean alt, boolean shift) {
        if (ctrl) {
            javaSource.append("selenium.keyDownNative(\"" + KeyEvent.VK_CONTROL
                    + "\");\n");
        }
        if (alt) {
            javaSource.append("selenium.keyDownNative(\"" + KeyEvent.VK_ALT
                    + "\");\n");
        }
        if (shift) {
            javaSource.append("selenium.keyDownNative(\"" + KeyEvent.VK_SHIFT
                    + "\");\n");
        }
    }

    private static void appendKeyModifierUp(StringBuilder javaSource,
            boolean ctrl, boolean alt, boolean shift) {
        if (ctrl) {
            javaSource.append("selenium.keyUpNative(\"" + KeyEvent.VK_CONTROL
                    + "\");\n");
        }
        if (alt) {
            javaSource.append("selenium.keyUpNative(\"" + KeyEvent.VK_ALT
                    + "\");\n");
        }
        if (shift) {
            javaSource.append("selenium.keyUpNative(\"" + KeyEvent.VK_SHIFT
                    + "\");\n");
        }
    }
}
