/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.testutils;

import org.junit.Rule;

import com.vaadin.testbench.RetryRule;

public class ParentRetryRule {
    @Rule
    public RetryRule retry = new RetryRule(1);
}
