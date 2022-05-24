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

import org.junit.After;
import org.junit.Before;

/**
 * Base JUnit 4 class for UI unit tests.
 *
 * Subclasses should typically restrict classpath scanning to a specific package
 * for faster bootstrap by overriding {@link #scanPackage()} method.
 *
 * Set up of Vaadin environment is performed before each test by
 * {@link #initVaadinEnvironment()}} method, and will be executed before
 * {@code @Before} methods defined in subclasses. At the same way, cleanup tasks
 * operated by {@link #cleanVaadinEnvironment()} are executed after each test,
 * and after all {@code @After} annotated methods in subclasses.
 *
 * Custom Flow service implementations supported by
 * {@link com.vaadin.flow.di.Lookup} SPI can be provided overriding
 * {@link #initVaadinEnvironment()} and passing to super implementation the
 * service classes that should be initialized during setup.
 *
 * <pre>
 * {@code
 * &#64;Override
 * public void initVaadinEnvironment() {
 *     super.initVaadinEnvironment(CustomInstantiatorFactory.class);
 * }
 * }
 * </pre>
 */
public abstract class UIUnit4Test extends BaseUIUnitTest {

    @Before
    public void initVaadinEnvironment() {
        super.initVaadinEnvironment();
    }

    @After
    @Override
    public void cleanVaadinEnvironment() {
        super.cleanVaadinEnvironment();
    }
}
