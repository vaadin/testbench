package com.vaadin.testbench.addons.framework;

import java.io.Serializable;

@FunctionalInterface
public interface Registration extends Serializable {

    boolean remove();
}
