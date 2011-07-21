package com.thoughtworks.selenium.grid.hub;

/**
 * Exception thrown on the hub if getting a session from a remote control fails.
 */
public class CouldNotGetSessionException extends RuntimeException {
    public CouldNotGetSessionException(String message) {
        super(message);
    }
}
