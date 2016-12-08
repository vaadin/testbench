package com.vaadin.testbench.annotations;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.vaadin.testbench.testutils.ParentRetryRule;

public class ParentRetryRuleTest extends ParentRetryRule {
    private static boolean passes = true;

    //Override parent RetryRule that uses RetryRule(1)
    @Rule
    public RetryRule retry = new RetryRule(10);

    @Test
    public void parentRule_settingRule_overridesParentRule() {
        passes = !passes;
        Assert.assertTrue(passes);
    }
}
