package junit.com.vaadin.vaadin.addons.junit5.extensions.container;

import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.extensions.container.ServletContainerExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ServletContainerExtension.class)
class TestSpringBoot2Initializer {

    @Test
    @Disabled
    void test_001(ContainerInfo containerInfo) throws IOException {
        try (InputStream in =
                     new URL("http://" + containerInfo.host() + ":" + containerInfo.port() + "/demo")
                             .openStream();
             Scanner scanner = new Scanner(in)) {
            String string = scanner.nextLine();

            assertEquals("Hello World on port " + containerInfo.port(), string);
        }
    }
}
