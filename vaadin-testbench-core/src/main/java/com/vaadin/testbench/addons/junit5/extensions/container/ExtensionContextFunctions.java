package com.vaadin.testbench.addons.junit5.extensions.container;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.function.Function;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.valueAsIntPlain;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.valueAsStringPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;

public interface ExtensionContextFunctions {

    static Function<ExtensionContext, Integer> serverPort() {
        return (ctx) -> valueAsIntPlain().apply(SERVER_PORT).apply(ctx);
    }

    static Function<ExtensionContext, String> serverIP() {
        return (ctx) -> valueAsStringPlain().apply(SERVER_IP).apply(ctx);
    }

    static Function<ExtensionContext, ContainerInfo> containerInfo() {
        return ctx -> new ContainerInfo(
                serverPort().apply(ctx),
                serverIP().apply(ctx));
    }
}
