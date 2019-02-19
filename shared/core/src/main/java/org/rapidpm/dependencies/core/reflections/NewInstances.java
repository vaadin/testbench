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
package org.rapidpm.dependencies.core.reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by RapidPM - Team on 08.09.16.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class NewInstances {

  private NewInstances() { }


  /**
   * <p>createInstances.</p>
   *
   * @param classes a {@link java.util.Set} object.
   * @param <T> a T object.
   * @return a {@link java.util.List} object.
   */
  public static <T> List<T> createInstances(final Set<Class<? extends T>> classes) {

    if (classes == null) return Collections.emptyList();

    return classes
        .stream()
        .map(c -> {
          try {
//            return Optional.of(c.newInstance());
            return Optional.of(c.getDeclaredConstructor().newInstance());
          } catch (InstantiationException
              | IllegalAccessException
              | NoSuchMethodException
              | InvocationTargetException e) {
            e.printStackTrace();
          }
          return Optional.<T>empty();
        })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.<T>toList());
  }


  /**
   * <p>createInstance.</p>
   *
   * @param clazz a {@link java.lang.Class} object.
   * @param <T> a T object.
   * @return a {@link java.util.Optional} object.
   */
  public static <T> Optional<T> createInstance(final Class<? extends T> clazz) {
    if (clazz == null) return Optional.empty();

    try {
      return Optional.of(clazz.getDeclaredConstructor().newInstance());
    } catch (InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }


}
