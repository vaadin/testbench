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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.spi.Backend;
import com.vaadin.testbench.spi.ElementBackend;

/**
 * {@code D_element_resolution}: a stale element is found again by its query.
 */
public class RefindTest {

    /** Nodes in this set are gone: every call with one throws. */
    private final Set<ElementBackend> gone = new HashSet<>();
    private final List<Object> scriptTargets = new ArrayList<>();

    private final Backend backend = new Backend() {
        @Override
        public Object executeScript(String script, Object... args) {
            if (script.contains("isActive()")) {
                return true;
            }
            for (Object arg : args) {
                if (gone.contains(arg)) {
                    throw new StaleElementReferenceException("gone");
                }
            }
            scriptTargets.add(args.length > 0 ? args[0] : null);
            return null;
        }

        @Override
        public Object executeAsyncScript(String script, Object... args) {
            return null;
        }

        @Override
        public String getBrowserName() {
            return "chrome";
        }
    };

    private final TestBenchCommandExecutor executor = new TestBenchCommandExecutor(
            null, null) {
        @Override
        public Backend getBackend() {
            return backend;
        }
    };

    {
        executor.setAutoScrollIntoView(false);
    }

    private ElementBackend node(String text) {
        ElementBackend node = Mockito.mock(ElementBackend.class, invocation -> {
            if (gone.contains(invocation.getMock())) {
                throw new StaleElementReferenceException("gone");
            }
            return invocation.getMethod().getName().equals("getText") ? text
                    : null;
        });
        return node;
    }

    /** Answers each search with the next list of nodes, then the last again. */
    private class ScriptedQuery extends ElementQuery<TestBenchElement> {
        private final Deque<List<ElementBackend>> answers;
        int searches;

        @SafeVarargs
        ScriptedQuery(List<ElementBackend>... answers) {
            super(TestBenchElement.class, "div");
            this.answers = new ArrayDeque<>(Arrays.asList(answers));
            onPage(executor);
        }

        @Override
        List<TestBenchElement> executeSearchScript(String script,
                Object context, String tagName, String attributePairs,
                TestBenchCommandExecutor executor) {
            searches++;
            List<ElementBackend> nodes = answers.size() > 1 ? answers.poll()
                    : answers.peek();
            // get(i) asks the script for result[i] alone
            java.util.regex.Matcher index = java.util.regex.Pattern
                    .compile("\\[(\\d+)\\]$").matcher(script);
            if (index.find()) {
                int i = Integer.parseInt(index.group(1));
                nodes = i < nodes.size() ? List.of(nodes.get(i)) : List.of();
            }
            return nodes.stream().map(node -> TestBench
                    .createElement(TestBenchElement.class, node, executor))
                    .toList();
        }
    }

    @Test
    public void aStaleElementIsFoundAgainByItsQuery() {
        ElementBackend old = node("old");
        ElementBackend fresh = node("fresh");
        ScriptedQuery query = new ScriptedQuery(List.of(old), List.of(fresh));
        TestBenchElement element = query.single();

        gone.add(old);

        Assert.assertEquals("fresh", element.getText());
        Assert.assertEquals(2, query.searches);
        Assert.assertSame(fresh, element.getElementBackend());
    }

    @Test
    public void theSamePositionIsFoundAgain() {
        ElementBackend first = node("first");
        ElementBackend second = node("second");
        ElementBackend newSecond = node("new second");
        ScriptedQuery query = new ScriptedQuery(List.of(first, second),
                List.of(first, newSecond));
        TestBenchElement element = query.get(1);

        gone.add(second);

        Assert.assertEquals("new second", element.getText());
    }

    @Test
    public void aScriptWithAStaleElementRunsAgainOnTheFreshOne() {
        ElementBackend old = node("old");
        ElementBackend fresh = node("fresh");
        TestBenchElement element = new ScriptedQuery(List.of(old),
                List.of(fresh)).single();

        gone.add(old);
        element.callFunction("click");

        Assert.assertEquals(List.of(fresh), scriptTargets);
    }

    @Test
    public void anElementTheQueryNoLongerFindsStaysStale() {
        ElementBackend old = node("old");
        TestBenchElement element = new ScriptedQuery(List.of(old),
                Collections.emptyList()).single();

        gone.add(old);

        Assert.assertThrows(StaleElementReferenceException.class,
                element::getText);
    }

    @Test
    public void anElementFromNoQueryStaysStale() {
        ElementBackend old = node("old");
        TestBenchElement element = TestBench
                .createElement(TestBenchElement.class, old, executor);

        gone.add(old);

        Assert.assertThrows(StaleElementReferenceException.class,
                element::getText);
        Assert.assertThrows(StaleElementReferenceException.class,
                () -> element.callFunction("click"));
    }

    @Test
    public void aWrappedElementKeepsItsQuery() {
        ElementBackend old = node("old");
        ElementBackend fresh = node("fresh");
        TestBenchElement found = new ScriptedQuery(List.of(old), List.of(fresh))
                .single();
        TestBenchElement wrapped = found.wrap(TestBenchElement.class);

        gone.add(old);

        Assert.assertEquals("fresh", wrapped.getText());
    }
}
