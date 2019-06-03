package com.vaadin.vaadin.addons.junit5.extensions.container;

import com.vaadin.testbench.addons.junit5.extensions.container.ServletContainerExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.vaadin.testbench.addons.junit5.extensions.container.ContainerInitializer.containerInfo;

@ExtendWith(ServletContainerExtension.class)
class NoneContainerTest {

    @Test
    void test001() {
        Assertions.assertEquals("127.0.0.1", containerInfo().getHost());
        Assertions.assertEquals(8088, containerInfo().getPort());
    }
}
