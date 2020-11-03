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
package com.vaadin.testbench.testutils;

import org.junit.Rule;

import com.vaadin.testbench.RetryRule;

public class ParentRetryRule {
    @Rule
    public RetryRule retry = new RetryRule(1);
}
