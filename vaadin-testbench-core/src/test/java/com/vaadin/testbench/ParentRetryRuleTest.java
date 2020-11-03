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
