/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
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
package org.rapidpm.dependencies.core.basepattern.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>Abstract NestedBuilder class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public abstract class NestedBuilder<T, V> {
  protected T parent;

  /**
   * To get the parent builder
   *
   * @return T the instance of the parent builder
   */
  public T done() {
    Class<?> parentClass = parent.getClass();
    try {
      V build = this.build();
      String methodname = "with" + build.getClass().getSimpleName();
      Method method = parentClass.getDeclaredMethod(methodname, build.getClass());
      final boolean accessible = method.isAccessible();
      method.setAccessible(true);
      method.invoke(parent, build);
      method.setAccessible(accessible);
    } catch (NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException e) {
      e.printStackTrace();
    }
    return parent;
  }

  /**
   * <p>build.</p>
   *
   * @return a V object.
   */
  public abstract V build();

  /**
   * <p>withParentBuilder.</p>
   *
   * @param parent a T object.
   * @param <P> a P object.
   * @return a P object.
   */
  public <P extends NestedBuilder<T, V>> P withParentBuilder(T parent) {
    this.parent = parent;
    return (P) this;
  }
}
