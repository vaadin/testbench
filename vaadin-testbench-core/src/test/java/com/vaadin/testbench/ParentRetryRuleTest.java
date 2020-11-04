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
        Assert.assertEquals(2, count);
    }
}
