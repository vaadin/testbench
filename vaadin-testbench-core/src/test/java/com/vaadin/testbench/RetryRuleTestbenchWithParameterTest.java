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
