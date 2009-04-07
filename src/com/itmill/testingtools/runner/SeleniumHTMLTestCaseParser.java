package com.itmill.testingtools.runner;

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

        private void init(String cmd, String[] params) {
            this.cmd = cmd;
            this.params = params;
        }

        public Command(String command, String target, String value) {
            if (value == null || value.equals("")) {
                init(command, new String[] { target });
            } else {
                init(command, new String[] { target, value });
            }
        }

        private String cmd;
        private String[] params;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String[] getParams() {
            return params;
        }

        public void setParams(String[] params) {
            this.params = params;
        }

    }
}
