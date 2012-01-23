package com.vaadin.testbench.commands;

public class CanvasObstructedException extends RuntimeException {
    public CanvasObstructedException(String message) {
        super(message);
    }

    public CanvasObstructedException(String message, Throwable cause) {
        super(message, cause);
    }
}
