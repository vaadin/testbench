/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.screenshot;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
     *            The name of the reference image file.
     * @return a BufferedImage containing the reference image.
     * @throws IOException
     *             if an error occurs during reading.
     */
    public static BufferedImage readReferenceImage(
            String referenceImageFileName) throws IOException {
        return impl.readReferenceImage(referenceImageFileName);
    }

    public static File getErrorScreenshotFile(String errorImageFileName) {
        return impl.getErrorScreenshotFile(errorImageFileName);
    }

    public static File getReferenceScreenshotFile(
            String referenceImageFileName) {
        return impl.getReferenceScreenshotFile(referenceImageFileName);
    }

    /**
     * Returns the relative file names of reference images. The actual image
     * file for a relative file name can be retrieved with
     * {@link #getReferenceScreenshotFile(String)}.
     *
     * @param referenceImageFileName
     *            id part of the reference image file name
     * @param capabilities
     *            used to compute the full name of the alternative references
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
         *            The name of the reference image file.
         * @return a BufferedImage containing the reference image.
         * @throws IOException
         *             if an error occurs during reading.
         */
        public BufferedImage readReferenceImage(String referenceImageFileName)
                throws IOException {
            return ImageIO
                    .read(getReferenceScreenshotFile(referenceImageFileName));
        }

        public File getErrorScreenshotFile(String errorImageFileName) {
            return new File(getScreenshotErrorDirectory(), errorImageFileName);
        }

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
         *            id part of the reference image file name
         * @param capabilities
         *            used to compute the full name of the alternative
         *            references
         * @return file names of reference images
         */
        public List<String> getReferenceImageFileNames(
                String referenceImageFileName, Capabilities capabilities) {
            ArrayList<String> referenceImages = new ArrayList<>();
            String actualName = findActualFileName(referenceImageFileName,
                    capabilities);
            if (getReferenceScreenshotFile(actualName).exists()) {
                referenceImages.add(actualName);
            }

            for (int alternativeId = 1; alternativeId < 100; alternativeId++) {
                String originalNameAlternative = referenceImageFileName.replace(
                        ".png", String.format("_%d.png", alternativeId));
                String actualNameAlternative = actualName.replace(".png",
                        String.format("_%d.png", alternativeId));

                boolean origAlternativeFound = getReferenceScreenshotFile(
                        originalNameAlternative).exists();
                boolean actualAlternativeFound = getReferenceScreenshotFile(
                        actualNameAlternative).exists();
                if (origAlternativeFound) {
                    referenceImages.add(originalNameAlternative);
                }
                if (actualAlternativeFound) {
                    referenceImages.add(actualNameAlternative);
                }
                if (!origAlternativeFound && !actualAlternativeFound) {
                    break;
                }
            }

            return referenceImages;
        }

        private String findActualFileName(String referenceFileName,
                Capabilities cap) {
            if (cap == null) {
                return referenceFileName;
            }
            String fileName = findOldReferenceScreenshot(cap.getBrowserName(),
                    Integer.valueOf(
                            ReferenceNameGenerator.getMajorVersion(cap)),
                    referenceFileName);
            return fileName;
        }

        /**
         * Checks for reference screenshots for older versions of Google Chrome
         * and use that instead of the generated file name if so.
         *
         * @param browserName
         *            the browser identifier (name + version, e.g. "Chrome_17")
         * @param browserVersion
         * @param fileName
         *            the file name generated from the test name and browser
         *            name + version.
         * @return the generated file name or the file name of a reference image
         *         that actually exists (for an older version of Chrome).
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
