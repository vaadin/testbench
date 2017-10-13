package com.vaadin.testbench;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


public class RetryRuleTestbenchWithParameterTest extends TestBenchTestCase {

    static int oldMaxAttempts = Parameters.getMaxAttempts();

    static {
        Parameters.setMaxAttempts(2);
    }

    @Test
    public void defaultExecution_ParameterSpecified_testPasses() {
        Assert.assertEquals(2, maxAttempts.getMaxAttempts());
    }

    @After
    public void cleanUp() {
        Parameters.setMaxAttempts(oldMaxAttempts);
    }
}
