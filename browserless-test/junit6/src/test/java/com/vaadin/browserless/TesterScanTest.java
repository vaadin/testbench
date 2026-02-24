/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
