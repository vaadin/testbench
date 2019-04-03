package com.vaadin.frp.functions;

import com.vaadin.frp.model.Result;

import java.util.function.Supplier;

import static com.vaadin.frp.ExceptionFunctions.message;

/**
 * Created by svenruppert on 25.04.17.
 */
@FunctionalInterface
public interface CheckedSupplier<T> extends Supplier<Result<T>> {

    @Override
    default Result<T> get() {
        try {
            return Result.success(getWithException());
        } catch (Exception e) {
            return Result.failure(message().apply(e));
        }
    }

    T getWithException() throws Exception;

    default T getOrElse(Supplier<T> supplier) {
        return get().getOrElse(supplier);
    }
}
