package com.vaadin.testbench.addons.junit5.pageobject;

import com.vaadin.testbench.TestBenchElement;

@FunctionalInterface
public interface WithID<T extends TestBenchElement> {
    T id(String id);
}
