package com.vaadin.testbench.annotations;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.TestBenchTestCase;


public class RetryRuleTestbenchTest extends TestBenchTestCase {

    private static boolean passes = true;

    @Test(expected = AssertionError.class)
    public void defaultExecution_noRetryRule_testcaseRunOnce() {
        passes = !passes;
        Assert.assertTrue(passes);
    }



}
