package com.vaadin.testbench.annotations;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBenchTestCase;


public class RetryRuleTestbenchWIthParameterTest extends TestBenchTestCase {

    static {
        System.setProperty(Parameters.class.getName() + "." + "retryCount", "2");
    }

    private static boolean passes = true;

    @Test
    public void defaultExecution_ParameterSpecified_testPasses() {

        passes = !passes;
        Assert.assertTrue(passes);
    }

    @After
    public void cleanUp() {
        System.getProperties().remove(Parameters.class.getName() + "." + "retryCount");
        System.getProperty(Parameters.class.getName() + "." + "retryCount");
    }
}
