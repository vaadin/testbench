/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
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
