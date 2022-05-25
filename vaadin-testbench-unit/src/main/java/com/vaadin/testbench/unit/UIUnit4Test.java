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
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.testbench.unit.internal.PrettyPrintTree;

/**
 * Base JUnit 4 class for UI unit tests.
 *
 * Subclasses should typically restrict classpath scanning to a specific package
 * for faster bootstrap by overriding {@link #scanPackage()} method.
 *
 * Set up of Vaadin environment is performed before each test by
 * {@link #initVaadinEnvironment()} method, and will be executed before
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
 * <p/>
 * To get a graphical ascii representation of the UI tree on failure override
 * the {@link #printTree()} method to return true.
 */
public abstract class UIUnit4Test extends BaseUIUnitTest {

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
