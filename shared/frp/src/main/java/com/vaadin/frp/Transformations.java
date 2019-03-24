package com.vaadin.frp;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.vaadin.frp.functions.CheckedBiFunction;
import com.vaadin.frp.functions.CheckedFunction;
import com.vaadin.frp.functions.CheckedTriFunction;
import com.vaadin.frp.functions.TriFunction;

/**
 * Created by svenruppert on 24.04.17.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public interface Transformations {


  /**
   * <p>not.</p>
   *
   * @return a {@link Function} object.
   */
  static Function<Boolean, Boolean> not() {
    return (input) -> !input;
  }


  /**
   * <p>higherCompose.</p>
   *
   * @param <T> a T object.
   * @param <U> a U object.
   * @param <V> a V object.
   * @return a {@link Function} object.
   */
  static <T, U, V> Function<Function<U, V>, Function<Function<T, U>, Function<T, V>>> higherCompose() {
    return (Function<U, V> f) -> (Function<T, U> g) -> (T x) -> f.apply(g.apply(x));
  }

  /**
   * <p>enumToStream.</p>
   *
   * @param <T> a T object.
   * @return a {@link Function} object.
   */
  static <T> Function<Enumeration<T>, Stream<T>> enumToStream() {
    return (e) ->
        StreamSupport
            .stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
              public T next() {
                return e.nextElement();
              }

              public boolean hasNext() {
                return e.hasMoreElements();
              }
            }, Spliterator.ORDERED), false);
  }


  /**
   * <p>curryBiFunction.</p>
   *
   * @param <A> a A object.
   * @param <B> a B object.
   * @param <R> a R object.
   * @return a {@link Function} object.
   */
  static <A, B, R> Function<BiFunction<A, B, R>, Function<A, Function<B, R>>> curryBiFunction() {
    return (func) -> a -> b -> func.apply(a, b);
  }

  /**
   * <p>curryCheckedBiFunction.</p>
   *
   * @param <A> a A object.
   * @param <B> a B object.
   * @param <R> a R object.
   * @return a {@link Function} object.
   */
  static <A, B, R> Function<CheckedBiFunction<A, B, R>, Function<A, CheckedFunction<B, R>>> curryCheckedBiFunction() {
    return (func) -> a -> b -> func.applyWithException(a, b);
  }

  /**
   * <p>unCurryBifunction.</p>
   *
   * @param <A> a A object.
   * @param <B> a B object.
   * @param <R> a R object.
   * @return a {@link Function} object.
   */
  static <A, B, R> Function<Function<A, Function<B, R>>, BiFunction<A, B, R>> unCurryBiFunction() {
    return (func) -> (a, b) -> func.apply(a).apply(b);
  }

  /**
   * <p>unCurryCheckedBifunction.</p>
   *
   * @param <A> a A object.
   * @param <B> a B object.
   * @param <R> a R object.
   * @return a {@link Function} object.
   */
  static <A, B, R> Function<Function<A, CheckedFunction<B, R>>, CheckedBiFunction<A, B, R>> unCurryCheckedBiFunction() {
    return (func) -> (a, b) -> func.apply(a).applyWithException(b);
  }

  /**
   * <p>curryTriFunction.</p>
   *
   * @param <A> a A object.
   * @param <B> a B object.
   * @param <C> a C object.
   * @param <R> a R object.
   * @return a {@link Function} object.
   */
  static <A, B, C, R> Function<
      TriFunction<A, B, C, R>,
      Function<A, Function<B, Function<C, R>>>> curryTriFunction() {
    return (func) -> a -> b -> c -> func.apply(a, b, c);
  }

  /**
   * <p>curryCheckedTriFunction.</p>
   *
   * @param <A> a A object.
   * @param <B> a B object.
   * @param <C> a C object.
   * @param <R> a R object.
   * @return a {@link Function} object.
   */
  static <A, B, C, R> Function<
      CheckedTriFunction<A, B, C, R>,
      Function<A, Function<B, CheckedFunction<C, R>>>> curryCheckedTriFunction() {
    return (func) -> a -> b -> c -> func.applyWithException(a, b, c);
  }

  /**
   * <p>unCurryTrifunction.</p>
   *
   * @param <A> a A object.
   * @param <B> a B object.
   * @param <C> a C object.
   * @param <R> a R object.
   * @return a {@link Function} object.
   */
  static <A, B, C, R> Function<
      Function<A, Function<B, Function<C, R>>>,
      TriFunction<A, B, C, R>> unCurryTriFunction() {
    return (func) -> (a, b, c) -> func.apply(a).apply(b).apply(c);
  }


  /**
   * <p>unCurryTrifunction.</p>
   *
   * @param <A> a A object.
   * @param <B> a B object.
   * @param <C> a C object.
   * @param <R> a R object.
   * @return a {@link Function} object.
   */
  static <A, B, C, R> Function<
      Function<A, Function<B, CheckedFunction<C, R>>>,
      CheckedTriFunction<A, B, C, R>> unCurryCheckedTriFunction() {
    return (func) -> (a, b, c) -> func.apply(a).apply(b).applyWithException(c);
  }


  //Function Casts

  /**
   * <p>not.</p>
   *
   * @param p   a {@link Predicate} object.
   * @param <T> a T object.
   * @return a {@link Predicate} object.
   */
  static <T> Predicate<T> not(Predicate<T> p) {
    return t -> !p.test(t);
  }

  /**
   * <p>asPredicate.</p>
   *
   * @param predicate a {@link Predicate} object.
   * @param <T>       a T object.
   * @return a {@link Predicate} object.
   */
  static <T> Predicate<T> asPredicate(Predicate<T> predicate) {
    return predicate;
  }

  /**
   * <p>asConsumer.</p>
   *
   * @param consumer a {@link Consumer} object.
   * @param <T>      a T object.
   * @return a {@link Consumer} object.
   */
  static <T> Consumer<T> asConsumer(Consumer<T> consumer) {
    return consumer;
  }

  /**
   * <p>asSupplier.</p>
   *
   * @param supplier a {@link Supplier} object.
   * @param <T>      a T object.
   * @return a {@link Supplier} object.
   */
  static <T> Supplier<T> asSupplier(Supplier<T> supplier) {
    return supplier;
  }

  /**
   * <p>asFunc.</p>
   *
   * @param function a {@link Function} object.
   * @param <T>      a T object.
   * @param <R>      a R object.
   * @return a {@link Function} object.
   */
  static <T, R> Function<T, R> asFunc(Function<T, R> function) {
    return function;
  }


  static <T, R> CheckedFunction<T, R> asCheckedFunc(Function<T, R> f) {
    return f::apply;
  }
}
