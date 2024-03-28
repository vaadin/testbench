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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

public class ScreenShotFailureReporter {
    private final BufferedImage referenceImage;
    private final boolean[][] falseBlocks;
    private final int xBlocks;
    private final int yBlocks;

    public ScreenShotFailureReporter(BufferedImage referenceImage,
            boolean[][] falseBlocks) {
        this.referenceImage = referenceImage;
        this.falseBlocks = falseBlocks;
        xBlocks = ImageComparisonUtil.getNrBlocks(referenceImage.getWidth());
        yBlocks = ImageComparisonUtil.getNrBlocks(referenceImage.getHeight());
    }

    public void createErrorImageAndHTML(String fileName,
            BufferedImage screenshotImage) {
        try {
            // Write the screenshot into the error directory
            ImageIO.write(screenshotImage, "png",
                    ImageFileUtil.getErrorScreenshotFile(fileName));
        } catch (IOException e) {
            System.err.println("Error writing screenshot to "
                    + ImageFileUtil.getErrorScreenshotFile(fileName).getPath());
            e.printStackTrace();
        }

        // collect big error blocks of differences
        List<ErrorBlock> errorAreas = collectErrorsToList(xBlocks, yBlocks);

        // Draw boxes around blocks that differ
        drawErrorsToImage(errorAreas, screenshotImage);

        createDiffHtml(errorAreas, fileName, screenshotImage, referenceImage);
    }

    /**
     * Runs through the marked false macroblocks and collects them to bigger
     * blocks
     * 
     * @param xBlocks
     *            Amount of macroblocks in x direction
     * @param yBlocks
     *            Amount of macroblocks in y direction
     * @param falseBlocks
     *            Map of false blocks
     * @return List of ErrorBlocks
     */
    private List<ErrorBlock> collectErrorsToList(int xBlocks, int yBlocks) {
        List<ErrorBlock> errorAreas = new LinkedList<ErrorBlock>();

        // run through blocks for marked errors for macroblocks.
        for (int y = 0; y < yBlocks; y++) {
            for (int x = 0; x < xBlocks; x++) {
                // if found error make new ErrorBlock and collect
                // connected error blocks and mark them false so
                // that they won't trigger new errors
                if (falseBlocks[x][y]) {
                    ErrorBlock newBlock = new ErrorBlock();
                    newBlock.setX(x * 16);
                    newBlock.setY(y * 16);
                    int x1 = x, xmin = x, y1 = y, maxSteps = xBlocks * yBlocks, steps = 0;
                    falseBlocks[x][y] = false;

                    // This'll confirm logic errors.
                    while (true) {
                        x1++;

                        // if x1 out of bounds set x1 to xmin where
                        // xmin == smallest error block found for
                        // this error
                        if (x1 >= xBlocks) {
                            x1 = xmin;
                        }

                        // if x1,y1 marked true add width to ErrorBlock
                        if (falseBlocks[x1][y1]) {
                            newBlock.addXBlock();
                            falseBlocks[x1][y1] = false;
                        } else if (y1 < yBlocks) {
                            x1 = xmin;

                            // If next row has a false block
                            // connected to our block
                            boolean foundConnectedBlock = false;
                            for (int foundX = x1; foundX < x1
                                    + newBlock.getXBlocks(); foundX++) {
                                if (foundX == xBlocks || y1 + 1 == yBlocks) {
                                    break;
                                }

                                if (falseBlocks[foundX][y1 + 1]) {
                                    foundConnectedBlock = true;
                                }
                            }

                            // If connected error to ErrorBlock add
                            // height to error block
                            if (foundConnectedBlock) {
                                y1++;
                                newBlock.addYBlock();

                                // while stepping back on this
                                // row is false change block x
                                // position
                                if (x1 - 1 >= 0) {
                                    while (falseBlocks[x1 - 1][y1]) {
                                        falseBlocks[x1 - 1][y1] = false;
                                        newBlock.addXBlock();
                                        x1 = x1 - 1;
                                        newBlock.setX(newBlock.getX() - 16);
                                        if (x1 == 0) {
                                            break;
                                        }
                                    }
                                    xmin = x1;
                                }

                                // Skip blocks inside main error
                                // block for this error
                                x1 = x1 + newBlock.getXBlocks() - 1;
                            } else {
                                x1 = newBlock.getX() / 16;
                                y1 = newBlock.getY() / 16;
                                // Set all blocks to false
                                // inside found box
                                for (int j = 0; j < newBlock.getYBlocks(); j++) {
                                    for (int i = 0; i < newBlock.getXBlocks(); i++) {
                                        if (x1 + i < xBlocks
                                                && y1 + j < yBlocks) {
                                            falseBlocks[x1 + i][y1 + j] = false;
                                        }
                                    }
                                }
                                break;

                            }
                        }
                        // In case something goes wrong we won't get stuck in
                        // the loop forever
                        if (++steps == maxSteps) {
                            break;
                        }
                    }
                    errorAreas.add(newBlock);
                }
            }
        }
        return errorAreas;
    }

    private void drawErrorsToImage(List<ErrorBlock> errorAreas,
            BufferedImage screenshotImage) {
        // Draw lines around false ErrorBlocks before saving _diff
        // file.
        Graphics2D drawToPicture = screenshotImage.createGraphics();
        drawToPicture.setColor(Color.MAGENTA);

        int width = screenshotImage.getWidth();
        int height = screenshotImage.getHeight();

        for (ErrorBlock error : errorAreas) {
            int offsetX = 0, offsetY = 0;
            if (error.getX() > 0) {
                offsetX = 1;
            }
            if (error.getY() > 0) {
                offsetY = 1;
            }
            int toX = error.getXBlocks() * 16 + offsetX;
            int toY = error.getYBlocks() * 16 + offsetY;
            // Draw lines inside canvas
            if ((error.getX() + (error.getXBlocks() * 16) + offsetX) > width) {
                toX = width - error.getX();
            }
            if ((error.getY() + (error.getYBlocks() * 16) + offsetY) > height) {
                toY = height - error.getY();
            }

            // draw error to image
            drawToPicture.drawRect(error.getX() - offsetX, error.getY()
                    - offsetY, toX, toY);

        }
        // release resources
        drawToPicture.dispose();
    }

    /**
     * Build a small html file that has mouse over picture change for fast
     * checking of errors and click on picture to switch between reference and
     * diff pictures.
     * 
     * @param blocks
     *            List of ErrorBlock
     * @param diff
     *            diff file
     * @param reference
     *            reference image file
     * @param fileId
     *            fileName for html file
     */
    private void createDiffHtml(List<ErrorBlock> blocks, String fileId,
            BufferedImage screenshotImage, BufferedImage referenceImage) {
        String image = ImageUtil.encodeImageToBase64(screenshotImage);
        String ref_image = ImageUtil.encodeImageToBase64(referenceImage);
        try {
            PrintWriter writer = new PrintWriter(
                    ImageFileUtil.getErrorScreenshotFile(fileId + ".html"));
            // Write head
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<script type=\"text/javascript\">var difference = true;function switchImage(){"
                    + "if(difference){difference = false;document.getElementById('reference').style.display='block';"
                    + "document.getElementById('diff').style.display='none';}else{difference = true;"
                    + "document.getElementById('reference').style.display='none';document.getElementById('diff').style.display='block';"
                    + "}}</script>");
            writer.println("</head>");
            writer.println("<body onclick=\"switchImage()\" style=\"-moz-user-select: none; -webkit-user-select: none; -ms-user-select: none;\">");

            writer.println("<div id=\"diff\" style=\"display: block; position: absolute; top: 0px; left: 0px;\"><img src=\"data:image/png;base64,"
                    + image
                    + "\"/><span style=\"position: absolute; top: 0px; left: 0px; opacity:0.4; filter: alpha(opacity=40); font-weight: bold;\">Image for this run</span></div>");
            writer.println("<div id=\"reference\" style=\"display: none; position: absolute; top: 0px; left: 0px; z-index: 999;\"><img src=\"data:image/png;base64,"
                    + ref_image + "\"/></div>");

            int add = 0;
            for (ErrorBlock error : blocks) {
                int offsetX = 0, offsetY = 0;
                if (error.getX() > 0) {
                    offsetX = 1;
                }
                if (error.getY() > 0) {
                    offsetY = 1;
                }
                String id = "popUpDiv_" + (error.getX() + add) + "_"
                        + (error.getY() + add);
                // position stars so that it's not out of screen.
                writer.println("<div  onmouseover=\"document.getElementById('"
                        + id
                        + "').style.display='block'\"  style=\"z-index: 66;position: absolute; top: 0px; left: 0px; clip: rect("
                        + (error.getY() - offsetY) + "px,"
                        + (error.getX() + (error.getXBlocks() * 16) + 1)
                        + "px,"
                        + (error.getY() + (error.getYBlocks() * 16) + 1)
                        + "px," + (error.getX() - offsetX)
                        + "px);\"><img src=\"data:image/png;base64," + image
                        + "\"/></div>");
                // Start "popup" div
                writer.println("<div class=\"popUpDiv\" onclick=\"document.getElementById('reference').style.display='block'; document.getElementById('diff').style.display='none';\" onmouseout=\"this.style.display='none'\" id=\""
                        + id
                        + "\"  style=\"display: none; position: absolute; top: 0px; left: 0px; clip: rect("
                        + (error.getY() - offsetY)
                        + "px,"
                        + (error.getX() + (error.getXBlocks() * 16) + 1)
                        + "px,"
                        + (error.getY() + (error.getYBlocks() * 16) + 1)
                        + "px,"
                        + (error.getX() - offsetX)
                        + "px); z-index: "
                        + (99 + add) + ";\">");
                writer.println("<img src=\"data:image/png;base64," + ref_image
                        + "\" />");
                // End popup div
                writer.println("</div>");
                add++;
            }

            // End file
            writer.println("</body></html>");
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
