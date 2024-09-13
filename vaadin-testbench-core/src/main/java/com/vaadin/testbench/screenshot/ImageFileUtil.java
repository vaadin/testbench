/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.screenshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.Capabilities;

import com.vaadin.testbench.Parameters;

public class ImageFileUtil {

    private static ImageFileUtilImpl impl = new ImageFileUtilImpl();

    /**
     * Returns the directory used for screenshot references.
     * 
     * @return The screenshot reference directory, ending in a slash.
     */
    public static String getScreenshotReferenceDirectory() {
        return impl.getScreenshotReferenceDirectory();
    }

    /**
     * Returns the directory used for screenshot error images.
     * 
     * @return The screenshot error directory, ending in a slash.
     */
    public static String getScreenshotErrorDirectory() {
        return impl.getScreenshotErrorDirectory();

    }

    /**
     * Creates all directories used to store screenshots unless they already
     * exist.
     */
    public static void createScreenshotDirectoriesIfNeeded() {
        impl.createScreenshotDirectoriesIfNeeded();
    }

    /**
     * Reads the given reference image into a BufferedImage
     * 
     * @param referenceImageFileName
     *            image pathname
     * @return buffered image
     * @throws IOException
     *             if an error occurs during reading the image
     */
    public static BufferedImage readReferenceImage(String referenceImageFileName)
            throws IOException {
        return impl.readReferenceImage(referenceImageFileName);
    }

    /**
     * Returns an error screenshot {@link File} instance with the given file
     * name within the error screenshot directory. The file doesn't necessarily
     * exist yet in the file system after this method has been called.
     *
     * @param errorImageFileName
     *            name of the error screenshot
     * @return error screenshot file
     */
    public static File getErrorScreenshotFile(String errorImageFileName) {
        return impl.getErrorScreenshotFile(errorImageFileName);
    }

    /**
     * Returns a reference screenshot {@link File} instance with the given file
     * name within the reference screenshot directory. The file doesn't
     * necessarily exist yet in the file system after this method has been
     * called.
     *
     * @param referenceImageFileName
     *            name of the reference screenshot
     * @return reference screenshot file
     */
    public static File getReferenceScreenshotFile(String referenceImageFileName) {
        return impl.getReferenceScreenshotFile(referenceImageFileName);
    }

    /**
     * Returns the relative file names of reference images. The actual image
     * file for a relative file name can be retrieved with
     * {@link #getReferenceScreenshotFile(String)}.
     * 
     * @param referenceImageFileName
     *            name of the reference screenshot
     * @param capabilities
     *            browser capabilities
     * @return file names of reference images
     */
    public static List<String> getReferenceImageFileNames(
            String referenceImageFileName, Capabilities capabilities) {
        return impl.getReferenceImageFileNames(referenceImageFileName,
                capabilities);
    }

    public static class ImageFileUtilImpl {
        /**
         * Returns the directory used for screenshot references.
         * 
         * @return The screenshot reference directory, ending in a slash.
         */
        public String getScreenshotReferenceDirectory() {
            return Parameters.getScreenshotReferenceDirectory();
        }

        /**
         * Returns the directory used for screenshot error images.
         * 
         * @return The screenshot error directory, ending in a slash.
         */
        public String getScreenshotErrorDirectory() {
            return Parameters.getScreenshotErrorDirectory();

        }

        /**
         * Creates all directories used to store screenshots unless they already
         * exist.
         */
        public void createScreenshotDirectoriesIfNeeded() {
            if (getScreenshotReferenceDirectory() != null) {
                // Check directories and create if needed
                File dir = new File(getScreenshotReferenceDirectory());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            if (getScreenshotErrorDirectory() != null) {
                File dir = new File(getScreenshotErrorDirectory());
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if (Parameters.isDebug()) {
                    dir = new File(getScreenshotErrorDirectory(), "diff");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    dir = new File(getScreenshotErrorDirectory(), "logs");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                }
            }
        }

        /**
         * Reads the given reference image into a BufferedImage
         * 
         * @param referenceImageFileName
         *            file name
         * @return image instance
         * @throws IOException
         *             if reading the file failed
         */
        public BufferedImage readReferenceImage(String referenceImageFileName)
                throws IOException {
            return ImageIO
                    .read(getReferenceScreenshotFile(referenceImageFileName));
        }

        /**
         * Returns an error screenshot {@link File} instance with the given file
         * name within the error screenshot directory. The file doesn't
         * necessarily exist yet in the file system after this method has been
         * called.
         *
         * @param errorImageFileName
         *            name of the error screenshot
         * @return error screenshot file
         */
        public File getErrorScreenshotFile(String errorImageFileName) {
            return new File(getScreenshotErrorDirectory(), errorImageFileName);
        }

        /**
         * Returns a reference screenshot {@link File} instance with the given
         * file name within the reference screenshot directory. The file doesn't
         * necessarily exist yet in the file system after this method has been
         * called.
         *
         * @param referenceImageFileName
         *            name of the reference screenshot
         * @return reference screenshot file
         */
        public File getReferenceScreenshotFile(String referenceImageFileName) {
            return new File(getScreenshotReferenceDirectory(),
                    referenceImageFileName);
        }

        /**
         * Returns the relative file names of reference images. The actual image
         * file for a relative file name can be retrieved with
         * {@link #getReferenceScreenshotFile(String)}.
         * 
         * @param referenceImageFileName
         *            name of the reference screenshot
         * @param capabilities
         *            browser capabilities
         * @return file names of reference images
         */
        public List<String> getReferenceImageFileNames(
                String referenceImageFileName, Capabilities capabilities) {
            ArrayList<String> referenceImages = new ArrayList<String>();
            String nextName = findActualFileName(referenceImageFileName,
                    capabilities);
            File file = getReferenceScreenshotFile(nextName);
            int i = 1;
            while (file.exists()) {
                referenceImages.add(nextName);
                nextName = referenceImageFileName.replace(".png",
                        String.format("_%d.png", i++));
                file = getReferenceScreenshotFile(nextName);
            }

            return referenceImages;
        }

        private String findActualFileName(String referenceFileName,
                Capabilities cap) {
            if (cap == null) {
                return referenceFileName;
            }
            String fileName = findOldReferenceScreenshot(
                cap.getBrowserName(),
                Integer.valueOf(ReferenceNameGenerator.getMajorVersion(cap)),
                referenceFileName);
            return fileName;
        }

        /**
         * Checks for reference screenshots for older versions of the given
         * browser and uses that instead of the generated file name if so.
         *
         * @param browserName
         *            the browser name, e.g. chrome
         * @param browserVersion
         *            the browser version number, e.g. 128
         * @param fileName
         *            the file name generated from the image reference (e.g.
         *            test name or some other unique identifier), platform, and
         *            browser name + version, e.g.
         *            button4_windows_chrome_128.png
         * @return the generated file name or the file name of a reference image
         *         that actually exists (for an older version of the same
         *         browser)
         */
        String findOldReferenceScreenshot(String browserName,
                int browserVersion, String fileName) {
            String newFileName = new String(fileName);
            if (!ImageFileUtil.getReferenceScreenshotFile(fileName).exists()) {
                String navigatorId = browserName + "_" + browserVersion;
                int nextVersion = browserVersion;
                String fileNameTemplate = fileName.replace(navigatorId, "####");
                do {
                    nextVersion--;
                    newFileName = fileNameTemplate.replace("####",
                            String.format("%s_%d", browserName, nextVersion));
                } while (!ImageFileUtil.getReferenceScreenshotFile(newFileName)
                        .exists() && nextVersion > 0);
                // We didn't find any existing screenshot for any older
                // versions of the browser.
                if (nextVersion == 0) {
                    newFileName = fileName;
                }
            }
            return newFileName;
        }

    }
}
