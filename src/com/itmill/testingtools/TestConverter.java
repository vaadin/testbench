package com.itmill.testingtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.itmill.testingtools.runner.SeleniumHTMLTestCaseParser.Command;

public class TestConverter {

    private static final String JAVA_HEADER = "package {package};\n" + "\n"
            + "import com.itmill.testingtools.runner.AbstractTestCase;\n"
            + "\n" + "public class {class} extends AbstractTestCase {\n" + "\n"
            + "public void test{class}() throws Exception {\n" + "\n";

    private static final String JAVA_FOOTER = "}\n" + "}\n";

    private static final String PACKAGE_DIR = "itmill/tests";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: " + TestConverter.class.getName()
                    + " <output directory> <java source files>");
            System.exit(1);
        }

        String outputDirectory = args[0];
        System.out.println("Using output directory: " + outputDirectory);
        createIfNotExists(outputDirectory);
        createIfNotExists(outputDirectory + File.separator + PACKAGE_DIR);

        for (int i = 1; i < args.length; i++) {
            String filename = args[i];
            try {
                convertFile(filename, outputDirectory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    private static void convertFile(String htmlFile, String outputDirectory)
            throws FileNotFoundException, IOException {
        File outputFile = getJavaFile(htmlFile, outputDirectory);
        System.out.println("Converting " + htmlFile + " into "
                + outputFile.getAbsolutePath());
        String className = removeExtension(outputFile.getName());
        // outputFile.createNewFile();

        String htmlSource = IOUtils.toString(new FileInputStream(htmlFile));
        htmlSource = htmlSource.replace("\"", "\\\"")
                .replaceAll("\\n", "\\\\n");

        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();

            List<Command> commands = parseTestCase(cx, scope, htmlSource);
            String javaFile = convertTestCaseToJava(commands);

            FileOutputStream outputStream = new FileOutputStream(outputFile);

            outputStream.write(getJavaHeader(className));
            outputStream.write(javaFile.getBytes());
            outputStream.write(getJavaFooter());

            outputStream.close();

            System.out.println("Done");
        } finally {
            Context.exit();
        }

    }

    private static String removeExtension(String name) {
        return name.replaceAll("\\.[^\\.]*$", "");
    }

    private static byte[] getJavaHeader(String className) {
        String header = JAVA_HEADER;
        header = header.replace("{class}", className);
        header = header.replace("{package}", getPackageName());

        return header.getBytes();
    }

    private static byte[] getJavaFooter() {
        return JAVA_FOOTER.getBytes();
    }

    private static File getJavaFile(String htmlFilename, String outputDirectory) {

        File file = new File(htmlFilename);
        String filename = removeExtension(file.getName());

        File outputFile = new File(outputDirectory + File.separator
                + getPackageDir() + File.separator + filename + ".java");

        return outputFile;
    }

    private static String convertTestCaseToJava(List<Command> commands)
            throws IOException {
        // cx.evaluateString(scope, "function load(a){}", "dummy-load", 1,
        // null);

        // loadScript("tools.js", scope, cx);
        // loadScript("formatCommandOnlyAdapter.js", scope, cx);
        // loadScript("remoteControl.js", scope, cx);
        // loadScript("itmill-java-rc.js", scope, cx);
        // loadScript("testCase.js", scope, cx);

        // cx.evaluateString(scope, "var result = format(testCase);",
        // "javaFormat", 1, null);

        // cx.evaluateString(scope,
        // "var testCase =  new TestCase();parse(testCase,src);",
        // "testCaseDef", 1, null);
        // Object javaSource = scope.get("result", scope);

        StringBuilder javaSource = new StringBuilder();

        for (Command command : commands) {
            javaSource.append("testBase.getSelenium().doCommand(\"");
            javaSource.append(command.getCmd());
            javaSource.append("\",new String[] {");

            boolean first = true;
            for (String param : command.getParams()) {
                if (!first) {
                    javaSource.append(",");
                }
                first = false;

                javaSource.append("\"");
                javaSource.append(param.replace("\"", "\\\"").replaceAll("\\n",
                        "\\\\n"));
                javaSource.append("\"");
            }
            javaSource.append("});\n");
            // javaSource.append("\n");
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
