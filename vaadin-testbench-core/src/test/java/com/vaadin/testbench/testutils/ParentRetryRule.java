/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.testutils;

import org.junit.Rule;

import com.vaadin.testbench.RetryRule;

public class ParentRetryRule {
    @Rule
    public RetryRule retry = new RetryRule(1);
}
