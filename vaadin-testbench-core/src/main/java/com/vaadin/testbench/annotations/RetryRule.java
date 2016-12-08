package com.vaadin.testbench.annotations;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A RetryRule specifies retry count, that is used to run same test several times.
 * Test passes when it finishes successfully at least once, while several repeated runs.
 * Repeating execution of the same test allows to avoid random failures.
 * The amount of retries is specified in the constructor.
 *
 * @since 4.2
 */
public class RetryRule implements TestRule {
    private int maxAttempts;

    /**
     *
     * @param maxAttempts specifies the amount of maximum attempts for running test
     */
    public RetryRule(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    /**
     *
     * @return the amount of maximum attempts for running test
     */
    public int getMaxAttempts() {
        return this.maxAttempts;
    }
    @Override
    public Statement apply(Statement base, Description description) {
        if(maxAttempts >1) {
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
                for (int i = 0; i < maxAttempts; i++) {
                    try {
                        base.evaluate();
                        return;
                    } catch (Throwable t) {
                        caughtThrowable = t;
                    }
                }
                String errorMessage = String.format("%s: run failed %s times",
                        description.getDisplayName(),
                        maxAttempts);
                throw new RuntimeException(errorMessage, caughtThrowable);
            }
        };
    }
}
