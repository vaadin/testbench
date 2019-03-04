/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */
package com.vaadin.testbench.addons.junit5.extensions.container;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.valueAsIntPlain;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.valueAsStringPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;

import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface ExtensionContextFunctions {

  static Function<ExtensionContext, Integer> serverPort() {
    return (ctx) -> valueAsIntPlain().apply(SERVER_PORT).apply(ctx);
  }

  static Function<ExtensionContext, String> serverIP() {
    return (ctx) -> valueAsStringPlain().apply(SERVER_IP).apply(ctx);
  }

  static Function<ExtensionContext, ContainerInfo> containerInfo() {
    return ctx -> new ContainerInfo(
        serverPort().apply(ctx) ,
        serverIP().apply(ctx));
  }
}
