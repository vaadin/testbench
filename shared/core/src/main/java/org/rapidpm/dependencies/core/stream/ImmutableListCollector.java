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
package org.rapidpm.dependencies.core.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * <p>ImmutableListCollector class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class ImmutableListCollector {
  private ImmutableListCollector() {
  }

  /**
   * <p>toImmutableList.</p>
   *
   * @param collectionFactory a {@link java.util.function.Supplier} object.
   * @param <T> a T object.
   * @param <A> a A object.
   * @return a {@link java.util.stream.Collector} object.
   */
  public static <T, A extends List<T>> Collector<T, A, List<T>> toImmutableList(Supplier<A> collectionFactory) {
    return Collector.of(collectionFactory, List::add, (left, right) -> {
      left.addAll(right);
      return left;
    }, Collections::unmodifiableList);
  }

  /**
   * <p>toImmutableList.</p>
   *
   * @param <T> a T object.
   * @return a {@link java.util.stream.Collector} object.
   */
  public static <T> Collector<T, List<T>, List<T>> toImmutableList() {
    return toImmutableList(ArrayList::new);
  }
}

