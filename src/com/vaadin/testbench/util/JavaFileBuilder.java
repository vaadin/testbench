package com.vaadin.testbench.util;

import java.awt.event.KeyEvent;
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
        appendCommandInfo("pause", delay);
        testMethodSource.append("pause(");
        testMethodSource.append(replaceParameters(delay));
        testMethodSource.append(");\n");
        appendCode("waitForVaadin();\n");
    }

    public void appendCommandInfo(String command, String value) {
        // setCommand assumes second parameter is not null
        if (value == null) {
            value = "";
        }

        testMethodSource.append("cmd.setCommand(");
        testMethodSource.append(quotedSafeParameterString(command));
        testMethodSource.append(", ");
        testMethodSource.append(quotedSafeParameterString(value));
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
        appendCommandInfo(command, replaceParameters(value));
        testMethodSource.append("doCommand(");
        testMethodSource.append(quotedSafeParameterString(command));
        testMethodSource.append(",new String[] {");

        if (locator != null) {
            testMethodSource.append(quotedSafeParameterString(locator));
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
        testMethodSource.append("});\n");

        // if (command.endsWith("AndWait"))
        if (!command.equals("close")) {
            appendCode("waitForVaadin();\n");
        }
    }

    private String replaceParameters(String value) {
        // Replace parameters
        value = ParameterUtil.translate(value);
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

        return string;
    }

    public void appendScreenshot(double errorTolerance, String imageIdentifier) {
        appendCommandInfo("screenCapture", imageIdentifier);
        testMethodSource.append("validateScreenshot(");
        testMethodSource.append(quotedSafeParameterString(testName));
        testMethodSource.append(", " + errorTolerance + ", ");
        testMethodSource
                .append(quotedSafeParameterString(replaceParameters(imageIdentifier)));
        testMethodSource.append(");\n");
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
        testMethodSource.append("try {\n");
        appendCommand(command);
        testMethodSource.append("} catch (Exception e) {\n");
        testMethodSource
                .append("System.out.println(\"Open failed, retrying\");\n");
        testMethodSource.append("selenium.stop();\n");
        testMethodSource.append("selenium.start();\n");
        testMethodSource.append("clearDimensions();\n");

        // Use true here as screenshot is not correctly set before this.
        // Tests could probably always be run in maximized mode anyway.
        testMethodSource.append(getWindowInitFunctions(true));
        testMethodSource.append("cmd.reduceCommandNumber();\n");
        appendCommand(command);
        testMethodSource.append("}\n");
    }

    public void appendCommand(Command command) {
        appendCommand(command.getCmd(), command.getLocator(), command
                .getValue());
    }

    public void appendMouseClick(String locator, String value) {
        appendCommandInfo("mouseClick", value);
        testMethodSource.append("doMouseClick(");
        testMethodSource.append(quotedSafeParameterString(locator));
        testMethodSource.append(",");
        testMethodSource
                .append(quotedSafeParameterString(replaceParameters(value)));
        testMethodSource.append(");\n");
        appendCode("waitForVaadin();\n");
    }

    public byte[] getTestMethodWrapper() {
        /* Create a test method for the browser. */
        StringBuilder browserInit = new StringBuilder();
        browserInit.append("public void test"
                + TestConverter.getSafeName(browserIdentifier)
                + "() throws Throwable{\n");

        browserInit.append("setBrowserIdentifier(\"" + browserIdentifier
                + "\");\n");
        browserInit.append("super.setUp();\n");

        browserInit.append(getTestMethodJavaName(testName) + "();");
        browserInit.append("\n}\n\n");

        return browserInit.toString().getBytes();
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
            hasScreenshots = true;
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
        String footer = TEST_METHOD_DEFINITION_END;
        footer = footer.replace("{testName}", testName);

        if (hasScreenshots) {
            return softAsserts + footer;
        }
        return "}catch(Throwable e){\nthrow new java.lang.AssertionError(cmd.getInfo() + \". Failure message = \" + e.getMessage());\n}\n"
                + footer;
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

        String methodHeader = testCaseHeader + currentCommand;

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
