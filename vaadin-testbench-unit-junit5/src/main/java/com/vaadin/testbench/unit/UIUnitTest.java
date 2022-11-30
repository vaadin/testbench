/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base JUnit 5 class for UI unit tests.
 *
 * The class automatically scans classpath for routes and error views.
 * Subclasses should typically restrict classpath scanning to a specific
 * packages for faster bootstrap, by using {@link ViewPackages} annotation. If
 * the annotation is not present a full classpath scan is performed
 *
 * <pre>
 * {@code
 * &#64;ViewPackages(classes = {CartView.class, CheckoutView.class})
 * class CartViewTest extends UIUnitTest {
 * }
 *
 * &#64;ViewPackages(packages = {"com.example.shop.cart", "com.example.security"})
 * class CartViewTest extends UIUnitTest {
 * }
 *
 * &#64;ViewPackages(
 *    classes = {CartView.class, CheckoutView.class},
 *    packages = {"com.example.security"}
 * )
 * class CartViewTest extends UIUnitTest {
 * }
 * </pre>
 *
 * Set up of Vaadin environment is performed before each test by {@link
 * #initVaadinEnvironment()} method, and will be executed before
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
 * custom Flow service implementations supported by {@link
 * com.vaadin.flow.di.Lookup} SPI. Implementations can be provided overriding
 * {@link #initVaadinEnvironment()} and passing to super implementation the
 * service classes that should be initialized during setup.
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
 * <p/>
 * To get a graphical ascii representation of the UI tree on failure add the
 * annotation {@code @ExtendWith(TreeOnFailureExtension.class)} to the test
 * class.
 *
 * @see ViewPackages
 */
public abstract class UIUnitTest extends BaseUIUnitTest
        implements TesterWrappers {

    @BeforeEach
    protected void initVaadinEnvironment() {
        super.initVaadinEnvironment();
    }

    @AfterEach
    @Override
    protected void cleanVaadinEnvironment() {
        super.cleanVaadinEnvironment();
    }

    @Override
    protected final String testingEngine() {
        return "JUnit 5";
    }
}
