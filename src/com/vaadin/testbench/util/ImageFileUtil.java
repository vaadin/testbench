package com.vaadin.testbench.util;

import java.io.File;

import com.vaadin.testbench.Parameters;

public class ImageFileUtil {

    public static final String REFERENCE_DIRECTORY = "reference";
    public static final String ERROR_DIRECTORY = "errors";

    private static String baseDirectory = null;

    /**
     * Returns the base directory used for screenshots. This directory contains
     * the "error" and "reference" folders.
     * 
     * @return The base directory used for screenshots, ending in a slash.
     */
    public static String getScreenshotBaseDirectory() {
        if (baseDirectory != null) {
            return baseDirectory;
        }

        baseDirectory = Parameters.getScreenshotDirectory();

        if (baseDirectory == null || baseDirectory.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing reference directory definition. Use -D"
                            + Parameters.SCREENSHOT_DIRECTORY
                            + "=c:\\screenshot\\. ");
        }

        if (!File.separator
                .equals(baseDirectory.charAt(baseDirectory.length() - 1))) {
            baseDirectory = baseDirectory + File.separator;
        }

        return baseDirectory;
    }

    /**
     * Returns the directory used for screenshot references.
     * 
     * @return The screenshot reference directory, ending in a slash.
     * @return
     */
    public static String getScreenshotReferenceDirectory() {
        return getScreenshotBaseDirectory() + REFERENCE_DIRECTORY
                + File.separator;
    }

    /**
     * Returns the directory used for screenshot error images.
     * 
     * @return The screenshot error directory, ending in a slash.
     */
    public static String getScreenshotErrorDirectory() {
        return baseDirectory + ERROR_DIRECTORY + File.separator;

    }

}
