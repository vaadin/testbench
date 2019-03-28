package com.vaadin.frp;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * <p>StreamFunctions interface.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public interface StreamFunctions {

  /**
   * <p>streamFilter.</p>
   *
   * @param <T> a T object.
   * @return a {@link Function} object.
   */
  static <T> Function<Predicate<T>, Function<Stream<T>, Stream<T>>> streamFilter() {
    return (filter) -> (Function<Stream<T>, Stream<T>>) inputStream -> inputStream.filter(filter);
  }
}
