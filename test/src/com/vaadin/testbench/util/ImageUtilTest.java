/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.testbench.util;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ImageUtilTest {

    private static final String FOLDER = ImageUtilTest.class.getPackage()
            .getName().replace('.', '/');

    @Test
    public void canCropImagesToSameSize() throws IOException {
        BufferedImage image1 = ImageLoader.loadImage(FOLDER,
                "screenshot1008x767.png");
        BufferedImage image2 = ImageLoader.loadImage(FOLDER + "/reference",
                "reference738x624.png");
        ImageUtil.cropToBeSameSize(Arrays.asList(image1, image2));
    }

    @Test
    public void cropImagesToSameSizeReturnsNewImages() throws IOException {
        BufferedImage image1 = ImageLoader.loadImage(FOLDER,
                "screenshot1008x767.png");
        BufferedImage image2 = ImageLoader.loadImage(FOLDER + "/reference",
                "reference738x624.png");
        List<BufferedImage> images = ImageUtil.cropToBeSameSize(Arrays.asList(
                image1, image2));
    }

    @Test
    public void cropImagesToSameSizeReturnsImagesOfSameDimensions()
            throws IOException {
        BufferedImage image1 = ImageLoader.loadImage(FOLDER,
                "screenshot1008x767.png");
        BufferedImage image2 = ImageLoader.loadImage(FOLDER + "/reference",
                "reference738x624.png");
        List<BufferedImage> images = ImageUtil.cropToBeSameSize(Arrays.asList(
                image1, image2));
        image1 = images.get(0);
        image2 = images.get(1);
        assertEquals(image1.getHeight(), image2.getHeight());
        assertEquals(image1.getWidth(), image2.getWidth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cropImagesToSameSizeDoesntAcceptListOfOne() {
        ImageUtil.cropToBeSameSize(Arrays.asList(dummyImage()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cropImagesToSameSizeDoesntAcceptListOfThree() {
        ImageUtil.cropToBeSameSize(Arrays.asList(dummyImage(), dummyImage(),
                dummyImage()));
    }

    @Test
    public void cropImagesToSameSizeReturnsImagesInTheSameOrderAsPassedIn()
            throws IOException {
        BufferedImage image1 = ImageLoader.loadImage(FOLDER,
                "screenshot1008x767.png");
        BufferedImage image2 = ImageLoader.loadImage(FOLDER + "/reference",
                "reference738x624.png");
        List<BufferedImage> images = ImageUtil.cropToBeSameSize(Arrays.asList(
                image1, image2));
        // image2 should remain unchanged, since it is smaller than image1 and
        // should not be cropped
        assertEquals(image2, images.get(1));
    }

    private BufferedImage dummyImage() {
        return new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
    }

}
