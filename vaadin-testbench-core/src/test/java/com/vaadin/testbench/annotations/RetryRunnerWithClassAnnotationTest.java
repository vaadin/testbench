package com.vaadin.testbench.annotations;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(RetryTestRunner.class)
@Retry(count = 300)
public class RetryRunnerWithClassAnnotationTest {

    @Test
    public void randomFailureWithRetry_passesOnce_testPasses() {
        int rand = new Random().nextInt(9);
        Assert.assertTrue(rand % 7 == 1);
    }

    @Test(expected = AssertionError.class)
    public void randomFailureWithRetry_notPassesOnce_testFails() {
        Assert.assertTrue(false);
    }


}
