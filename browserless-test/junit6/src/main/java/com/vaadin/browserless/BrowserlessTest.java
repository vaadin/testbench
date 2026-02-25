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
package com.vaadin.browserless;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base JUnit 6 class for browserless tests.
 *
 * The class automatically scans classpath for routes and error views.
 * Subclasses should typically restrict classpath scanning to a specific
 * packages for faster bootstrap, by using {@link ViewPackages} annotation. If
 * the annotation is not present a full classpath scan is performed
 *
 * <pre>
 * {@code
 * &#64;ViewPackages(classes = {CartView.class, CheckoutView.class})
 * class CartViewTest extends BrowserlessTest {
 * }
 *
 * &#64;ViewPackages(packages = {"com.example.shop.cart", "com.example.security"})
 * class CartViewTest extends BrowserlessTest {
 * }
 *
 * &#64;ViewPackages(
 *    classes = {CartView.class, CheckoutView.class},
 *    packages = {"com.example.security"}
 * )
 * class CartViewTest extends BrowserlessTest {
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
public abstract class BrowserlessTest extends BaseBrowserlessTest
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
        return "JUnit 6";
    }
}
