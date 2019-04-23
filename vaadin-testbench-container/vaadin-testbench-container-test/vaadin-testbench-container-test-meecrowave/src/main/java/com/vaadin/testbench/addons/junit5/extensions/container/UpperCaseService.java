package com.vaadin.testbench.addons.junit5.extensions.container;

import javax.enterprise.context.Dependent;

@Dependent
public class UpperCaseService {

    public String upperCase(String txt) {
        return txt.toUpperCase();
    }
}
