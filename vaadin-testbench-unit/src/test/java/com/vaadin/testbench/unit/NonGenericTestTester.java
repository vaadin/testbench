/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
