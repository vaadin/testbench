package com.vaadin.testbench.annotations;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class RetryRuleBeforeTest {
    private static int beforeExecutedCount = 0;
    @Rule
    public RetryRule retry = new RetryRule(2);

    @Before
    public void setUp() {
        beforeExecutedCount++;
    }

    @Test
    public void beforeAnnotation_executedSeveralTimes_whenUsingRetryRule() {
        Assert.assertEquals(2, beforeExecutedCount);
    }
}
