package com.vaadin.frp.matcher;

import com.vaadin.frp.model.Pair;
import com.vaadin.frp.model.Result;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class Case<T> extends Pair<Supplier<Boolean>, Supplier<Result<T>>> {

    /**
     * <p>Constructor for Case.</p>
     *
     * @param booleanSupplier a {@link Supplier} object.
     * @param resultSupplier  a {@link Supplier} object.
     */
    public Case(final Supplier<Boolean> booleanSupplier, final Supplier<Result<T>> resultSupplier) {
        super(booleanSupplier, resultSupplier);
    }

    /**
     * <p>matchCase.</p>
     *
     * @param condition a {@link Supplier} object.
     * @param value     a {@link Supplier} object.
     * @param <T>       a T object.
     * @return a {@link Case} object.
     */
    public static <T> Case<T> matchCase(Supplier<Boolean> condition,
                                        Supplier<Result<T>> value) {
        return new Case<>(condition, value);
    }

    /**
     * <p>matchCase.</p>
     *
     * @param value a {@link Supplier} object.
     * @param <T>   a T object.
     * @return a {@link DefaultCase} object.
     */
    public static <T> DefaultCase<T> matchCase(Supplier<Result<T>> value) {
        return new DefaultCase<>(() -> true, value);
    }

    /**
     * <p>match.</p>
     *
     * @param defaultCase a {@link DefaultCase} object.
     * @param matchers    a {@link Case} object.
     * @param <T>         a T object.
     * @return a {@link Result} object.
     */
    @SafeVarargs
    public static <T> Result<T> match(DefaultCase<T> defaultCase, Case<T>... matchers) {
        return Stream
                .of(matchers)
                .filter(Case::isMatching)
                .map(Case::result)
                .findFirst()
                .orElseGet(defaultCase::result);
    }

    /**
     * <p>isMatching.</p>
     *
     * @return a boolean.
     */
    private boolean isMatching() {
        return getT1().get();
    }

    /**
     * <p>result.</p>
     *
     * @return a {@link Result} object.
     */
    public Result<T> result() {
        return getT2().get();
    }

    public static class DefaultCase<T> extends Case<T> {
        public DefaultCase(final Supplier<Boolean> booleanSupplier, final Supplier<Result<T>> resultSupplier) {
            super(booleanSupplier, resultSupplier);
        }
    }
}
