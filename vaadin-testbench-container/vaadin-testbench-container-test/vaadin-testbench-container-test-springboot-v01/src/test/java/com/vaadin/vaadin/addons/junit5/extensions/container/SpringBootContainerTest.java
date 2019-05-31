package com.vaadin.vaadin.addons.junit5.extensions.container;

import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.extensions.container.DemoApp;
import com.vaadin.testbench.addons.junit5.extensions.container.ServletContainerExtension;
import com.vaadin.testbench.addons.junit5.extensions.container.SpringBootConf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ServletContainerExtension.class)
@SpringBootConf(source = DemoApp.class)
class SpringBootContainerTest {

    @Test
    void test_001(ContainerInfo containerInfo) throws IOException {
        try (InputStream in =
                     new URL("http://" + containerInfo.getHost() + ":" + containerInfo.getPort() + "/demo")
                             .openStream();
             Scanner scanner = new Scanner(in)) {
            String string = scanner.nextLine();

            assertEquals("Hello World on port " + containerInfo.getPort(), string);
        }
    }
}
