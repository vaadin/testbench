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
import org.junit.Test;

public class RetryRuleTestbenchTest extends TestBenchTestCase {

    private int count = 0;

    @Test(expected = AssertionError.class)
    public void defaultExecution_noRetryRule_testcaseRunOnce() {
        count++;
        Assert.assertEquals(2, count);
    }

}
