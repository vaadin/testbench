package com.vaadin.dependencies.core.stream;

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

