/**
 * Copyright (C) 2012 Vaadin Ltd
 * <p>
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * <p>
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * <p>
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.screenshot;

import static com.vaadin.testbench.screenshot.ReferenceNameGenerator.majorVersion;
import static com.vaadin.testbench.screenshot.ScreenshotProperties.getScreenshotErrorDirectory;
import static com.vaadin.testbench.screenshot.ScreenshotProperties.getScreenshotReferenceDirectory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.imageio.ImageIO;

import com.vaadin.frp.functions.CheckedExecutor;
import com.vaadin.frp.functions.CheckedFunction;
import com.vaadin.frp.model.serial.Triple;

public class ImageFileUtil {

  public static final String DIRECTORY_DIFF = "diff";
  public static final String DIRECTORY_LOGS = "logs";
  public static final String IMAGE_FILE_NAME_ENDING = "png";

  public static final class ReferenceInfo extends Triple<String, String, String> {
    public ReferenceInfo(String referenceImageFileName ,
                         String browserName ,
                         String browserVersion) {
      super(referenceImageFileName , browserName , browserVersion);
    }

    public String fileName() { return getT1(); }

    public String browserName() { return getT2(); }

    public String browserVersion() { return getT3(); }

  }


  /**
   * Returns the relative file names of reference images. The actual image
   * file for a relative file name can be retrieved with
   *
   * @return file names of reference images
   */
  public static Function<ReferenceInfo,List<String>> getReferenceImageFileNames() {
    return (info) -> {
      List<String> referenceImages = new ArrayList<>();

      String nextName = findActualFileName().apply(info);

      final Function<String, File> fileFunction = getReferenceScreenshotFile();

      File file = fileFunction.apply(nextName);
      int i = 1;
      while (file.exists()) {
        referenceImages.add(nextName);
        nextName = info.fileName().replace("." + IMAGE_FILE_NAME_ENDING ,
                                           String.format("_%d." + IMAGE_FILE_NAME_ENDING , i++));
        file = fileFunction.apply(nextName);
      }
      return referenceImages;
    };
  }


  private static Function<ReferenceInfo, String> findActualFileName() {
    return (info) -> info.browserName() == null
           ? info.fileName()
           : findOldReferenceScreenshot().apply(info);
  }

  /**
   * Checks for reference screenshots for older versions of Google Chrome
   * and use that instead of the generated file name if so.
   *
   * @return the generated file name or the file name of a reference image
   * that actually exists (for an older version of Chrome).
   */
  private static Function<ReferenceInfo, String> findOldReferenceScreenshot() {
    return (info) -> {
      //TODO check behavior !
      String newFileName = new String(info.fileName());

      if (! getReferenceScreenshotFile().apply(info.fileName()).exists()) {
        String navigatorId = info.browserName() + "_" + info.browserVersion();

        int nextVersion = Integer.valueOf(majorVersion().apply(info.browserVersion()));

        String fileNameTemplate = info.fileName().replace(navigatorId , "####");
        do {
          nextVersion--;
          newFileName = fileNameTemplate.replace("####" ,
                                                 String.format("%s_%d" , info.browserName() , nextVersion));
        } while (! getReferenceScreenshotFile().apply(newFileName).exists()
                 && nextVersion > 0);
        // We didn't find any existing screenshot for any older
        // versions of the browser.
        if (nextVersion == 0) {
          newFileName = info.fileName();
        }
      }
      return newFileName;
    };
  }


  public static CheckedFunction<String, BufferedImage> readReferenceImage() {
    return (referenceImageFileName) -> ImageIO.read(getReferenceScreenshotFile().apply(referenceImageFileName));
  }

  public static Function<String, File> getErrorScreenshotFile() {
    return (String errorImageFileName) -> new File(getScreenshotErrorDirectory() ,
                                                   errorImageFileName);
  }

  public static Function<String, File> getReferenceScreenshotFile() {
    return (referenceImageFileName) -> new File(getScreenshotReferenceDirectory() ,
                                                referenceImageFileName);
  }


  /**
   * Creates all directories used to store screenshots unless they already
   * exist.
   */
  public static CheckedExecutor createScreenshotDirectoriesIfNeeded() {

    return () -> {
      final String refDir = getScreenshotReferenceDirectory();
      final String errorDir = getScreenshotErrorDirectory();

      if (refDir != null) {
        // Check directories and create if needed
        File dir = new File(refDir);
        if (! dir.exists()) {
          dir.mkdirs();
        }
      }
      if (errorDir != null) {
        File dir = new File(errorDir);
        if (! dir.exists()) {
          dir.mkdirs();
        }
        dir = new File(errorDir , DIRECTORY_DIFF);
        if (! dir.exists()) {
          dir.mkdir();
        }
        dir = new File(errorDir , DIRECTORY_LOGS);
        if (! dir.exists()) {
          dir.mkdir();
        }
      }
    };
  }


}
