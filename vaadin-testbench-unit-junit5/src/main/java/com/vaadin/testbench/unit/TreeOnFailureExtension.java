/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.vaadin.flow.component.UI;
import com.vaadin.testbench.unit.internal.PrettyPrintTree;

/**
 * JUnit5 extension that will collect and output the component tree for the
 * failing test UI.
 * <p>
 * This can help with identifying a problem that has happened in the test where
 * a component is missing or has faulty data.
 */
public class TreeOnFailureExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        boolean testFailed = extensionContext.getExecutionException()
                .isPresent();
        if (testFailed) {
            final String prettyPrintTree = PrettyPrintTree.Companion
                    .ofVaadin(UI.getCurrent()).print();
            extensionContext.publishReportEntry("Test "
                    + extensionContext.getTestClass().get().getSimpleName()
                    + "::" + extensionContext.getTestMethod().get().getName()
                    + " failed with the tree:\n" + prettyPrintTree);
        }
    }
}
