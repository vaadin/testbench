package org.openqa.selenium.server.browserlaunchers;

import org.apache.tools.ant.taskdefs.condition.Os;

/**
 * Helper methods related to runtime operating system
 */
public class SystemUtils {

    public static String libraryPathEnvironmentVariable() {
        if (WindowsUtils.thisIsWindows()) {
            return WindowsUtils.getExactPathEnvKey();
        }
        if (Os.isFamily("mac")) {
            return "XXX_DYLD_LIBRARY_PATH";
        }
        // TODO other linux?
        return "LD_LIBRARY_PATH";
    }

}