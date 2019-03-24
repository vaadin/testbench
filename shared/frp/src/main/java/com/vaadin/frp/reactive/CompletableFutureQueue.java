package com.vaadin.frp.reactive;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * <p>CompletableFutureQueue class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class CompletableFutureQueue<T, R> {

  private Function<T, CompletableFuture<R>> resultFunction;

  private CompletableFutureQueue(Function<T, CompletableFuture<R>> resultFunction) {
    this.resultFunction = resultFunction;
  }

  /**
   * <p>define.</p>
   *
   * @param transformation a {@link Function} object.
   * @param <T>            a T object.
   * @param <R>            a R object.
   * @return a {@link CompletableFutureQueue} object.
   */
  public static <T, R> CompletableFutureQueue<T, R> define(Function<T, R> transformation) {
    return new CompletableFutureQueue<>(t -> CompletableFuture.completedFuture(transformation.apply(t)));
  }

  /**
   * <p>thenCombineAsync.</p>
   *
   * @param nextTransformation a {@link Function} object.
   * @param <N>                a N object.
   * @return a {@link CompletableFutureQueue} object.
   */
  public <N> CompletableFutureQueue<T, N> thenCombineAsync(Function<R, N> nextTransformation) {
    final Function<T, CompletableFuture<N>> f = this.resultFunction
        .andThen(before -> before.thenComposeAsync(v -> supplyAsync(() -> nextTransformation.apply(v))));
    return new CompletableFutureQueue<>(f);
  }


//TODO : how to combine a list of CF ?

  public <N> CompletableFutureQueue<T, N> thenCombineAsyncFromArray(Function<R, N>[] nextTransformations) {
    CompletableFutureQueue cfq = this;
    for (Function<R, N> nextTransformation : nextTransformations) {
      cfq = cfq.thenCombineAsync(nextTransformation);
    }
    return cfq;
  }

//  public <N> CompletableFutureQueue<T, N> thenCombineAsync(Collection<Function<R, N>> nextTransformations) {
//
//    nextTransformations
//        .forEach(nextTransformation -> {
//      this.resultFunction = this.resultFunction
//          .andThen(before -> before.thenComposeAsync(v -> supplyAsync(() -> nextTransformation.apply(v))));
//    });
//
//
//    return new CompletableFutureQueue<>(this.resultFunction);
//  }
//
//
//
//
//  public <N> CompletableFutureQueue<T, N> thenCombineAsync(Function<R, N> firstTransformation, Function<R, N>... nextTransformations) {
//    final Function<T, CompletableFuture<N>> f = this.resultFunction
//        .andThen(before -> before.thenComposeAsync(v -> supplyAsync(() -> firstTransformation.apply(v))));
//
//    if (nextTransformations != null) {
//      Arrays
//          .stream(nextTransformations)
//          .map(nf -> this.resultFunction.andThen(before -> before.thenComposeAsync(v -> supplyAsync(() -> nf.apply(v)))))
//          .forEach(nF -> { /** don something **/ });
//
//    }
//    return new CompletableFutureQueue<>(f);
//  }

  /**
   * <p>resultFunction.</p>
   *
   * @return a {@link Function} object.
   */
  public Function<T, CompletableFuture<R>> resultFunction() {
    return this.resultFunction;
  }
}
