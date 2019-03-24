package com.vaadin.frp.functions;

import static com.vaadin.frp.ExceptionFunctions.message;

import java.util.function.Function;

import com.vaadin.frp.model.Result;

/**
 * Created by svenruppert on 25.04.17.
 */
@FunctionalInterface
public interface CheckedFunction<T, R> extends Function<T, Result<R>> {
  @Override
  default Result<R> apply(T t) {
    try {
      return Result.success(applyWithException(t));
    } catch (Exception e) {
      return Result.failure(message().apply(e));
    }
  }
  R applyWithException(T t) throws Exception;
}