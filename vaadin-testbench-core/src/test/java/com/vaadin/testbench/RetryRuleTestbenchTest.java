/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
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
