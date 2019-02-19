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
package org.rapidpm.vaadin.addons.junit5.extensions;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;

/**
 *
 */
public interface ExtensionFunctions {

  ExtensionContext.Namespace NAMESPACE_GLOBAL = ExtensionContext.Namespace.create("global");


  static Function<ExtensionContext, ExtensionContext.Namespace> namespaceFor() {
    return (ctx) -> {
      String name       = ctx.getTestClass().get().getName();
      String methodName = ctx.getTestMethod().get().getName();
      ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(ExtensionFunctions.class,
                                                                               name,
                                                                               methodName
      );
      return namespace;
    };
  }
  static Function<ExtensionContext, ExtensionContext.Namespace> namespaceForClass() {
    return (ctx) -> {
      String name       = ctx.getTestClass().get().getName();
      ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(ExtensionFunctions.class,
                                                                               name
      );
      return namespace;
    };
  }
  static Function<ExtensionContext, ExtensionContext.Store> storeGlobal() {
    return (context) -> context.getStore(NAMESPACE_GLOBAL);
  }

  static Function<ExtensionContext, ExtensionContext.Store> storeClass() {
    return (context) -> context.getStore(namespaceForClass().apply(context));
  }
  static Function<ExtensionContext, ExtensionContext.Store> store() {
    return (context) -> context.getStore(namespaceFor().apply(context));
  }

  static Function<ExtensionContext, BiConsumer<String, Object>> storeObjectIn() {
    return (context) -> (key, value) -> store().apply(context).put(key, value);
  }

  static Function<ExtensionContext, Consumer<String>> removeObjectIn() {
    return (context) -> (key) -> store().apply(context).remove(key);
  }


}
