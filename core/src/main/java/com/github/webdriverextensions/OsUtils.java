package com.github.webdriverextensions;

import org.openqa.selenium.Platform;

public class OsUtils {

    private OsUtils() {
    }

    public static boolean isWindows() {
        return Platform.getCurrent().is(Platform.WINDOWS);
    }

    public static boolean isWindows10() {
        return Platform.getCurrent().is(Platform.WIN10);
    }

    public static boolean isMac() {
        return Platform.getCurrent().is(Platform.MAC);
    }

    public static boolean isLinux() {
        return Platform.getCurrent().is(Platform.LINUX);
    }

    public static boolean isCurrentPlatform(String platform) {
        try {
            return Platform.getCurrent().is(Platform.valueOf(platform));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean is64Bit() {
        return System.getProperty("sun.arch.data.model",
                System.getProperty("com.ibm.vm.bitmode")
        ).equals("64");
    }
}
