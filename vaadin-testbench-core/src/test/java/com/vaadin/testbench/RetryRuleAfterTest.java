/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class RetryRuleAfterTest {
    private int afterExecutedCount = 0;

    @Rule
    public RetryRule retry = new RetryRule(3);

    @After
    public void cleanUp() {
        afterExecutedCount++;
    }

    @Test
    public void afterAnnotation_executedSeveralTimes_whenUsingRetryRule() {
        Assert.assertEquals(2, afterExecutedCount);
    }

}
