/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.testbench.annotations;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * JUnit test runner that allows to specify the amount retries
 * for running a JUnit test.
 * The test is marked passed, if it passed at least once.
 *
 * <p>
 *  Should be used to ignore random failures, such as
 *  client-server sync, network problems.
 *
 *
 * </p>
 */
public class RetryTestRunner extends BlockJUnit4ClassRunner {
    private final Class<?> type;

    public RetryTestRunner(final Class<?> type) throws InitializationError {
        super(type);
        this.type = type;
    }

    @Override
    protected void runChild(final FrameworkMethod method,
                            final RunNotifier notifier) {
        Statement baseStatement = this.methodBlock(method);
        Description description = describeChild(method);
        int retryCount = repeatCount(method);
        if (retryCount> 1) {
            Statement statement =
                    statement(baseStatement, description, retryCount);
            super.runLeaf(statement, description, notifier);
        } else {
            super.runLeaf(baseStatement, description, notifier);
        }
    }

    private Statement statement(final Statement base,
                                final Description description,
                                final int retryCount) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable caughtThrowable = null;
                for (int i = 0; i < retryCount; i++) {
                    try {
                        base.evaluate();
                        return;
                    } catch (Throwable t) {
                        //NOP
                        caughtThrowable = t;
                    }
                }
                System.err.println(String.format("%s: run failed %s times",
                                description.getDisplayName(),
                                retryCount));
                throw caughtThrowable;
            }
        };
    }

    private static boolean isMethodRetry(final FrameworkMethod method) {
        return method.getAnnotation(Retry.class) != null;
    }

    private static boolean isClassRetry(final Class<?> type) {
        return type.getAnnotation(Retry.class) != null;
    }

    private static int getRetries(final FrameworkMethod method) {
        return method.getAnnotation(Retry.class).count();
    }

    private static int getRetries(final Class<?> type) {
        return type.getAnnotation(Retry.class).count();
    }

    private int repeatCount(final FrameworkMethod method) {
        if (isMethodRetry(method)) {
            return getRetries(method);
        }
        if (isClassRetry(type)) {
            return getRetries(type);
        }
        return 1;
    }
}
