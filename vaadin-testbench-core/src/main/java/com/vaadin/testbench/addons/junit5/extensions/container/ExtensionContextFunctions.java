package com.vaadin.testbench.addons.junit5.extensions.container;

import org.junit.jupiter.api.extension.ExtensionContext;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.valueAsIntPlain;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.valueAsStringPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;

public interface ExtensionContextFunctions {

    static Integer serverPort(ExtensionContext ctx) {
        return valueAsIntPlain(SERVER_PORT, ctx);
    }

    static String serverIp(ExtensionContext ctx) {
        return valueAsStringPlain(SERVER_IP, ctx);
    }

    static ContainerInfo containerInfo(ExtensionContext ctx) {
        return new ContainerInfo(serverPort(ctx), serverIp(ctx));
    }
}
