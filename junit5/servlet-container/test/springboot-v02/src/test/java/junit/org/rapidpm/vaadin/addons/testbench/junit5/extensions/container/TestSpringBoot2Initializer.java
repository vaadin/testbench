/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.container;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.ContainerInfo;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.DemoApp;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.ServletContainerExtension;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.SpringBoot2Conf;

@ExtendWith(ServletContainerExtension.class)
@SpringBoot2Conf(source = DemoApp.class)
public class TestSpringBoot2Initializer {

  @Test
  void test_001(ContainerInfo containerInfo) throws MalformedURLException, IOException {
    try (InputStream in =
        new URL("http://" + containerInfo.getHost() + ":" + containerInfo.getPort() + "/demo")
            .openStream();
        Scanner scanner = new Scanner(in);) {
      String string = scanner.nextLine();

      assertEquals("Hello World on port " + containerInfo.getPort(), string);
    }
  }
}
