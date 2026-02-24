/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// Ensure that scanning testers does not fail when a tester references
// a component class that is not present on the classpath.
public class TesterScanTest {

    @Test
    public void scanForTesters_testerForClassNotInClasspath_doNotThrowOnClassNotFoundException() {
        // Loads a dummy tester annotated with @Tests using an FQN to a
        // non-existing component class.
        Assertions.assertDoesNotThrow(() -> BaseUIUnitTest
                .scanForTesters("com.vaadin.testbench.dontscan.classnotfound"));
    }

    @Test
    public void scanForTesters_testerForClassNotInClasspath_doNotThrowNoClassDefFound() {
        // Loads a dummy tester annotated with @Tests referencing a class in
        // another module with provided scope so the test itself is not able to
        // load the class.
        Assertions.assertDoesNotThrow(() -> BaseUIUnitTest.scanForTesters(
                "com.vaadin.testbench.dontscan.noclassdeffound"));
    }

    @Test
    public void scanForTesters_testerForClassNotInClasspath_doNotThrowTypeNotPresentException() {
        // Loads a dummy tester annotated with @Tests referencing a class in
        // another module with provided scope so the test itself is not able to
        // load the class.
        Assertions.assertDoesNotThrow(() -> BaseUIUnitTest.scanForTesters(
                "com.vaadin.testbench.dontscan.typenotpresent"));
    }

}
