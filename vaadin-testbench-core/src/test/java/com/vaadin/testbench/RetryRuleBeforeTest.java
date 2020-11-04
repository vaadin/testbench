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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RetryRuleBeforeTest {
    private int beforeExecutedCount = 0;

    @Rule
    public RetryRule retry = new RetryRule(3);

    @Before
    public void setUp() {
        beforeExecutedCount++;
    }

    @Test
    public void beforeAnnotation_executedSeveralTimes_whenUsingRetryRule() {
        Assert.assertEquals(3, beforeExecutedCount);
    }
}
