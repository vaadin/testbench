package com.vaadin.testbench.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumHTMLTestCaseParser {

    /* From html.js: this.options.commandLoadPattern */
    private final static String commandLoadRegexp = "<tr\\s*[^>]*>"
            + "\\s*(<!--[\\d\\D]*?-->)?"
            + "\\s*<td\\s*[^>]*>\\s*([\\w]*?)\\s*</td>"
            + "\\s*<td\\s*[^>]*>([\\d\\D]*?)</td>"
            + "\\s*(<td\\s*/>|<td\\s*[^>]*>([\\d\\D]*?)</td>)"
            + "\\s*</tr>\\s*";

    private final static Pattern commandLoadPatter = Pattern.compile(
            commandLoadRegexp, Pattern.CASE_INSENSITIVE);

    /**
     * This is based on the javascript version found in html.js. Might be too
     * simplistic for some cases.
     * 
     * @param testCase
     * @return
     */
    public static List<Command> parseTestCase(String testCase) {
        List<Command> commands = new ArrayList<Command>();
        Matcher matcher = commandLoadPatter.matcher(testCase);

        while (matcher.find()) {
            if (matcher.groupCount() != 5) {
                continue;
            }

            // String comment = matcher.group(1);
            String command = matcher.group(2);
            String target = matcher.group(3);
            String value = matcher.group(5);

            Command cmd = new Command(command, target, value);
            commands.add(cmd);
        }

        return commands;
    }

    public static class Command {

        private String cmd;
        private String locator;
        private String value;

        private void init(String cmd, String locator, String value) {
            this.cmd = cmd;
            this.locator = locator;
            this.value = value;
        }

        public Command(String command, String target, String value) {
            if (value == null || value.equals("")) {
                init(command, target, null);
            } else {
                init(command, target, value);
            }
        }

        public String getLocator() {
            return locator;
        }

        public String getCmd() {
            return cmd;
        }

        public String getValue() {
            return value;
        }

        public int getIntValue() {
            String value = getValue();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Value for command " + cmd
                        + " is \"" + value + "\" and is not an integer.");
            }
        }

    }
}
