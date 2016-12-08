package com.vaadin.testbench.annotations;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RetryRule implements TestRule {
    private int retryCount;

    public RetryRule(int retryCount) {
        this.retryCount = retryCount;
    }

    public Statement apply(Statement base, Description description) {
        if(retryCount>1) {
            return statement(base, description);
        } else {
            return base;
        }

    }

    private Statement statement(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable caughtThrowable = null;
                // implement retry logic here
                for (int i = 0; i < retryCount; i++) {
                    try {
                        base.evaluate();
                        return;
                    } catch (Throwable t) {
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
}