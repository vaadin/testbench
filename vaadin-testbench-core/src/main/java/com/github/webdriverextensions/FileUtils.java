package com.github.webdriverextensions;

import java.io.File;

public class FileUtils {

    private FileUtils() {
    }

    public static void makeExecutable(String path) {
        if (path == null) {
            return;
        }
        File file = new File(path);
        if (file.exists() && !file.canExecute()) {
            file.setExecutable(true);
        }
    }
}
