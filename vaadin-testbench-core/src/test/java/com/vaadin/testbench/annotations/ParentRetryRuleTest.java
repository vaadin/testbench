package com.vaadin.testbench.annotations;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.vaadin.testbench.testutils.ParentRetryRule;

public class ParentRetryRuleTest extends ParentRetryRule {
    private int count = 0;
    //Override parent RetryRule that uses RetryRule(1)
    @Rule
    public RetryRule retry = new RetryRule(2);

    @Test
    public void parentRule_settingRule_overridesParentRule() {
        count++;
        Assert.assertEquals(2, count);
    }
}
