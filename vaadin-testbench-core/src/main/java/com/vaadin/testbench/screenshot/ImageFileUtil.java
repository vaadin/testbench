package com.vaadin.testbench.screenshot;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.vaadin.testbench.screenshot.ReferenceNameGenerator.majorVersion;
import static com.vaadin.testbench.screenshot.ScreenshotProperties.getScreenshotErrorDirectory;
import static com.vaadin.testbench.screenshot.ScreenshotProperties.getScreenshotReferenceDirectory;

public class ImageFileUtil {

    public static final String DIRECTORY_DIFF = "diff";
    public static final String DIRECTORY_LOGS = "logs";

    /**
     * Returns the relative file names of reference images. The actual image
     * file for a relative file name can be retrieved with
     *
     * @return file names of reference images
     */
    public static List<String> getReferenceImageFileNames(ReferenceInfo info) {
        List<String> referenceImages = new ArrayList<>();

        String nextName = findActualFileName(info);
        Optional<ByteArrayInputStream> file = getReferenceScreenshotFile(nextName);
        int i = 1;
        while (file.isPresent()) {
            referenceImages.add(nextName);
            nextName = info.fileName().replace("." + ScreenshotProperties.IMAGE_FILE_NAME_ENDING,
                    String.format("_%d." + ScreenshotProperties.IMAGE_FILE_NAME_ENDING, i++));
            file = getReferenceScreenshotFile(nextName);
        }

        return referenceImages;
    }

    private static String findActualFileName(ReferenceInfo info) {
        return info.browserName() == null
                ? info.fileName()
                : findOldReferenceScreenshot(info);
    }

    /**
     * Checks for reference screenshots for older versions of Google Chrome
     * and use that instead of the generated file name if so.
     *
     * @return the generated file name or the file name of a reference image
     * that actually exists (for an older version of Chrome).
     */
    private static String findOldReferenceScreenshot(ReferenceInfo info) {
        // TODO(sven): Check behavior!
        String newFileName = info.fileName();

        if (!getReferenceScreenshotFile(info.fileName()).isPresent()) {
            String navigatorId = info.browserName() + "_" + info.browserVersion();

            int nextVersion = Integer.valueOf(majorVersion(info.browserVersion()));

            String fileNameTemplate = info.fileName().replace(navigatorId, "####");
            do {
                nextVersion--;
                newFileName = fileNameTemplate.replace("####",
                        String.format("%s_%d",
                                info.browserName(),
                                nextVersion));
            } while (!getReferenceScreenshotFile(newFileName)
                    .isPresent()
                    && nextVersion > 0);
            // We didn't find any existing screenshot for any older
            // versions of the browser.
            if (nextVersion == 0) {
                newFileName = info.fileName();
            }
        }
        return newFileName;
    }

    public static Optional<BufferedImage> readReferenceImage(String referenceImageFileName) {
        return getReferenceScreenshotFile(referenceImageFileName)
                .map(e -> {
                    try {
                        return ImageIO.read(e);
                    } catch (IOException ex) {
                        return null;
                    }
                });
    }

    public static File getErrorScreenshotFile(String errorImageFileName) {
        return new File(getScreenshotErrorDirectory(),
                errorImageFileName);
    }

    private static Optional<ByteArrayInputStream> getReferenceScreenshotFile(String referenceImageFileName) {
        final String filename = "/" + getScreenshotReferenceDirectory() + "/" + referenceImageFileName;
        final InputStream resource = ImageFileUtil.class.getResourceAsStream(filename);
        byte[] buff = new byte[8000];

        int bytesRead;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            while ((bytesRead = resource.read(buff)) != -1) {
                bao.write(buff, 0, bytesRead);
            }

            byte[] data = bao.toByteArray();
            return Optional.of(new ByteArrayInputStream(data));
        } catch (IOException e) {
//            logger().info("No reference screenshot found for " + referenceImageFileName);
            return Optional.empty();
        }
    }

    /**
     * Creates all directories used to store screenshots unless they already
     * exist.
     */
    public static void createScreenshotDirectoriesIfNeeded() {
        final String errorDir = getScreenshotErrorDirectory();

        if (errorDir != null) {
            File dir = new File(errorDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            dir = new File(errorDir, DIRECTORY_DIFF);
            if (!dir.exists()) {
                dir.mkdir();
            }
            dir = new File(errorDir, DIRECTORY_LOGS);
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
    }

    public static final class ReferenceInfo {

        private final String referenceImageFileName;
        private final String browserName;
        private final String browserVersion;

        public ReferenceInfo(String referenceImageFileName,
                             String browserName,
                             String browserVersion) {
            this.referenceImageFileName = referenceImageFileName;
            this.browserName = browserName;
            this.browserVersion = browserVersion;
        }

        public String fileName() {
            return referenceImageFileName;
        }

        public String browserName() {
            return browserName;
        }

        public String browserVersion() {
            return browserVersion;
        }
    }
}
