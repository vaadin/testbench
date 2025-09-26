/*
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.dontscan.noclassdeffound;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Dummy tester referencing a class from a third-party JAR that might not be
 * available at runtime. This is used to verify that scanning for testers does
 * not fail when the referenced component class is not on the classpath.
 */
@Tests(NoClassDefFoundComponent.class)
public class NoClassDefFoundComponentTester<T extends NoClassDefFoundComponent>
        extends ComponentTester<T> {

    public NoClassDefFoundComponentTester(T component) {
        super(component);
    }
}
