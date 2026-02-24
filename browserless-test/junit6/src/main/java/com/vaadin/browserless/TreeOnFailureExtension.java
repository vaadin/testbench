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
package com.vaadin.testbench.unit;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.vaadin.flow.component.UI;
import com.vaadin.testbench.unit.internal.PrettyPrintTree;

/**
 * JUnit5+ extension that will collect and output the component tree for the
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
