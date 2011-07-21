package com.vaadin.testbench.util;

import java.io.File;

/**
 * Does the same as TestConverter, but accepts a folder as a root from which
 * tests are found. Somewhat more usable than listing html files separately.
 */
public class DirectoryTestConverter {

    public static void main(String[] args) throws Exception {
        String dir = args[2];
        File file = new File(dir);
        File[] listFiles = file.listFiles();
        String[] fakeArgs = args.clone();
        convertTests(listFiles, fakeArgs);

    }

    private static void convertTests(File[] listFiles, String[] fakeArgs)
            throws Exception {
        for (File file2 : listFiles) {
            if (file2.isDirectory()) {
                convertTests(file2.listFiles(), fakeArgs);
            } else if (file2.getName().endsWith(".html")) {
                String path = file2.getPath();
                fakeArgs[fakeArgs.length - 1] = path;
                try {
                    TestConverter.main(fakeArgs);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }
    }

}
