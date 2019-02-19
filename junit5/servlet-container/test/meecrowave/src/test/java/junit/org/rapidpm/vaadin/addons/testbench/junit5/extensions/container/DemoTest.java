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
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.ContainerInfoExtension;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.ServletContainerExtension;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ExtendWith(ServletContainerExtension.class)
//@MeecrowaveConfig - would start the meecrowave again on beforeAll level
public class DemoTest implements HasLogger {

  @RegisterExtension ContainerInfoExtension config = new ContainerInfoExtension();

  private static OkHttpClient client = new OkHttpClient();


  @Test
  public void test001(TestReporter reporter) {

    final String url = "http://" + config.getHost() + ":" + config.getPort() + "?value=HalloNase";
//    final String url = "http://" + config.getHost() + ":" + config.getPort() + "";
    logger().info("url - " + url);

    Request request = new Request.Builder()
        .url(url)
        .build();

    try (Response response = client.newCall(request).execute()) {
      String result = response.body().string().trim();
      assertEquals("HALLONASE" , result);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
