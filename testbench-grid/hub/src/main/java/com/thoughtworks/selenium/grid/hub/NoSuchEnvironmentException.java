package com.thoughtworks.selenium.grid.hub;

/**
 * Signals attemps to reserve an environment for which there is no registered remote control.
 */
public class NoSuchEnvironmentException extends RuntimeException {

    private final String environment;

    public NoSuchEnvironmentException(String environment) {
        super("Could not find any remote control providing the '" + environment + "' environment. " +
              "Please make sure you started some remote controls which registered as offering this environment.");
        this.environment = environment;
    }

    public String environment() {
        return environment;
    }

}