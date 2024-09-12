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

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

import com.vaadin.testbench.testutils.ParentRetryRule;

public class ParentRetryRuleTest extends ParentRetryRule {
    private int count = 0;
    // Override parent RetryRule that uses RetryRule(1)
    @Rule
    public RetryRule retry = new RetryRule(2);

    @Test
    public void parentRule_settingRule_overridesParentRule() {
        count++;
        assertEquals(2, count);
    }
}
