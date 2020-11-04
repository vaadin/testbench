/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
