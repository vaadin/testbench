/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

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
