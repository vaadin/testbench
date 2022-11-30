/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

public class TestBenchElementTest {

    @Test
    public void testIsEnabled_VaadinComponentDisabled_returnsFalse()
            throws Exception {
        WebElement webElement = Mockito.mock(WebElement.class);
        Mockito.when(webElement.getAttribute("class"))
                .thenReturn("v-button v-disabled");

        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);
        Assertions.assertFalse(element.isEnabled());
    }

    @Test
    public void elementsEquals() {
        RemoteWebElement webElement = new RemoteWebElement();
        webElement.setId("remote1");
        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);
        TestBenchElement element2 = TestBenchElement.wrapElement(webElement,
                null);

        Assertions.assertTrue(webElement.equals(webElement));
        Assertions.assertTrue(element.equals(element));
        Assertions.assertTrue(webElement.equals(element));
        Assertions.assertTrue(element.equals(webElement));

        Assertions.assertTrue(element.equals(element2));
        Assertions.assertTrue(element2.equals(element));

    }

    @Test
    public void elementsHashCode() {
        RemoteWebElement webElement = new RemoteWebElement();
        webElement.setId("remote1");
        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);

        HashSet<WebElement> elements = new HashSet<>();
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
        Mockito.when(webElement.getAttribute("class")).thenReturn(className);

        return TestBenchElement.wrapElement(webElement, null);
    }

    @Test
    public void wrapNullElement() {
        Assertions.assertNull(TestBenchElement.wrapElement(null, null));
    }

    @Test
    public void wrapElementOrElementsEmptyList() {
        List<Object> execJsResult = new ArrayList<>();
        TestBenchCommandExecutor tbCommandExecutor = Mockito
                .mock(TestBenchCommandExecutor.class);
        PublicTestBenchCommandExecutor.wrapElementOrElements(execJsResult,
                tbCommandExecutor);
    }

    @Test
    public void wrapElementOrElementsNull() {
        Object execJsResult = null;

        TestBenchCommandExecutor tbCommandExecutor = Mockito
                .mock(TestBenchCommandExecutor.class);
        Assertions.assertNull(PublicTestBenchCommandExecutor
                .wrapElementOrElements(execJsResult, tbCommandExecutor));
    }

    public static class PublicTestBenchCommandExecutor
            extends TestBenchCommandExecutor {

        public PublicTestBenchCommandExecutor(TestBenchDriverProxy actualDriver,
                ImageComparison imageComparison,
                ReferenceNameGenerator referenceNameGenerator) {
            super(imageComparison, referenceNameGenerator);
            setDriver(actualDriver);
        }

        public static Object wrapElementOrElements(
                Object elementElementsOrValues,
                TestBenchCommandExecutor tbCommandExecutor) {
            return TestBenchDriverProxy.wrapElementOrElements(
                    elementElementsOrValues, tbCommandExecutor);
        }

    }

    @Test
    public void wrapElementOrElementsMixed() {
        WebElement element1 = Mockito.mock(WebElement.class);
        WebElement element2 = Mockito.mock(WebElement.class);
        WebElement element3 = Mockito.mock(WebElement.class);
        WebElement element4 = Mockito.mock(WebElement.class);
        WebElement element5 = Mockito.mock(WebElement.class);

        List<Object> execJsResult = new ArrayList<>();
        execJsResult.add(element1);
        execJsResult.add(element2);
        execJsResult.add(12L);
        execJsResult.add("asdjkfasdfjko");
        ArrayList<Object> sublist = new ArrayList<>();
        sublist.add(element4);
        sublist.add(15L);
        sublist.add(element5);
        execJsResult.add(sublist);
        execJsResult.add(element3);

        TestBenchCommandExecutor tbCommandExecutor = Mockito
                .mock(TestBenchCommandExecutor.class);
        List<Object> wrappedResult = (List<Object>) PublicTestBenchCommandExecutor
                .wrapElementOrElements(execJsResult, tbCommandExecutor);

        assertWrappedList(execJsResult, wrappedResult);
    }

    private void assertWrappedList(List<Object> unwrapped,
            List<Object> wrapped) {
        Assertions.assertEquals(unwrapped.size(), wrapped.size());

        for (int i = 0; i < unwrapped.size(); i++) {
            Object unwrappedObject = unwrapped.get(i);
            Object wrappedObject = wrapped.get(i);

            if (unwrappedObject instanceof WebElement) {
                WebElement webelement = (WebElement) unwrappedObject;
                TestBenchElement tbelement = (TestBenchElement) wrappedObject;
                Assertions.assertSame(webelement,
                        tbelement.getWrappedElement());
            } else if (unwrappedObject instanceof List) {
                assertWrappedList((List<Object>) unwrappedObject,
                        (List<Object>) wrappedObject);
            } else {
                Assertions.assertSame(unwrappedObject, wrappedObject);
            }
        }

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
        TestBenchElement element = TestBenchElement.wrapElement(webElement,
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
