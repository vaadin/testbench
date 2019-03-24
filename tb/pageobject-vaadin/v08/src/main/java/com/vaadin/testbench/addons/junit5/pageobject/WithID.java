package com.vaadin.testbench.addons.junit5.pageobject;

import com.vaadin.testbench.elementsbase.AbstractElement;

/**
 *
 */
@FunctionalInterface
public interface WithID<T extends AbstractElement> {
  T id(String id);
}
