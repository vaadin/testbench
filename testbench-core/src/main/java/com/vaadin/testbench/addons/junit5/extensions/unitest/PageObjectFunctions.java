package com.vaadin.testbench.addons.junit5.extensions.unitest;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;
import com.vaadin.testbench.addons.junit5.pageobject.PageObject;

public interface PageObjectFunctions {

  String PAGE_OBJECT_PRELOAD = "preload";
  String PAGE_OBJECT_NAVIGATION_TARGET = "navigation-target";
  String PAGEOBJECT_STORAGE_KEY = "pageobject";

  static Function<ExtensionContext, PageObject> pageObject() {
    return (context) -> storeMethodPlain().apply(context).get(PAGEOBJECT_STORAGE_KEY , PageObject.class);
  }

  static BiConsumer<ExtensionContext, PageObject> storePageObject() {
    return (context , pageObject) -> storeMethodPlain().apply(context).put(PAGEOBJECT_STORAGE_KEY , pageObject);
  }

  static Consumer<ExtensionContext> removePageObject() {
    return (context) -> storeMethodPlain().apply(context).remove(PAGEOBJECT_STORAGE_KEY);
  }
}
