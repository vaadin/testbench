/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.spi.ElementBackend;

public class QueryAssertTest {

    private static final String TIMEOUT = "com.vaadin.testbench.Parameters.actionTimeout";

    private final TestBenchCommandExecutor executor = new TestBenchCommandExecutor(
            null, null);

    /** Answers each search with the next list, then the last one again. */
    private class ScriptedQuery extends ElementQuery<TestBenchElement> {
        private final Deque<List<TestBenchElement>> answers;
        int searches;

        @SafeVarargs
        ScriptedQuery(List<TestBenchElement>... answers) {
            super(TestBenchElement.class, "div");
            this.answers = new ArrayDeque<>(List.of(answers));
            onPage(executor);
        }

        @Override
        List<TestBenchElement> executeSearchScript(String script,
                Object context, String tagName, String attributePairs,
                TestBenchCommandExecutor executor) {
            searches++;
            return answers.size() > 1 ? answers.poll() : answers.peek();
        }
    }

    private TestBenchElement element() {
        return TestBench.createElement(TestBenchElement.class,
                Mockito.mock(ElementBackend.class), executor);
    }

    @Before
    public void shortTimeout() {
        System.setProperty(TIMEOUT, "400");
    }

    @After
    public void clearTimeout() {
        System.clearProperty(TIMEOUT);
    }

    @Test
    public void assertNoneSearchesUntilNothingMatches() {
        ScriptedQuery query = new ScriptedQuery(List.of(element()),
                List.of(element()), Collections.emptyList());
        query.assertNone();
        Assert.assertEquals(3, query.searches);
    }

    @Test
    public void assertNoneFailsWhenSomethingStillMatches() {
        ScriptedQuery query = new ScriptedQuery(List.of(element()));
        AssertionError error = Assert.assertThrows(AssertionError.class,
                query::assertNone);
        Assert.assertEquals(
                "Expected no elements with tag <div>, found 1 after 400 ms",
                error.getMessage());
        Assert.assertTrue(query.searches > 1);
    }

    @Test
    public void assertCountReturnsWhatItCounted() {
        List<TestBenchElement> two = List.of(element(), element());
        ScriptedQuery query = new ScriptedQuery(List.of(element()), two);
        Assert.assertEquals(two, query.assertCount(2));
    }

    @Test
    public void assertCountNamesTheAttributes() {
        ScriptedQuery query = new ScriptedQuery(Collections.emptyList());
        query.withId("x");
        AssertionError error = Assert.assertThrows(AssertionError.class,
                () -> query.assertCount(1));
        Assert.assertEquals(
                "Expected 1 elements with tag <div> and the"
                        + " attributes [id='x'], found 0 after 400 ms",
                error.getMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void aNegativeCountIsRefused() {
        new ScriptedQuery(Collections.emptyList()).assertCount(-1);
    }
}
