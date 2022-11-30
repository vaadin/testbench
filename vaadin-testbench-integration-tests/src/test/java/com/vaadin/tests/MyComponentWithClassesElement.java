/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;

@Attribute(name = "class", contains = Attribute.SIMPLE_CLASS_NAME)
@Attribute(name = "class", contains = "my-component-first")
public class MyComponentWithClassesElement extends TestBenchElement {

}
