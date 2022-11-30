/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.screenshot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.testbench.testutils.ImageLoader;

public class ImageUtilTest {

    private static final String FOLDER = ImageUtilTest.class.getPackage()
            .getName().replace('.', '/');

    @Test
    public void canCropImagesToSameSize() throws IOException {
        BufferedImage image1 = ImageLoader.loadImage(FOLDER,
                "screenshot1008x767.png");
        BufferedImage image2 = ImageLoader.loadImage(FOLDER + "/reference",
                "reference738x624.png");
        ImageUtil.cropToBeSameSize(image1, image2);
    }

    @Test
    public void cropImagesToSameSizeReturnsNewFirstImage() throws IOException {
        BufferedImage image1 = ImageLoader.loadImage(FOLDER,
                "screenshot1008x767.png");
        BufferedImage image2 = ImageLoader.loadImage(FOLDER + "/reference",
                "reference738x624.png");
        List<BufferedImage> images = ImageUtil.cropToBeSameSize(image1, image2);
        Assertions.assertFalse(image1.equals(images.get(0)));
        Assertions.assertEquals(image2, images.get(1));
    }

    @Test
    public void cropImagesToSameSizeReturnsImagesOfSameDimensions()
            throws IOException {
        BufferedImage image1 = ImageLoader.loadImage(FOLDER,
                "screenshot1008x767.png");
        BufferedImage image2 = ImageLoader.loadImage(FOLDER + "/reference",
                "reference738x624.png");
        List<BufferedImage> images = ImageUtil.cropToBeSameSize(image1, image2);
        image1 = images.get(0);
        image2 = images.get(1);
        Assertions.assertEquals(image1.getHeight(), image2.getHeight());
        Assertions.assertEquals(image1.getWidth(), image2.getWidth());
    }

    @Test
    public void cropImagesToSameSizeReturnsImagesInTheSameOrderAsPassedIn()
            throws IOException {
        BufferedImage image1 = ImageLoader.loadImage(FOLDER,
                "screenshot1008x767.png");
        BufferedImage image2 = ImageLoader.loadImage(FOLDER + "/reference",
                "reference738x624.png");
        List<BufferedImage> images = ImageUtil.cropToBeSameSize(image1, image2);
        // image2 should remain unchanged, since it is smaller than image1 and
        // should not be cropped
        Assertions.assertEquals(image2, images.get(1));
    }

    @Test
    public void cropImagesToSameSize_bothImagesAlreadySameSize_doesNothing()
            throws IOException {
        BufferedImage image1 = ImageLoader.loadImage(FOLDER,
                "16x16-screenshot.png");
        BufferedImage image2 = ImageLoader.loadImage(FOLDER,
                "16x16-reference.png");
        List<BufferedImage> images = ImageUtil.cropToBeSameSize(image1, image2);
        Assertions.assertEquals(image1, images.get(0));
        Assertions.assertEquals(image2, images.get(1));
    }
}
