/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A retry rule is used to re-run a test several times in case of a random
 * failure. The test passes as soon as one attempt is executed without any
 * errors, i.e. it is only run as many times as needed. The maximum number of
 * attempts is specified in the constructor.
 *
 * @since 5.0
 */
public class RetryRule implements TestRule {
    private int maxAttempts;

    /**
     *
     * Constructs the retry rule with a maximum number of attempts. The maximum
     * number of attempts specifies how many times the test will be run in case
     * of a random failure.
     *
     * @param maxAttempts
     *            a maximum number of attempts.
     */
    public RetryRule(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    /**
     * Gets the maximum number of times to run the test.
     *
     * @return the maximum number of times to run the test.
     */
    public int getMaxAttempts() {
        return maxAttempts;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        if (maxAttempts > 1) {
            return statement(base, description);
        } else {
            return base;
        }

    }

    private Statement statement(final Statement base,
            final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                List<Throwable> caughtThrowables = new ArrayList<>();
                for (int i = 0; i < maxAttempts; i++) {
                    try {
                        base.evaluate();
                        return;
                    } catch (AssumptionViolatedException t) {
                        throw t;
                    } catch (Throwable t) {
                        caughtThrowables.add(t);
                    }
                }
                String testDisplayName = description.getDisplayName();
                Logger logger = LoggerFactory.getLogger(RetryRule.class);
                if (logger.isDebugEnabled() && caughtThrowables.size() > 1) {
                    logger.debug("Caught {} exceptions for {} test",
                            caughtThrowables.size(), testDisplayName);
                    AtomicInteger attempt = new AtomicInteger();
                    caughtThrowables
                            .forEach(t -> logger.debug("\t{} [attempt {}]: {}",
                                    testDisplayName, attempt.incrementAndGet(),
                                    t.getMessage(), t));
                }
                Throwable lastCaught = caughtThrowables
                        .get(caughtThrowables.size() - 1);
                String errorMessage = String.format("%s: run failed %s times",
                        testDisplayName, maxAttempts);
                throw new RuntimeException(errorMessage, lastCaught);
            }
        };
    }
}
