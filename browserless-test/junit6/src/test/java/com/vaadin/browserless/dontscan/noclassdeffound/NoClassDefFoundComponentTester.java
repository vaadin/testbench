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
