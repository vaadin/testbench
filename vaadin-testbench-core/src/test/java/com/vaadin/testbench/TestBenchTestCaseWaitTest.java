/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestBenchTestCaseWaitTest {

    @Test
    public void theTimeoutNamesTheCondition() {
        TestBenchTestCase test = new TestBenchTestCase() {
        };
        test.setDriver(Mockito.mock(BrowserDriver.class));
        Function<BrowserDriver, Boolean> condition = new Function<>() {
            @Override
            public Boolean apply(BrowserDriver driver) {
                return false;
            }

            @Override
            public String toString() {
                return "the cell to update";
            }
        };
        TimeoutException e = Assert.assertThrows(TimeoutException.class,
                () -> test.waitUntil(condition, 0));
        Assert.assertTrue(e.getMessage(),
                e.getMessage().contains("waiting for the cell to update"));
    }
}
