/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.spi.Backend;
import com.vaadin.testbench.spi.ElementBackend;
import com.vaadin.testbench.testutils.ScriptOnlyBackend;

/**
 * What {@link Mode#MODERN} changes, and that {@link Mode#COMPAT} keeps today's
 * behaviour.
 */
@SuppressWarnings("deprecation")
public class ModeTest {

    private static final String PROPERTY = "com.vaadin.testbench.Parameters.mode";

    private final List<String> scripts = new ArrayList<>();
    private Object innerText = "inner text";
    private final ElementBackend node = Mockito.mock(ElementBackend.class);

    /** Other tests read the mode too, which caches it. */
    @Before
    @After
    public void resetMode() {
        System.clearProperty(PROPERTY);
        Parameters.setMode(null);
    }

    /**
     * Vaadin is never busy; the innerText script answers {@link #innerText}.
     */
    private TestBenchElement element() {
        Backend backend = new ScriptOnlyBackend() {
            @Override
            public Object executeScript(String script, Object... args) {
                scripts.add(script);
                if (script.contains("isActive()")) {
                    return Boolean.TRUE;
                }
                return script.contains("innerText") ? innerText : null;
            }

            @Override
            public Object executeAsyncScript(String script, Object... args) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getBrowserName() {
                return "chromium";
            }
        };
        TestBenchCommandExecutor executor = new TestBenchCommandExecutor(null,
                null) {
            @Override
            public Backend getBackend() {
                return backend;
            }
        };
        return TestBench.createElement(TestBenchElement.class, node, executor);
    }

    @Test
    public void compatIsTheDefault() {
        Assert.assertEquals(Mode.COMPAT, Parameters.getMode());
    }

    @Test
    public void propertyChoosesTheMode() {
        System.setProperty(PROPERTY, " Modern ");
        Assert.assertEquals(Mode.MODERN, Parameters.getMode());
    }

    @Test
    public void misspelledModeThrows() {
        System.setProperty(PROPERTY, "moden");
        try {
            Parameters.getMode();
            Assert.fail("A misspelled mode must not run as compat");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage(),
                    e.getMessage().contains("'moden'"));
        }
    }

    @Test
    public void compatGetTextIsTheBackendsText() {
        Mockito.when(node.getText()).thenReturn("visible text");
        Assert.assertEquals("visible text", element().getText());
        Assert.assertTrue(
                scripts.stream().noneMatch(s -> s.contains("innerText")));
    }

    @Test
    public void modernGetTextIsInnerText() {
        Parameters.setMode(Mode.MODERN);
        Assert.assertEquals("inner text", element().getText());
        Mockito.verify(node, Mockito.never()).getText();
    }

    @Test
    public void modernGetTextThrowsWithoutInnerText() {
        Parameters.setMode(Mode.MODERN);
        innerText = null;
        Mockito.when(node.getTagName()).thenReturn("svg");
        try {
            element().getText();
            Assert.fail("An element without innerText has no modern getText()");
        } catch (UnsupportedOperationException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("<svg>"));
        }
    }

    @Test
    public void modernRejectsFirst() {
        Parameters.setMode(Mode.MODERN);
        TestBenchElement element = element();
        assertRejected(() -> element.$("div").first(), "first()");
        assertRejected(() -> element.$("div").waitForFirst(), "waitForFirst()");
        assertRejected(() -> element.$("div").waitForFirst(1),
                "waitForFirst()");
        Assert.assertTrue("Rejected before searching", scripts.stream()
                .noneMatch(s -> s.contains("querySelectorAll")));
    }

    @Test
    public void compatKeepsFirst() {
        TestBenchElement element = element();
        try {
            element.$("div").first();
            Assert.fail("The fake browser finds nothing");
        } catch (NoSuchElementException expected) {
            // searched, as first() always has
        }
    }

    private static void assertRejected(Runnable call, String method) {
        try {
            call.run();
            Assert.fail(method + " must throw in the modern mode");
        } catch (UnsupportedOperationException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().startsWith(method)
                    && e.getMessage().contains("get(0)"));
        }
    }
}
