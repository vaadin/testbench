package com.vaadin.testbench.util;

/**
 * Class with browser dimensions
 * 
 */
public class BrowserDimensions {

    private int width;
    private int height;
    private int canvasWidth;
    private int canvasHeight;
    private int canvasXPosition;
    private int canvasYPosition;

    public BrowserDimensions(int width, int height, int canvasWidth,
            int canvasHeight, int canvasXPosition, int canvasYPosition) {
        this.width = width;
        this.height = height;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.canvasXPosition = canvasXPosition;
        this.canvasYPosition = canvasYPosition;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public int getCanvasXPosition() {
        return canvasXPosition;
    }

    public void setCanvasXPosition(int canvasXPosition) {
        this.canvasXPosition = canvasXPosition;
    }

    public int getCanvasYPosition() {
        return canvasYPosition;
    }

    public void setCanvasYPosition(int canvasYPosition) {
        this.canvasYPosition = canvasYPosition;
    }

}
