/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

@Tests(TestComponentForConcreteTester.class)
public class NonGenericTestTester
        extends ComponentTester<TestComponentForConcreteTester> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public NonGenericTestTester(TestComponentForConcreteTester component) {
        super(component);
    }
}
