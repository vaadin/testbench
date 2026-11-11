/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

public class WaitTest {

    @Test
    public void returnsTheFirstValueNeitherNullNorFalse() {
        Iterator<Object> values = List.<Object> of(false, "found").iterator();
        Assert.assertEquals("found",
                Wait.until(values::next, Duration.ofSeconds(5)));
    }

    @Test
    public void keepsPollingThroughNoSuchElement() {
        Iterator<Boolean> found = List.of(false, true).iterator();
        Supplier<Boolean> condition = () -> {
            if (!found.next()) {
                throw new NoSuchElementException("not yet");
            }
            return true;
        };
        Assert.assertTrue(Wait.until(condition, Duration.ofSeconds(5)));
    }

    @Test
    public void timesOutWithTheLastNoSuchElementAsItsCause() {
        NoSuchElementException notFound = new NoSuchElementException("gone");
        Supplier<Object> condition = () -> {
            throw notFound;
        };
        try {
            Wait.until(condition, Duration.ZERO);
            Assert.fail("Expected a timeout");
        } catch (TimeoutException e) {
            Assert.assertSame(notFound, e.getCause());
            Assert.assertTrue(e.getMessage(), e.getMessage().startsWith(
                    "Expected condition failed: waiting for " + condition));
            Assert.assertTrue(e.getMessage(), e.getMessage().endsWith(
                    "(tried for 0 seconds with 500 milliseconds interval)"));
        }
    }

    @Test
    public void timesOutWithoutCauseWhenTheLastTryReturnedFalse() {
        try {
            Wait.until(() -> false, Duration.ZERO);
            Assert.fail("Expected a timeout");
        } catch (TimeoutException e) {
            Assert.assertNull(e.getCause());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void otherExceptionsEndTheWait() {
        Wait.until(() -> {
            throw new IllegalStateException();
        }, Duration.ofSeconds(5));
    }
}
