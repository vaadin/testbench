/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;

public class TestBenchElementTest {

    @Test
    public void testIsEnabled_VaadinComponentDisabled_returnsFalse()
            throws Exception {
        WebElement webElement = Mockito.mock(WebElement.class);
        Mockito.when(webElement.getDomAttribute("class"))
                .thenReturn("v-button v-disabled");

        TestBenchElement element = TestBench.createElement(webElement, null);
        Assertions.assertFalse(element.isEnabled());
    }

    @Test
    public void elementsEquals() {
        RemoteWebElement webElement = new RemoteWebElement();
        webElement.setId("remote1");
        TestBenchElement element = TestBench.createElement(webElement, null);
        TestBenchElement element2 = TestBench.createElement(webElement, null);

        Assertions.assertTrue(webElement.equals(webElement));
        Assertions.assertTrue(element.equals(element));
        Assertions.assertTrue(element.equals(webElement));

        Assertions.assertTrue(element.equals(element2));
        Assertions.assertTrue(element2.equals(element));

    }

    @Test
    public void elementsHashCode() {
        RemoteWebElement webElement = new RemoteWebElement();
        webElement.setId("remote1");
        TestBenchElement element = TestBench.createElement(webElement, null);

        HashSet<Object> elements = new HashSet<>();
        elements.add(webElement);
        elements.add(element);
        Assertions.assertEquals(1, elements.size());

    }

    @Test
    public void hasClassName() throws Exception {
        Assertions.assertFalse(createElementWithClass("foo").hasClassName(""));
        Assertions
                .assertTrue(createElementWithClass("foo").hasClassName("foo"));
        Assertions
                .assertFalse(createElementWithClass("foo").hasClassName("fo"));
        Assertions.assertFalse(
                createElementWithClass("v-foo").hasClassName("foo"));

        Assertions.assertTrue(
                createElementWithClass("foo bar baz").hasClassName("foo"));
        Assertions.assertTrue(
                createElementWithClass("foo bar baz").hasClassName("bar"));
        Assertions.assertTrue(
                createElementWithClass("foo bar baz").hasClassName("baz"));
        Assertions.assertFalse(
                createElementWithClass("foo bar baz").hasClassName("ba"));
    }

    @Test
    public void getClassName() throws Exception {
        Assertions.assertEquals(set("foo"),
                createElementWithClass("foo").getClassNames());
        Assertions.assertEquals(set("foo", "bar"),
                createElementWithClass("foo bar").getClassNames());
        Assertions.assertEquals(set("foo", "bar"),
                createElementWithClass("foo bar").getClassNames());
        Assertions.assertEquals(set("foo", "bar"),
                createElementWithClass("foo bar foo").getClassNames());
        Assertions.assertEquals(set("foo"),
                createElementWithClass("foo ").getClassNames());
        Assertions.assertEquals(set("foo"),
                createElementWithClass(" foo").getClassNames());
        Assertions.assertEquals(set(),
                createElementWithClass("").getClassNames());

    }

    private <T> Set<T> set(T... items) {
        return new HashSet<>(Arrays.asList(items));
    }

    private TestBenchElement createElementWithClass(String className) {
        WebElement webElement = Mockito.mock(WebElement.class);
        Mockito.when(webElement.getDomAttribute("class")).thenReturn(className);

        return TestBench.createElement(webElement, null);
    }

    @Test
    public void wrapNullElement() {
        Assertions.assertNull(TestBench.createElement(null, null));
    }

    public class DebugTestBenchElement extends TestBenchElement {
        private String lastScript;
        private Object[] lastArgs;

        @Override
        protected Object executeScript(String script, Object[] args) {
            lastScript = script;
            lastArgs = args;
            return "Logged";
        };

    }

    @Test
    public void callFunctionNoArgs() {
        DebugTestBenchElement e = new DebugTestBenchElement();
        e.callFunction("foo");
        Assertions.assertEquals(e.lastScript, "return arguments[0].foo()");
        Assertions.assertArrayEquals(new Object[] { e }, e.lastArgs);
    }

    @Test
    public void callFunctionOneArg() {
        DebugTestBenchElement e = new DebugTestBenchElement();
        e.callFunction("foo", 12.5);
        Assertions.assertEquals(e.lastScript,
                "return arguments[0].foo(arguments[1])");
        Assertions.assertArrayEquals(new Object[] { e, 12.5 }, e.lastArgs);
    }

    @Test
    public void callFunctionMultipleArgs() {
        DebugTestBenchElement e = new DebugTestBenchElement();
        e.callFunction("foo", 12.5, "foo", false);
        Assertions.assertEquals(e.lastScript,
                "return arguments[0].foo(arguments[1],arguments[2],arguments[3])");
        Assertions.assertArrayEquals(new Object[] { e, 12.5, "foo", false },
                e.lastArgs);
    }

    @Test
    public void doesNotWrapExceptions() {
        WebElement webElement = Mockito.mock(WebElement.class);

        TestBenchCommandExecutor executor = Mockito
                .mock(TestBenchCommandExecutor.class);
        Mockito.when(executor.executeScript(Mockito.any()))
                .thenThrow(new RuntimeException("foobar"));
        TestBenchElement element = TestBench.createElement(webElement,
                executor);
        RuntimeException exceptionReceived = null;
        try {
            element.executeScript("foo");
        } catch (RuntimeException e) {
            exceptionReceived = e;
        }
        Assertions.assertNotNull(exceptionReceived);
        Assertions.assertEquals("foobar", exceptionReceived.getMessage());
    }
}
