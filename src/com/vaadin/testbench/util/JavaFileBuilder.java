package com.vaadin.testbench.util;

import java.awt.event.KeyEvent;
import java.util.Date;

import com.vaadin.testbench.util.SeleniumHTMLTestCaseParser.Command;

public class JavaFileBuilder {

    private StringBuilder javaSource;
    private String testName;

    // private String browser;

    public JavaFileBuilder(String testName) {
        // String testName, String browser) {
        // }
        javaSource = new StringBuilder();
        this.testName = testName;
        // this.browser = browser;

    }

    public void appendPause(int i) {
        javaSource.append("pause(");
        javaSource.append(i);
        javaSource.append(");\n");
    }

    public void appendCommandInfo(String command, String value) {
        // setCommand assumes second parameter is not null
        if (value == null) {
            value = "";
        }

        javaSource.append("cmd.setCommand(\"");
        javaSource.append(command);
        javaSource.append("\", \"");
        javaSource.append(makeJavaStringSafe(value));
        javaSource.append("\");\n");

    }

    /**
     * Append a command to the file. The command and all parameters are
     * sanitized to be java safe (newlines, quotes are escaped).
     * 
     * @param command
     * @param parameters
     */
    public void appendCommand(String command, String locator, String value) {
        javaSource.append("doCommand(\"");
        javaSource.append(makeJavaStringSafe(command));
        javaSource.append("\",new String[] {");

        if (locator != null) {
            javaSource.append("\"");
            javaSource.append(makeJavaStringSafe(replaceParameters(locator)));
            javaSource.append("\"");
        }
        // TODO: Can locator be null and value != null. What should even happen
        // then?
        if (value != null) {
            if (locator != null) {
                javaSource.append(",\"");
            }

            javaSource.append(makeJavaStringSafe(replaceParameters(value)));
            javaSource.append("\"");
        }
        javaSource.append("});\n");

    }

    private String replaceParameters(String value) {
        // Replace parameters
        value = ParameterUtil.translate(value);
        // Replace current day functions
        value = DateUtil.replaceDateFunctions(value, new Date());

        return value;
    }

    private static String makeJavaStringSafe(String string) {
        // The string must not contain quotes or newlines as this will cause
        // compilation errors
        string = string.replace("\\", "\\\\");
        string = string.replace("\n", "\\n");

        return string;
    }

    public void appendScreenshot(String testName, double errorTolerance,
            String imageIdentifier) {
        javaSource.append("validateScreenshot(\""
                + makeJavaStringSafe(testName) + "\", " + errorTolerance
                + ", \"" + makeJavaStringSafe(imageIdentifier) + "\");\n");

    }

    public void appendCode(String string) {
        javaSource.append(string);

    }

    public void appendKeyModifierDown(boolean ctrl, boolean alt, boolean shift) {
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

    public void appendKeyModifierUp(boolean ctrl, boolean alt, boolean shift) {
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

    public void appendKeyPressNative(String value) {
        javaSource.append("selenium.keyPressNative(\""
                + makeJavaStringSafe(value) + "\");\n");

    }

    public void appendOpen(Command command) {
        javaSource.append("try {");
        appendCommand(command);
        javaSource.append("} catch (Exception e) {\n");
        javaSource.append("System.out.println(\"Open failed, retrying\");\n");
        javaSource.append("selenium.stop();\n");
        javaSource.append("selenium.start();\n");
        javaSource.append("clearDimensions();\n");

        // Use true here as screenshot is not correctly set before this.
        // Tests could probably always be run in maximized mode anyway.
        javaSource.append(TestConverter.getWindowInitFunctions(true));
        appendCommand(command);
        javaSource.append("}\n");
    }

    public void appendCommand(Command command) {

        String locator = command.getLocator();
        locator = locator.replace("\\", "\\\\"); // What is this for ?

        String value = command.getValue();

        appendCommand(command.getCmd(), locator, value);

        // if (command.getCmd().endsWith("AndWait")) {
        appendCode("waitForVaadin();\n");
        // }
    }

    public String getJavaSource() {
        return javaSource.toString();
    }

    public String getPackageName(String browser) {
        return TestConverter.getJavaPackageName(testName, browser);
    }

    public byte[] getJavaHeader(String browser) {
        return TestConverter.getJavaHeader(TestConverter.getSafeName(testName),
                getPackageName(browser));
    }

    public void appendMouseClick(String locator, String value) {
        javaSource.append("doMouseClick(\"");
        javaSource.append(makeJavaStringSafe(locator));
        javaSource.append("\",");
        if (value != null) {
            javaSource.append("\"");
            javaSource.append(makeJavaStringSafe(value));
            javaSource.append("\"");
        } else {
            javaSource.append("null");
        }
        javaSource.append(");\n");

        javaSource.append("});\n");
    }

    public byte[] getBrowserTestMethod(String browser) {
        /* Create a test method for the browser. */
        StringBuilder browserInit = new StringBuilder();
        browserInit
                .append("public void test" + TestConverter.getSafeName(browser)
                        + "() throws Throwable{\n");

        browserInit.append("setBrowserIdentifier(\"" + browser + "\");\n");
        browserInit.append("super.setUp();\n");

        browserInit.append(TestConverter.getTestMethodName(testName) + "();");
        browserInit.append("\n}\n\n");

        return browserInit.toString().getBytes();
    }

}
