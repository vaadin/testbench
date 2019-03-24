package com.vaadin.testbench.addons.junit5.extensions;

import static com.vaadin.frp.matcher.Case.match;
import static com.vaadin.frp.matcher.Case.matchCase;
import static com.vaadin.frp.model.Result.failure;
import static com.vaadin.frp.model.Result.ofNullable;
import static com.vaadin.frp.model.Result.success;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import com.vaadin.dependencies.core.logger.Logger;
import com.vaadin.frp.Transformations;
import com.vaadin.frp.functions.TriFunction;
import com.vaadin.frp.model.Result;

/**
 *
 */
public interface ExtensionFunctions {

  Namespace NAMESPACE_GLOBAL = Namespace.create("global");

  static Function<ExtensionContext, Namespace> namespaceForMethod() {
    return (ctx) -> {
      String name = ctx.getTestClass().get().getName();
      String methodName = ctx.getTestMethod().get().getName();
      Namespace namespace = Namespace.create(ExtensionFunctions.class ,
                                             name ,
                                             methodName
      );
      return namespace;
    };
  }

  static Function<ExtensionContext, Namespace> namespaceForClass() {
    return (ctx) -> {
      String name = ctx.getTestClass().get().getName();
      Namespace namespace = Namespace.create(ExtensionFunctions.class ,
                                             name
      );
      return namespace;
    };
  }

  static Function<ExtensionContext, Boolean> isMethodCtx() {
    return (ctx) -> ctx.getTestMethod().isPresent();
  }

  static Function<ExtensionContext, Boolean> isClassCtx() {
    return (ctx) -> ctx.getTestMethod().isPresent();
  }

  static Function<ExtensionContext, Store> storeGlobalPlain() {
    return (context) -> context.getStore(NAMESPACE_GLOBAL);
  }

  static Function<ExtensionContext, Store> storeClassPlain() {
    return (context) -> context.getStore(namespaceForClass().apply(context));
  }

  static Function<ExtensionContext, Store> storeMethodPlain() {
    return (context) -> context.getStore(namespaceForMethod().apply(context));
  }


  static Function<ExtensionContext, Result<Store>> nearestStore() {
    return (ctx) -> match(
        matchCase(() -> ofNullable(storeGlobalPlain().apply(ctx))) ,
        matchCase(() -> isMethodCtx().apply(ctx) , () -> success(storeMethodPlain().apply(ctx))) ,
        matchCase(() -> isClassCtx().apply(ctx) , () -> success(storeClassPlain().apply(ctx)))
    );
  }

  static Function<Store, Boolean> contains(String key) {
    return (store) -> store.get(key) != null;
  }

  static Function<ExtensionContext, BiConsumer<String, Object>> addToMethodStore() {
    return (context) -> (key , value) -> storeMethodPlain().apply(context).put(key , value);
  }

  static Function<ExtensionContext, BiConsumer<String, Object>> addToClassStore() {
    return (context) -> (key , value) -> storeClassPlain().apply(context).put(key , value);
  }

  static Function<ExtensionContext, BiConsumer<String, Object>> addToGlobalStore() {
    return (context) -> (key , value) -> storeGlobalPlain().apply(context).put(key , value);
  }


  static Function<ExtensionContext, Consumer<String>> removeFromMethodStore() {
    return (context) -> (key) -> storeMethodPlain().apply(context).remove(key);
  }

  static Function<ExtensionContext, Consumer<String>> removeFromClassStore() {
    return (context) -> (key) -> storeClassPlain().apply(context).remove(key);
  }

  static Function<ExtensionContext, Consumer<String>> removeFromGlobal() {
    return (context) -> (key) -> storeGlobalPlain().apply(context).remove(key);
  }

  static <T> TriFunction<Class<T>, Consumer<T>, String, Consumer<ExtensionContext>> removeAndCloseInMethod() {
    return (type , howToClose , key) -> (ctx) -> {
      final Store store = storeMethodPlain().apply(ctx);
      if (store.get(key) != null) {
        ofNullable(store.remove(key , type))
            .ifPresent(howToClose);
      }
    };
  }


  static <T> Function<String, BiFunction<Class<T>, Consumer<T>, Consumer<ExtensionContext>>> removeAndClose() {
    return (key) -> (type , howToClose) -> (ctx) -> {
      if (ctx.getTestMethod().isPresent()) {
        final Store store = storeMethodPlain().apply(ctx);
        if (store.get(key) != null)
          ofNullable(store.get(key , type))
              .ifPresent(howToClose);
      }
    };
  }


  /**
   * Will deliver a value (typed) from
   * 1. method Store
   * 2. class Store
   * 3. Global Store
   * or fail
   *
   * @param <T>
   * @return
   */
  static <T> TriFunction<Class<T>, String, ExtensionContext, Result<T>> value() {


    return (type , key , ctx) -> match(
        matchCase(() -> failure("no key value pair found key -> " + key)) ,
        matchCase(() -> isMethodCtx().apply(ctx) &&
                        storeMethodPlain().apply(ctx).get(key) != null ,
                  () -> success(storeMethodPlain().apply(ctx).get(key , type))) ,
        matchCase(() -> isClassCtx().apply(ctx) &&
                        storeClassPlain().apply(ctx).get(key) != null ,
                  () -> success(storeClassPlain().apply(ctx).get(key , type))) ,
        matchCase(() -> storeGlobalPlain().apply(ctx).get(key) != null ,
                  () -> success(storeGlobalPlain().apply(ctx).get(key , type)))
    );
  }

  static <T> TriFunction<Class<T>, String, ExtensionContext, T> valuePlain() {
    return (type , key , ctx) -> ExtensionFunctions.<T>value()
        .apply(type , key , ctx)
        .ifFailed(failed -> Logger.getLogger(ExtensionFunctions.class).warning(failed))
        .get();
  }

  /**
   * Curried Version of the Function value and reduced um one level by type
   *
   * @param type
   * @param <T>
   * @return
   */
  static <T> Function<String, Function<ExtensionContext, Result<T>>> valueTyped(Class<T> type) {
    return (key) -> Transformations.<Class<T>, String, ExtensionContext, Result<T>>curryTriFunction()
        .apply(value())
        .apply(type)
        .apply(key);
  }

  /**
   * Curried Version of the Function valueTyped and reduced um one level by type of Integer
   *
   * @return
   */
  static Function<String, Function<ExtensionContext, Result<Integer>>> valueAsInt() {
    return (key) -> (ctx) -> valueTyped(Integer.class).apply(key).apply(ctx);
  }

  static Function<String, Function<ExtensionContext, Result<Double>>> valueAsDouble() {
    return (key) -> (ctx) -> valueTyped(Double.class).apply(key).apply(ctx);
  }

  static Function<String, Function<ExtensionContext, Result<Boolean>>> valueAsBoolean() {
    return (key) -> (ctx) -> valueTyped(Boolean.class).apply(key).apply(ctx);
  }

  static Function<String, Function<ExtensionContext, Result<String>>> valueAsString() {
    return (key) -> (ctx) -> valueTyped(String.class).apply(key).apply(ctx);
  }


  static <T> Function<String, Function<ExtensionContext, T>> valueTypedPlain(Class<T> type) {
    return (key) -> (ctx) -> valueTyped(type)
        .apply(key)
        .apply(ctx)
        .ifFailed(failed -> Logger.getLogger(ExtensionFunctions.class)
                                  .warning(failed))
        .get();
  }

  static Function<String, Function<ExtensionContext, Integer>> valueAsIntPlain() {
    return (key) -> (ctx) -> valueAsInt()
        .apply(key)
        .apply(ctx)
        .ifFailed(failed -> Logger.getLogger(ExtensionFunctions.class)
                                  .warning(failed))
        .get();
  }

  static Function<String, Function<ExtensionContext, Double>> valueAsDoublePlain() {
    return (key) -> (ctx) -> valueAsDouble()
        .apply(key)
        .apply(ctx)
        .ifFailed(failed -> Logger.getLogger(ExtensionFunctions.class)
                                  .warning(failed))
        .get();
  }

  static Function<String, Function<ExtensionContext, Boolean>> valueAsBooleanPlain() {
    return (key) -> (ctx) -> valueAsBoolean()
        .apply(key)
        .apply(ctx)
        .ifFailed(failed -> Logger.getLogger(ExtensionFunctions.class)
                                  .warning(failed))
        .get();
  }

  static Function<String, Function<ExtensionContext, String>> valueAsStringPlain() {
    return (key) -> (ctx) -> valueAsString()
        .apply(key)
        .apply(ctx)
        .ifFailed(failed -> Logger.getLogger(ExtensionFunctions.class)
                                  .warning(failed))
        .get();
  }


}
