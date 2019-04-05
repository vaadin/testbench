package com.vaadin.testbench.addons.junit5.extensions.container;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.vaadin.testbench.addons.junit5.extensions.container.ExtensionContextFunctions.containerInfo;

public class ContainerInfoExtension implements BeforeEachCallback {

    private ContainerInfo containerInfo;

    public int port() {
        return containerInfo.port();
    }

    public String host() {
        return containerInfo.host();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
//    logger().info("ContainerInfoExtension - beforeEach ");
        containerInfo = containerInfo(extensionContext);
//    logger().info("ContainerInfoExtension - " + containerInfo);
    }
}
