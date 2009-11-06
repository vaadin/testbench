package com.vaadin.testbench.util;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private static final String JAVA_HEADER = "package {package};\n" + "\n"
            + "import com.vaadin.testbench.testcase.AbstractVaadinTestCase;\n"
            + "\n" + "public class {class} extends AbstractVaadinTestCase {\n"
            + "\n" + "public void setUp() throws Exception {\n"
            + "        setBrowser({browser});\n super.setUp();\n" + "}" + "\n";

    private static final String TEST_METHOD_HEADER = "public void test{testName}() throws Exception {\n";
    private static final String TEST_METHOD_FOOTER = "}\n";

    private static final String JAVA_FOOTER = "}\n";

    private static final String PACKAGE_DIR = "com/vaadin/automatedtests";

    private static boolean screenshot = false;
    private static boolean firstScreenshot = true;
    private static boolean isOpera = false, isSafari = false, isChrome = false;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: " + TestConverter.class.getName()
                    + " <output directory> <browsers> <html test files>");
            System.exit(1);
        }

        String outputDirectory = args[0];
        String browserString = args[1];
        String browsers[] = browserString.split(",");

        System.out.println("Using output directory: " + outputDirectory);
        createIfNotExists(outputDirectory);
        createIfNotExists(outputDirectory + File.separator + PACKAGE_DIR);

        for (String browser : browsers) {
            System.out.println("Generating tests for " + browser);
            isOpera = isSafari = isChrome = false;

            OutputStream out = null;
            try {
                String browserId = knownBrowsers.get(browser.toLowerCase());
                if (browserId == null) {
                    // System.err.println("Warning: Unknown browser: " +
                    // browser);
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

                // Create a java file for holding all tests for this browser
                out = createJavaFileForBrowser(browser, outputDirectory);

                // Write the tests to the java file
                for (int i = 2; i < args.length; i++) {
                    String filename = args[i];
                    try {
                        String testMethod = createTestMethod(filename,
                                getTestName(filename));
                        out.write(testMethod.getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private static void writeJavaFooter(OutputStream out) throws IOException {
        String footer = getJavaFooter();
        out.write(footer.getBytes());

    }

    private static String getTestName(String filename) {
        File f = new File(filename);
        String testName = removeExtension(f.getName());

        // Sanitize so it is a valid method name
        testName = testName.replaceAll("[^0-9a-zA-Z_]", "_");

        return testName;
    }

    private static OutputStream createJavaFileForBrowser(String browser,
            String outputDirectory) throws IOException {
        File outputFile = getJavaFile(browser, outputDirectory);
        System.out.println("Creating " + outputFile + " for " + browser
                + " tests");
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        outputStream.write(getJavaHeader(getSafeName(browser), browser));

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
        // Add these in the case a screenshot is wanted
        String windowFunctions = "doCommand(\"windowMaximize\", new String[] { \"\" });\n"
                + "doCommand(\"windowFocus\", new String[] { \"\" });\n"
                + "getCanvasPosition();\n";

        if (screenshot) {
            return testCaseHeader + windowFunctions + testCaseBody
                    + testCaseFooter;
        }

        return testCaseHeader + testCaseBody + testCaseFooter;
    }

    private static String removeExtension(String name) {
        return name.replaceAll("\\.[^\\.]*$", "");
    }

    private static String getTestCaseHeader(String testName) {
        String header = TEST_METHOD_HEADER;
        header = header.replace("{testName}", testName);

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
        String footer = TEST_METHOD_FOOTER;
        footer = footer.replace("{testName}", testName);

        if (screenshot) {
            return softAsserts + footer;
        }
        return footer;
    }

    private static byte[] getJavaHeader(String className, String browser) {
        String header = JAVA_HEADER;
        header = header.replace("{class}", className);
        header = header.replace("{package}", getPackageName());

        String browserId = knownBrowsers.get(browser.toLowerCase());
        if (browserId == null) {
            browserId = browser;
        }

        header = header.replace("{browser}", "\"" + browserId + "\"");

        return header.getBytes();
    }

    private static String getJavaFooter() {
        return JAVA_FOOTER;
    }

    private static File getJavaFile(String browser, String outputDirectory) {
        String filenameSafeBrowser = getSafeName(browser);

        File file = new File(filenameSafeBrowser);
        String filename = removeExtension(file.getName());

        File outputFile = new File(outputDirectory + File.separator
                + getPackageDir() + File.separator + filename + ".java");

        return outputFile;
    }

    private static String convertTestCaseToJava(List<Command> commands,
            String testName) {
        StringBuilder javaSource = new StringBuilder();

        for (Command command : commands) {
            if (command.getCmd().equals("screenCapture")) {

                String identifier = "";
                boolean first = true;
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

                javaSource.append("validateScreenshot(\"" + testName
                        + "\", 0.025, \"" + identifier + "\");\n");
                screenshot = true;
            } else if (command.getCmd().equalsIgnoreCase("pause")) {
                String identifier = "";
                boolean first = true;
                for (String param : command.getParams()) {
                    if (first) {
                        identifier = param;
                    }
                    first = false;
                }
                javaSource.append("pause(" + identifier + ");\n");
                // } else if
                // (command.getCmd().equalsIgnoreCase("enterCharacter")) {
                // String locator = "";
                // String characters = "";
                // boolean first = true;
                // for (String param : command.getParams()) {
                // if (first) {
                // /* get locator */
                // locator = param.replace("\\", "\\\\");
                // } else {
                // /* get characters */
                // characters = param;
                // }
                //
                // first = false;
                // }
                // javaSource.append("selenium.type(\"" + locator + "\", \""
                // + characters + "\");\n");
                // if (characters.length() > 1) {
                // /* Add a keyDown keyUp for each character */
                // for (int i = 0; i < characters.length(); i++) {
                // javaSource.append("selenium.keyDown(\"" + locator
                // + "\", \"" + characters.charAt(i) + "\");\n");
                // javaSource.append("selenium.keyUp(\"" + locator
                // + "\", \"" + characters.charAt(i) + "\");\n");
                // }
                // } else {
                // javaSource.append("selenium.keyDown(\"" + locator
                // + "\", \"" + characters + "\");\n");
                // javaSource.append("selenium.keyUp(\"" + locator + "\", \""
                // + characters + "\");\n");
                // }
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

                /* Opera, Safari and GoogleChrome need the java native keypress */
                if (isOpera || isSafari || isChrome) {
                    javaSource
                            .append("selenium.focus(\"" + location + "\");\n");
                    javaSource.append("selenium.keyPressNative(\"" + value
                            + "\");\n");
                } else {
                    /* if enter VK_ENTER will give 10 instead of 13 */
                    if (Integer.parseInt(value) == 10) {
                        value = "13";
                    }
                    javaSource.append("selenium.keyDown(\"" + location
                            + "\", \"\\\\" + value + "\");\n");
                    javaSource.append("selenium.keyPress(\"" + location
                            + "\", \"\\\\" + value + "\");\n");
                    javaSource.append("selenium.keyUp(\"" + location
                            + "\", \"\\\\" + value + "\");\n");
                }

            } else if (command.getCmd().equalsIgnoreCase("mouseClick")
                    || command.getCmd().equalsIgnoreCase("closeNotification")) {
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
                javaSource
                        .append("selenium.mouseDownAt(\"" + values + "\");\n");
                javaSource.append("selenium.mouseUpAt(\"" + values + "\");\n");
                if (!isOpera) {
                    javaSource.append("selenium.click(\"" + firstParam
                            + "\");\n");
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

                javaSource.append("assertTrue(\"Could not find: " + identifier
                        + "\", selenium.isTextPresent(\"" + identifier
                        + "\"));\n");
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

                javaSource.append("doCommand(\"showTooltip\",new String[] {\""
                        + locator + "\", \"" + value + "\"});\n");
                javaSource.append("pause(700);\n");
            } else if (command.getCmd().equalsIgnoreCase("appendToTest")) {
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
                        /* get characters */
                        value = param;
                    }

                    first = false;
                }

                try {
                    // open and read target file
                    FileInputStream fis = new FileInputStream(value);
                    String htmlSource = IOUtils.toString(fis);
                    fis.close();

                    // sanitize source
                    htmlSource = htmlSource.replace("\"", "\\\"").replaceAll(
                            "\\n", "\\\\n").replace("'", "\\'").replaceAll(
                            "\\r", "");

                    Context cx = Context.enter();
                    try {
                        Scriptable scope = cx.initStandardObjects();

                        // Parse commands to a List
                        List<Command> newCommands = parseTestCase(cx, scope,
                                htmlSource);
                        // Convert tests to Java
                        String tests = convertTestCaseToJava(newCommands,
                                testName);
                        javaSource.append(tests);

                    } finally {
                        Context.exit();
                    }
                } catch (Exception e) {
                    System.err.println("Failed in appending test. "
                            + e.getMessage());
                }

            } else {
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

    private static String getPackageName() {
        return PACKAGE_DIR.replaceAll("/", ".");
    }

    private static String getPackageDir() {
        return PACKAGE_DIR.replace("/", File.separator);
    }
}
