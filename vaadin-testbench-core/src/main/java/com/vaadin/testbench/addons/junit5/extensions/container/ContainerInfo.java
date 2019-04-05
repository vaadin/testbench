package com.vaadin.testbench.addons.junit5.extensions.container;

public class ContainerInfo {

    private final Integer port;
    private final String host;

    public ContainerInfo(Integer port, String host) {
        this.port = port;
        this.host = host;
    }

    public int port() {
        return port;
    }

    public String host() {
        return host;
    }
}
