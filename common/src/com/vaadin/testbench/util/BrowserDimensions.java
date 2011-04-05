package com.vaadin.testbench.util;

/**
 * Class with browser dimensions
 * 
 */
public class BrowserDimensions {

    private int screenWidth;
    private int screenHeight;
    private int canvasWidth;
    private int canvasHeight;
    /**
     * X position of the canvas. For IE9 in native mode this will be 2 pixel too
     * large.
     */
    /**
     * Y position of the canvas. For IE9 in native mode this will be 2 pixel too
     * large.
     */
    private int canvasXPosition;
    private int canvasYPosition;

    public BrowserDimensions(int screenWidth, int screenHeight,
            int canvasWidth, int canvasHeight, int canvasXPosition,
            int canvasYPosition) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.canvasXPosition = canvasXPosition;
        this.canvasYPosition = canvasYPosition;
    }

    public BrowserDimensions(String dimensions) {
        String[] dims = dimensions.split(",");
        int ix = 0;
        if ("OK".equals(dims[0])) {
            ix = 1;
        }
        screenWidth = Integer.parseInt(dims[ix++]);
        screenHeight = Integer.parseInt(dims[ix++]);
        canvasWidth = Integer.parseInt(dims[ix++]);
        canvasHeight = Integer.parseInt(dims[ix++]);
        canvasXPosition = Integer.parseInt(dims[ix++]);
        canvasYPosition = Integer.parseInt(dims[ix++]);
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int width) {
        screenWidth = width;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int height) {
        screenHeight = height;
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

    @Override
    public String toString() {
        return "[BrowserDimensions, canvas: " + getCanvasWidth() + ","
                + getCanvasHeight() + "]";
    }

    public String getDimensionsString() {
        return getScreenWidth() + "," + getScreenHeight() + ","
                + getCanvasWidth() + "," + getCanvasHeight() + ","
                + getCanvasXPosition() + "," + getCanvasYPosition();
    }
}
