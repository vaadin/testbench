/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.screenshot;

/**
 * Class for holding position and size of a error found during image comparison
 */
public class ErrorBlock {

    private int x, y;
    private int xBlocks = 1, yBlocks = 1;

    public ErrorBlock() {
        //
    }

    public ErrorBlock(int x, int y, int xBlocks, int yBlocks) {
        this.x = x;
        this.y = y;
        this.xBlocks = xBlocks;
        this.yBlocks = yBlocks;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getXBlocks() {
        return xBlocks;
    }

    public void setXBlocks(int blocks) {
        xBlocks = blocks;
    }

    public int getYBlocks() {
        return yBlocks;
    }

    public void setYBlocks(int blocks) {
        yBlocks = blocks;
    }

    public void addXBlock() {
        xBlocks++;
    }

    public void addYBlock() {
        yBlocks++;
    }
}
