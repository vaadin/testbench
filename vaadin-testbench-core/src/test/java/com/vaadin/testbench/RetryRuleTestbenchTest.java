package com.vaadin.testbench;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.TestBenchTestCase;


public class RetryRuleTestbenchTest extends TestBenchTestCase {

    private int count = 0;

    @Test(expected = AssertionError.class)
    public void defaultExecution_noRetryRule_testcaseRunOnce() {
        count++;
        Assert.assertEquals(2, count);
    }

}
