package com.vaadin.testbench.annotations;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBenchTestCase;


public class RetryRuleTestbenchWithParameterTest extends TestBenchTestCase {

    static String oldSystemPropertyValue = System.getProperty(Parameters.class.getName() + "." + "max.attempts");

    static {
        System.setProperty(Parameters.class.getName() + "." + "max.attempts", "2");
    }

    @Test
    public void defaultExecution_ParameterSpecified_testPasses() {
        Assert.assertEquals(2, maxAttempts.getMaxAttempts());
    }

    @After
    public void cleanUp() {
        if (oldSystemPropertyValue != null) {
            System.setProperty(
                    Parameters.class.getName() + "." + "max.attempts", oldSystemPropertyValue);
        } else {
            System.clearProperty(Parameters.class.getName() + "." + "max.attempts");
        }
    }
}
