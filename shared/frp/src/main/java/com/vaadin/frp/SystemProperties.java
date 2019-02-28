package com.vaadin.frp;

import static java.lang.System.getProperty;
import static com.vaadin.frp.Converting.convertToBoolean;
import static com.vaadin.frp.Converting.convertToDouble;
import static com.vaadin.frp.Converting.convertToInteger;
import static com.vaadin.frp.model.Result.ofNullable;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.vaadin.frp.model.Result;

public interface SystemProperties {

  static BiFunction<Class, String, String> qualifiedParameter() {
    return (clazz , unqualifiedName) -> clazz.getName() + "." + unqualifiedName;
  }

  static BiFunction<Class, String, Boolean> hasSystemProperty() {
    return qualifiedParameter().andThen(key -> getProperty(key) != null);
  }

  static Function<String, Boolean> hasSystemProperty(Class qualifier) {
    return (key) -> hasSystemProperty().apply(qualifier , key);
  }


  static BiFunction<Class, String, Result<String>> systemProperty() {
    return qualifiedParameter().andThen(key -> Result.ofNullable(getProperty(key)));
  }

  static BiFunction<Class, String, Result<String>> systemProperty(String defaultValue) {
    return qualifiedParameter().andThen(key -> Result.ofNullable(getProperty(key , defaultValue)));
  }


  static Function<String, Result<String>> systemProperty(Class qualifier) {
    return (key) -> qualifiedParameter()
        .andThen(k -> Result.ofNullable(getProperty(k)))
        .apply(qualifier , key);
  }

  static Function<String, Result<String>> systemProperty(Class qualifier , String defaultValue) {
    return (key) -> qualifiedParameter()
        .andThen(k -> Result.ofNullable(getProperty(k , defaultValue)))
        .apply(qualifier , key);
  }


  static Function<String, Result<Boolean>> systemPropertyBoolean(Class qualifier) {
    return (key) -> systemProperty(qualifier)
        .apply(key)
        .flatMap(Converting.convertToBoolean());
  }

  static Function<String, Result<Boolean>> systemPropertyBoolean(Class qualifier,String defaultValue) {
    return (key) -> systemProperty(qualifier, defaultValue)
        .apply(key)
        .flatMap(Converting.convertToBoolean());
  }

  static Function<String, Result<Integer>> systemPropertyInt(Class qualifier) {
    return (key) -> systemProperty(qualifier)
        .apply(key)
        .flatMap(Converting.convertToInteger());
  }

  static Function<String, Result<Integer>> systemPropertyInt(Class qualifier,String defaultValue) {
    return (key) -> systemProperty(qualifier, defaultValue)
        .apply(key)
        .flatMap(Converting.convertToInteger());
  }

  static Function<String, Result<Double>> systemPropertyDouble(Class qualifier) {
    return (key) -> systemProperty(qualifier)
        .apply(key)
        .flatMap(Converting.convertToDouble());
  }

  static Function<String, Result<Double>> systemPropertyDouble(Class qualifier,String defaultValue) {
    return (key) -> systemProperty(qualifier, defaultValue)
        .apply(key)
        .flatMap(Converting.convertToDouble());
  }

}
