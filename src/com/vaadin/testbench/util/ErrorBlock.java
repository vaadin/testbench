package com.vaadin.testbench.util;

/**
 * Class for holding position and size of a error found during image comparison
 * 
 */
public class ErrorBlock {

    private int x, y;
    private int xBlocks = 1, yBlocks = 1;

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
