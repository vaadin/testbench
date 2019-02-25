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

import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.util.function.Function;

import org.rapidpm.frp.functions.CheckedFunction;

public class ImageComparisonUtil {

  /**
   * Generates blocks representing an image by dividing the image up in 16x16
   * pixel blocks and calculating a mean value of the color in each block.
   *
   * @return the block representation of the image
   */
  public static CheckedFunction<BufferedImage, byte[]> imageHash() {
    return (image) -> {
      int width = image.getWidth();
      int height = image.getHeight();

      byte[] data = new byte[width * height * 3];

      int idx = 0;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int rgb = image.getRGB(x , y);
          rgb &= 0x00FCFCFC;

          // Skip the two last bits for fuzzy comparison
          data[idx++] = (byte) ((rgb >> 16));
          data[idx++] = (byte) ((rgb >> 8));
          data[idx++] = (byte) (rgb);
        }
      }
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(data);
      return md5.digest();
    };
  }


  public static Function<byte[], String> byteToHex(){
    return (bytes) -> {
      String hex = "";
      for (byte aByte : bytes) {
        hex += Integer
            .toString((aByte & 0xff) + 0x100 , 16)
            .substring(1);
      }
      return hex;
    };
  }

  /**
   * Returns the number of blocks used for the given number of pixels. All
   * blocks are full size with the (possible) exception of the bottom and
   * right edges.
   *
   * @param pixels The number of pixels for the dimension.
   * @return The number of blocks used for that dimension
   */
  public static int getNrBlocks(int pixels) {
    return (int) Math.floor(pixels + 15) / 16;
  }
}
