package com.vaadin.testbench.util;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Date;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.util.SeleniumHTMLTestCaseParser.Command;

public class JavaFileBuilder {

    private StringBuilder testMethodSource;
    private String testName;

    private static final String TEST_METHOD_DEFINITION = "private void {testMethodName}() throws Throwable {\n";
    private static final String TEST_METHOD_DEFINITION_END = "}\n";

    // Empty setUp() is needed to prevent super.setUp from being executed in the
    // setup phase
    private static final String JAVA_HEADER = "package {package};\n\n"
            + "import com.vaadin.testbench.testcase.AbstractVaadinTestCase;\n"
            + "import java.io.IOException;\n" + "import java.io.File;\n"
            + "import javax.imageio.ImageIO;\n"
            + "import com.vaadin.testbench.util.ImageUtil;\n"
            + "import com.vaadin.testbench.util.CurrentCommand;\n"
            + "import com.vaadin.testbench.util.BrowserUtil;\n"
            + "import com.vaadin.testbench.util.BrowserVersion;\n\n"
            + "public class {class} extends AbstractVaadinTestCase {\n\n"
            + "private boolean writeScreenshots = true;\n"
            + "public void setUp(){\n}\n\n";;

    private static final String JAVA_FOOTER = "}\n";

    private String browserIdentifier;
    private boolean hasScreenshots = false;
    private String className;

    public JavaFileBuilder(String testName, String className, String browser) {
        testMethodSource = new StringBuilder();
        this.testName = testName;
        this.className = className;
        browserIdentifier = browser;

    }

    public void appendPause(String delay) {
        appendCommandInfo("pause", "", delay);
        testMethodSource.append("pause(");
        testMethodSource.append(replaceParameters(delay));
        testMethodSource.append(");\n\n");
    }

    public void appendCommandInfo(String command, String locator, String value) {
        // setCommand assumes second parameter is not null
        if (value == null) {
            value = "";
        }

        testMethodSource.append("cmd.setCommand(");
        testMethodSource.append(quotedSafeParameterString(command));
        testMethodSource.append(", ");
        testMethodSource
                .append(quotedSafeParameterString(replaceParameters(locator)));
        testMethodSource.append(", ");
        testMethodSource
                .append(quotedSafeParameterString(replaceParameters(value)));
        testMethodSource.append(");\n");

    }

    /**
     * Append a command to the file. The command and all parameters are
     * sanitized to be java safe (newlines, quotes are escaped).
     * 
     * @param command
     * @param parameters
     */
    public void appendCommand(String command, String locator, String value) {
        appendCommandInfo(command, replaceParameters(locator),
                replaceParameters(value));
        testMethodSource.append("doCommand(");
        testMethodSource.append(quotedSafeParameterString(command));
        testMethodSource.append(",new String[] {");

        if (locator != null) {
            testMethodSource
                    .append(quotedSafeParameterString(replaceParameters(locator)));
        }
        // TODO: Can locator be null and value != null. What should even happen
        // then?
        if (value != null) {
            if (locator != null) {
                testMethodSource.append(",");
            }

            testMethodSource
                    .append(quotedSafeParameterString(replaceParameters(value)));
        }
        testMethodSource.append("});\n\n");

    }

    private String replaceParameters(String value) {
        // Replace parameters
        value = ParameterUtil.translate(value, browserIdentifier);
        // Replace current day functions
        value = DateUtil.replaceDateFunctions(value, new Date());

        return value;
    }

    /**
     * @param string
     *            String to make safe. Must not be null.
     * @return
     */
    private static String makeJavaStringSafe(String string) {
        // The string must not contain quotes or newlines as this will cause
        // compilation errors
        string = string.replace("\\", "\\\\");
        string = string.replace("\n", "\\n");
        string = string.replace("\"", "\\\"");

        return string;
    }

    public void appendScreenshot(String imageIdentifier) {
        appendCommandInfo("screenCapture", "", imageIdentifier);
        testMethodSource.append("validateScreenshot(");
        testMethodSource.append(quotedSafeParameterString(testName));
        testMethodSource.append(", ");
        testMethodSource
                .append(quotedSafeParameterString(replaceParameters(imageIdentifier)));
        testMethodSource.append(",writeScreenshots);\n\n");
        hasScreenshots = true;
    }

    /**
     * Makes the string safe to be added as a parameter to a java function. The
     * return value is quoted if not null and any quotes in the supplied string
     * are escaped.
     * 
     * @param parameter
     *            The parameter to quote and make safe.
     * @return Parameter safe to be added as a parameter to a java function.
     */
    private String quotedSafeParameterString(String parameter) {
        if (parameter == null) {
            return "null";
        } else {
            return "\"" + makeJavaStringSafe(parameter) + "\"";
        }
    }

    public void appendCode(String string) {
        testMethodSource.append(string);
    }

    public void appendKeyModifierDown(boolean ctrl, boolean alt, boolean shift) {
        if (ctrl) {
            testMethodSource.append("selenium.keyDownNative(\""
                    + KeyEvent.VK_CONTROL + "\");\n");
        }
        if (alt) {
            testMethodSource.append("selenium.keyDownNative(\""
                    + KeyEvent.VK_ALT + "\");\n");
        }
        if (shift) {
            testMethodSource.append("selenium.keyDownNative(\""
                    + KeyEvent.VK_SHIFT + "\");\n");
        }
    }

    public void appendKeyModifierUp(boolean ctrl, boolean alt, boolean shift) {
        if (ctrl) {
            testMethodSource.append("selenium.keyUpNative(\""
                    + KeyEvent.VK_CONTROL + "\");\n");
        }
        if (alt) {
            testMethodSource.append("selenium.keyUpNative(\"" + KeyEvent.VK_ALT
                    + "\");\n");
        }
        if (shift) {
            testMethodSource.append("selenium.keyUpNative(\""
                    + KeyEvent.VK_SHIFT + "\");\n");
        }
    }

    /**
     * Add native keypress command.
     * 
     * @param value
     *            ???. Must not be null.
     */
    public void appendKeyPressNative(String value) {
        testMethodSource.append("selenium.keyPressNative("
                + quotedSafeParameterString(value) + ");\n");

    }

    public void appendOpen(Command command) {
        appendCommandInfo(command.getCmd(),
                replaceParameters(command.getLocator()),
                replaceParameters(command.getValue()));
        testMethodSource.append("open(\"" + command.getLocator() + "\");\n\n");
    }

    public void appendCommand(Command command) {
        appendCommand(command.getCmd(), command.getLocator(),
                command.getValue());
    }

    public void appendMouseClick(String locator, String value) {
        appendCommandInfo("mouseClick", locator, value);
        testMethodSource.append("doMouseClick(");
        testMethodSource
                .append(quotedSafeParameterString(replaceParameters(locator)));
        testMethodSource.append(",");
        testMethodSource
                .append(quotedSafeParameterString(replaceParameters(value)));
        testMethodSource.append(");\n\n");
    }

    public void appendUploadFile(Command command, String filePath) {
        appendCommandInfo(command.getCmd(), command.getLocator(),
                command.getValue());
        testMethodSource.append("doUploadFile(");
        testMethodSource
                .append(quotedSafeParameterString(replaceParameters(command
                        .getLocator())));
        testMethodSource.append(",");
        testMethodSource
                .append(quotedSafeParameterString(replaceParameters(getFileName(
                        command.getValue(), filePath))));
        testMethodSource.append(");\n\n");
    }

    private String getFileName(String value, String filePath) {
        String fileWithPath = value;

        String filename = null;
        if (fileWithPath.indexOf(":\\") == 1) {
            // This is probably a windows path
            filename = fileWithPath
                    .substring(fileWithPath.lastIndexOf('\\') + 1);
        } else {
            // Unix-style path
            filename = fileWithPath
                    .substring(fileWithPath.lastIndexOf('/') + 1);
        }

        File localFile = new File(filePath + filename);
        if (localFile.exists()) {
            fileWithPath = localFile.getAbsolutePath();
        }

        return fileWithPath;
    }

    public byte[] getTestMethodWrapper() {
        /* Create a test method for the browser. */
        StringBuilder browserInit = new StringBuilder();
        browserInit.append("public void test"
                + TestConverter.getSafeName(browserIdentifier)
                + "() throws Throwable{\n");

        browserInit.append("startBrowser(\"" + browserIdentifier + "\");\n");

        if (Parameters.gerMaxTestRetries() > 0) {
            retryHeader(browserInit);
        }

        browserInit.append(getTestMethodJavaName(testName) + "();\n");

        if (Parameters.gerMaxTestRetries() > 0) {
            retryFooter(browserInit);
        }
        browserInit.append("}\n\n");

        return browserInit.toString().getBytes();
    }

    private void retryHeader(StringBuilder browserInit) {
        browserInit.append("writeScreenshots=false;\n");
        browserInit.append("for (int i = 0; i < "
                + Parameters.gerMaxTestRetries() + "; i++) {\n");
        browserInit.append("try {\n");
        // Only write screenshots for the last iteration to avoid extra images
        // in the error folder
        browserInit.append("if (i == " + (Parameters.gerMaxTestRetries() - 1)
                + ")\n {\nwriteScreenshots=true;\n}\n");
    }

    private void retryFooter(StringBuilder browserInit) {
        browserInit
                .append("System.out.println(\"Retried: \" + i + \" times\");\n");
        browserInit.append("break;\n");
        browserInit.append("} catch (Throwable t) {\n");
        browserInit.append("if (i < " + (Parameters.gerMaxTestRetries() - 1)
                + ") {\n");
        browserInit.append("selenium.stop();\n");
        browserInit.append("selenium.start();\n");
        browserInit.append("resetImageNumber();\n");
        browserInit.append("}else{\n");
        browserInit
                .append("System.out.println(\"Retried: \" + i + \" times\");\n");
        browserInit.append("throw t;\n");
        browserInit.append("}\n}\n}\n");
    }

    public String getTestName() {
        return testName;
    }

    private String getTestMethodDefinition() {
        String header = TEST_METHOD_DEFINITION;
        header = header.replace("{testMethodName}",
                getTestMethodJavaName(getTestName()));

        return header;
    }

    private static String getTestMethodJavaName(String testName) {
        return "internal_" + testName;
    }

    private String getTestMethodDefinitionEnd() {
        String footer = TEST_METHOD_DEFINITION_END;
        footer = footer.replace("{testName}", testName);

        String catchClause = "}catch(Throwable e){\n";
        if (Parameters.isCaptureScreenshotOnFailure()) {
            hasScreenshots = true;
            catchClause += "if (writeScreenshots) {\ncreateFailureScreenshot(\""
                    + testName + "\");\n}\n";
        }
        catchClause += "throw new java.lang.AssertionError(cmd.getInfo() + \"\\n Message: \" + e.getMessage()+\"\\nRemote control: \"+getRemoteControlName());\n}\n";

        // adding the softAssert so creating reference images throws a assert
        // failure at end of test
        String softAsserts = "handleSoftErrors();\n";

        return catchClause + softAsserts + footer;
    }

    /**
     * Returns the java source for the test method, including method definition.
     * 
     * @return
     */
    public String getTestMethodSource() {
        String testCaseHeader = getTestMethodDefinition();
        String currentCommand = "CurrentCommand cmd = new CurrentCommand(\""
                + getTestName() + "\");\n";
        String setTestName = "setTestName("
                + quotedSafeParameterString(getTestName()) + ");\n";
        String methodHeader = testCaseHeader + currentCommand + setTestName;

        // Add canvas size initialization in the case a screenshot is wanted
        if (hasScreenshots) {
            methodHeader += getWindowInitFunctions(true);
        }

        methodHeader += "try{\n";

        String methodFooter = getTestMethodDefinitionEnd();

        return methodHeader + testMethodSource.toString() + methodFooter;
    }

    public String getPackageName() {
        return TestConverter.getJavaPackageName(testName, browserIdentifier);
    }

    public byte[] getJavaHeader() {
        String header = JAVA_HEADER;
        header = header.replace("{class}", className);
        header = header.replace("{package}", getPackageName());

        return header.getBytes();
    }

    public String getJavaFooter() {
        return JAVA_FOOTER;
    }

    public static String getWindowInitFunctions(boolean hasScreenshots) {
        final String windowInitFunctions = "setupWindow(#hasScreenshots#);\n";

        return windowInitFunctions.replaceAll("#hasScreenshots#",
                hasScreenshots ? "true" : "false");
    }

}
