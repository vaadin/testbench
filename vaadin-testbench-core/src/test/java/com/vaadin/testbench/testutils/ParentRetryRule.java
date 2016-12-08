package com.vaadin.testbench.testutils;

import org.junit.Rule;

import com.vaadin.testbench.annotations.RetryRule;

public class ParentRetryRule {
    @Rule
    public RetryRule retry = new RetryRule(1);
}
