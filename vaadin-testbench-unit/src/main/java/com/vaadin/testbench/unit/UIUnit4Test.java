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

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.vaadin.flow.component.UI;
import com.vaadin.testbench.unit.internal.PrettyPrintTree;

/**
 * Base JUnit 4 class for UI unit tests.
 *
 * The class automatically scans classpath for routes and error views.
 * Subclasses should typically restrict classpath scanning to a specific
 * packages for faster bootstrap, by using {@link ViewPackages} annotation. If
 * the annotation is not present a full classpath scan is performed
 *
 * <pre>
 * {@code
 * &#64;ViewPackages(classes = {CartView.class, CheckoutView.class})
 * public class CartViewTest extends UIUnit4Test {
 * }
 *
 * &#64;ViewPackages(packages = {"com.example.shop.cart", "com.example.security"})
 * public class CartViewTest extends UIUnit4Test {
 * }
 *
 * &#64;ViewPackages(
 *    classes = {CartView.class, CheckoutView.class},
 *    packages = {"com.example.security"}
 * )
 * public class CartViewTest extends UIUnit4Test {
 * }
 * </pre>
 *
 *
 * Set up of Vaadin environment is performed before each test by {@link
 * #initVaadinEnvironment()} method, and will be executed before {@code @Before}
 * methods defined in subclasses. At the same way, cleanup tasks operated by
 * {@link #cleanVaadinEnvironment()} are executed after each test, and after all
 * {@code @After} annotated methods in subclasses.
 *
 * Custom Flow service implementations supported by {@link
 * com.vaadin.flow.di.Lookup} SPI can be provided overriding {@link
 * #initVaadinEnvironment()} and passing to super implementation the service
 * classes that should be initialized during setup.
 *
 * <pre>
 * {@code
 * &#64;Override
 * public void initVaadinEnvironment() {
 *     super.initVaadinEnvironment(CustomInstantiatorFactory.class);
 * }
 * }
 * </pre>
 * <p/>
 * To get a graphical ascii representation of the UI tree on failure override
 * the {@link #printTree()} method to return true.
 */
public abstract class UIUnit4Test extends BaseUIUnitTest
        implements TesterWrappers {

    /**
     * Override to return true to get component tree output into log on test
     * failure.
     *
     * @return {@code true} to print component tree
     */
    public boolean printTree() {
        return false;
    }

    @Before
    public void initVaadinEnvironment() {
        super.initVaadinEnvironment();
    }

    @Override
    protected final String testingEngine() {
        return "JUnit 4";
    }

    @Rule
    public TestRule treeOnFailure = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            if (printTree()) {
                final String prettyPrintTree = PrettyPrintTree.Companion
                        .ofVaadin(UI.getCurrent()).print();
                System.out.println("Test " + description.getTestClass() + "::"
                        + description.getMethodName()
                        + " failed with the tree:\n" + prettyPrintTree);
            }
        }

        @Override
        protected void finished(Description description) {
            // Watcher handles cleaning of environment instead of After.
            // Else the UI has been cleared, and we can not build the tree.
            cleanVaadinEnvironment();
        }
    };
}
