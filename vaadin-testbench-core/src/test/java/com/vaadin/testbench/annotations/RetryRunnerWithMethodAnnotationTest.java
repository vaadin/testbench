package com.vaadin.testbench.annotations;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(RetryTestRunner.class)
public class RetryRunnerWithMethodAnnotationTest {

    @Retry(count = 100)
    @Test
    public void randomFailureWithRetry_passesOnce_testPasses() {
        int rand = new Random().nextInt(9);
        Assert.assertTrue(rand % 7 == 1);
    }

    @Retry(count = 100)
    @Test(expected = AssertionError.class)
    public void randomFailureWithRetry_notPassesOnce_testFails() {
        Assert.assertTrue(false);
    }

    @Test(expected = AssertionError.class)
    public void randomFailureWithOutRetry_passesOnce_testFails() {
        int rand = new Random().nextInt(9);
        Assert.assertTrue(rand % 7 == 1);
    }
}
