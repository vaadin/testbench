package com.vaadin.testbench;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.artsok.RepeatedIfExceptionsTest;

public class RerunnerTest {

    private static AtomicInteger idx = new AtomicInteger(0);

    @RepeatedIfExceptionsTest(repeats = 10)
    public void reRunTest() throws IOException {
        if (idx.incrementAndGet() < 4) {
            throw new IOException("Error in Test");
        }
    }

}
