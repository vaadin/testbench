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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base JUnit 5 class for UI unit tests.
 *
 * Subclasses should typically restrict classpath scanning to a specific package
 * for faster bootstrap by overriding {@link #scanPackage()} method.
 *
 * Set up of Vaadin environment is performed before each test by
 * {@link #initVaadinEnvironment()} method, and will be executed before
 * {@code @BeforeEach} methods defined in subclasses. At the same way, cleanup
 * tasks operated by {@link #cleanVaadinEnvironment()} are executed after each
 * test, and after all {@code @AfterEach} annotated methods in subclasses.
 *
 * Usually, it is not necessary to override {@link #initVaadinEnvironment()} or
 * {@link #cleanVaadinEnvironment()} methods, but if this is done it is
 * mandatory to add the {@code @BeforeEach} and {@code @AfterEach} annotations
 * in the subclass, in order to have hooks handled by testing framework.
 *
 * A use case for overriding {@link #initVaadinEnvironment()} is to provide
 * custom Flow service implementations supported by
 * {@link com.vaadin.flow.di.Lookup} SPI. Implementations can be provided
 * overriding {@link #initVaadinEnvironment()} and passing to super
 * implementation the service classes that should be initialized during setup.
 *
 * <pre>
 * {@code
 * &#64;BeforeEach
 * &#64;Override
 * void initVaadinEnvironment() {
 *     super.initVaadinEnvironment(CustomInstantiatorFactory.class);
 * }
 * }
 * </pre>
 */
public abstract class UIUnitTest extends BaseUIUnitTest {

    @BeforeEach
    protected void initVaadinEnvironment() {
        super.initVaadinEnvironment();
    }

    @AfterEach
    @Override
    protected void cleanVaadinEnvironment() {
        super.cleanVaadinEnvironment();
    }
}
