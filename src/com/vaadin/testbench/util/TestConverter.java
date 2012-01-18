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

    // Flags to determine what to do during conversion.
    private static boolean isOpera = false, isSafari = false, isChrome = false;

    // Path to file being converted.
    private static String filePath = "";
    private static String absoluteFilePath = "";

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: " + TestConverter.class.getName()
                    + " <output directory> <browsers> <html test files>");
            System.exit(1);
        }
        try {
            // init ParameterUtil
            ParameterUtil.init();
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
                    String className = getSafeName(testName);
                    JavaFileBuilder builder = new JavaFileBuilder(testName,
                            className, browser);

                    System.out.println("Generating test " + testName + " for "
                            + browser + " in " + builder.getPackageName());

                    // Create a java file for the test
                    out = createJavaFileForTest(testName,
                            builder.getPackageName(), browser, outputDirectory);

                    out.write(builder.getJavaHeader());
                    out.write(builder.getTestMethodWrapper());

                    try {
                        addCommandsToTestMethod(builder,
                                getCommandsFromFile(filename));
                        out.write(builder.getTestMethodSource().getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Write the footer to the browser test class.
                    out.write(builder.getJavaFooter().getBytes());
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

    private static String getTestInputFilename(String TestBenchHTMLFile)
            throws FileNotFoundException, IOException {

        File testFile = new File(TestBenchHTMLFile);

        if (!testFile.exists()) {
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

        return outputStream;
    }

    public static String getSafeName(String name) {
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

    private static List<Command> getCommandsFromFile(String htmlFile)
            throws IOException {
        FileInputStream fis = new FileInputStream(htmlFile);
        String htmlSource = IOUtils.toString(fis);
        fis.close();

        // Escape special characters, since they are passed to a JavaScript
        // function through a string, i.e. str = "var src ='" + htmlSource +
        // "';".
        // replace(CharSequence, CharSequence) replaces all occurrences in the
        // string, as does replaceAll(regexp, String)...
        htmlSource = htmlSource.replace("\"", "\\\"")
                .replaceAll("\\n", "\\\\n").replace("'", "\\'")
                .replaceAll("\\r", "");

        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();

            List<Command> commands = parseTestCase(cx, scope, htmlSource);
            return commands;
        } finally {
            Context.exit();
        }

    }

    private static String removeExtension(String name) {
        return name.replaceAll("\\.[^\\.]*$", "");
    }

    public static String getJavaPackageName(String testName, String browserName) {
        return testName + "." + getSafeName(browserName);
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
                + ".java"); // +
                            // getPackageDir()
                            // +
                            // File.separator

        return outputFile;
    }

    private static void addCommandsToTestMethod(JavaFileBuilder builder,
            List<Command> commands) {
        boolean firstScreenshot = true;

        for (Command command : commands) {
            if ("screenCapture".equals(command.getCmd())) {
                String imageId = command.getValue();

                if (firstScreenshot) {
                    builder.appendPause("500");
                    firstScreenshot = false;
                }

                builder.appendScreenshot(imageId);
            } else if ("pause".equalsIgnoreCase(command.getCmd())) {
                // Special case to ensure pause value is an integer

                // Value is either in the locator (Selenium compatibility) or in
                // the value
                int pause = 0;
                try {
                    pause = Integer.parseInt(command.getLocator());
                } catch (NumberFormatException e) {
                }
                if (pause <= 0) {
                    try {
                        pause = Integer.parseInt(command.getValue());
                    } catch (NumberFormatException e) {
                    }
                }
                if (pause <= 0) {
                    throw new RuntimeException("No delay given for pause");
                }
                builder.appendPause(String.valueOf(pause));
            } else if ("pressSpecialKey".equalsIgnoreCase(command.getCmd())) {
                // Special case because keys are played back differently in
                // different browsers.
                String locator = command.getLocator();
                String value = command.getValue();

                String convertedValue = convertKeyCodeOrName(value);
                boolean shift = (value.contains("shift"));
                boolean alt = (value.contains("alt"));
                boolean ctrl = (value.contains("ctrl"));

                /*
                 * Opera, Safari and GoogleChrome need the java native keypress
                 */
                if (isOpera || isSafari || isChrome) {
                    builder.appendCommandInfo("pressSpecialKey", locator, value);
                    builder.appendCode("selenium.focus(\"" + locator + "\");\n");

                    builder.appendKeyModifierDown(ctrl, alt, shift);
                    builder.appendKeyPressNative(convertedValue);
                    builder.appendKeyModifierUp(ctrl, alt, shift);
                } else {
                    builder.appendCommand("pressSpecialKey", locator,
                            convertedValue);

                }
            } else if ("mouseClick".equalsIgnoreCase(command.getCmd())) {
                // Special case because the actual command we execute vary

                builder.appendMouseClick(command.getLocator(),
                        command.getValue());

            } else if ("verifyTextPresent".equalsIgnoreCase(command.getCmd())
                    || "assertTextPresent".equalsIgnoreCase(command.getCmd())) {
                // Special case because value is in locator and not in value
                // (stupid...)
                String text = command.getLocator();
                builder.appendCommand(command.getCmd(), text, null);
            } else if ("htmlTest".equalsIgnoreCase(command.getCmd())) {
                // FIXME is this even a command? "locator" is not used for
                // anything and no command is appended
                String locator = command.getLocator().replace("\\", "\\\\");
                String value = command.getValue();

                builder.appendCode("cmd.resetCmdNr();\n");
                builder.appendCode("cmd.setFile(\"" + value + "\");\n");
                builder.appendCode("System.out.println(\"Start test " + value
                        + "\");");
            } else if ("showTooltip".equalsIgnoreCase(command.getCmd())) {
                // Special case only because of pause afterwards..
                // TODO Change to default command and add pause later on
                String locator = command.getLocator();
                String value = command.getValue();

                builder.appendCommand(command.getCmd(), locator, value);
                builder.appendPause("700");
            } else if ("includeTest".equalsIgnoreCase(command.getCmd())) {
                // Loads another test, parses the commands, converts tests and
                // adds result to this TestCase
                String value = command.getValue().replace("\\", "\\\\");

                if (value.length() == 0) {
                    System.err.println("No file defined in Value field.");
                    System.err.println("Check includeTest command in "
                            + absoluteFilePath);
                    builder.appendCode("junit.framework.Assert.fail(\"No file defined in Value field.\");\n");
                } else {
                    includeTest(builder, value);
                }
            } else if ("open".equals(command.getCmd())) {
                // Special case because we need to try open several times in IE6
                // sometimes..
                builder.appendOpen(command);
            } else if ("uploadFile".equals(command.getCmd())) {
                // process the uploadFile command.
                builder.appendUploadFile(command, filePath);
            } else {
                // Default way to handle commands
                builder.appendCommand(command);
            }

        }

    }

    private static String convertKeyCodeOrName(String value) {

        if (!isOpera && !isSafari && !isChrome) {
            // Key codes are checked for opera,safari and chrome because they
            // use the JavaRobot on keypresses and not javascript so keys need
            // to be the correct Java KeyEvent

            // FIXME: isOpera/isSafari/isChrome does not really reflect what
            // browser is used. This should be moved to AbstractVaadinTestCase
            // with proper checks.

            return value;
        }

        /* get pressed key from keyCode or name */
        if (value.contains("\\")) {
            switch (Integer.parseInt(value.substring(value.lastIndexOf("\\")))) {
            case 13:
                return "" + KeyEvent.VK_ENTER;
            case 37:
                return "" + KeyEvent.VK_LEFT;
            case 38:
                return "" + KeyEvent.VK_UP;
            case 39:
                return "" + KeyEvent.VK_RIGHT;
            case 40:
                return "" + KeyEvent.VK_DOWN;
            }
        } else if (value.contains("up")) {
            return "" + KeyEvent.VK_UP;
        } else if (value.contains("down")) {
            return "" + KeyEvent.VK_DOWN;
        } else if (value.contains("left")) {
            return "" + KeyEvent.VK_LEFT;
        } else if (value.contains("right")) {
            return "" + KeyEvent.VK_RIGHT;
        } else if (value.contains("enter")) {
            return "" + KeyEvent.VK_ENTER;
            // } else if ("BACKSPACE".equalsIgnoreCase(value)) {
            // values.append("\\\\8\"");
        } else if (value.contains("tab")) {
            return "" + KeyEvent.VK_TAB;
        }

        return null;
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

    private static void includeTest(JavaFileBuilder builder, String value) {

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
            builder.appendCode("junit.framework.Assert.fail(\"Couldn't find file "
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
                addCommandsToTestMethod(builder, newCommands);

            } catch (Exception e) {
                // if exception was caught put a assert fail to
                // inform user of error.
                System.err.println("Failed in appending test. "
                        + e.getMessage());
                if (Parameters.isDebug()) {
                    e.printStackTrace();
                }
                builder.appendCode("junit.framework.Assert.fail(\"Insertion of test "
                        + value + " failed with " + e.getMessage() + "\");");
            } finally {
                Context.exit();
                // Set path back to calling file
                filePath = parentPath;
                absoluteFilePath = absoluteParent;
            }
        }
    }

}
