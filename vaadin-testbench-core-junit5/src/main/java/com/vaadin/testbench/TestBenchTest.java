/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.vaadin.testbench.capabilities.CapabilitiesInvocationContextProvider;

/**
 * Shorthand annotation that combines {@code @TestTemplate} together with
 * {@code @ExtendWith(CapabilitiesInvocationContextProvider.class)}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
@ExtendWith(CapabilitiesInvocationContextProvider.class)
public @interface TestBenchTest {

}
