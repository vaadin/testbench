package com.vaadin.testbench.addons.junit5.extensions.container;

import com.vaadin.frp.model.serial.Pair;

public class ContainerInfo extends Pair<Integer, String> {

    public ContainerInfo(Integer port, String host) {
        super(port, host);
    }

    public int port() {
        return getT1();
    }

    public String host() {
        return getT2();
    }
}
