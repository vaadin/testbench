/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

public class TestBenchElementTest {

    @Test
    public void testIsEnabled_VaadinComponentDisabled_returnsFalse()
            throws Exception {
        WebElement webElement = Mockito.mock(WebElement.class);
        Mockito.when(webElement.getAttribute("class"))
                .thenReturn("v-button v-disabled");

        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);
        assertFalse(element.isEnabled());
    }

    @Test
    public void elementsEquals() {
        RemoteWebElement webElement = new RemoteWebElement();
        webElement.setId("remote1");
        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);
        TestBenchElement element2 = TestBenchElement.wrapElement(webElement,
                null);

        assertTrue(webElement.equals(webElement));
        assertTrue(element.equals(element));
        assertTrue(webElement.equals(element));
        assertTrue(element.equals(webElement));

        assertTrue(element.equals(element2));
        assertTrue(element2.equals(element));

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
        Assert.assertEquals(1, elements.size());

    }

    @Test
    public void hasClassName() throws Exception {
        Assert.assertFalse(createElementWithClass("foo").hasClassName(""));
        Assert.assertTrue(createElementWithClass("foo").hasClassName("foo"));
        Assert.assertFalse(createElementWithClass("foo").hasClassName("fo"));
        Assert.assertFalse(createElementWithClass("v-foo").hasClassName("foo"));

        Assert.assertTrue(
                createElementWithClass("foo bar baz").hasClassName("foo"));
        Assert.assertTrue(
                createElementWithClass("foo bar baz").hasClassName("bar"));
        Assert.assertTrue(
                createElementWithClass("foo bar baz").hasClassName("baz"));
        Assert.assertFalse(
                createElementWithClass("foo bar baz").hasClassName("ba"));
    }

    @Test
    public void getClassName() throws Exception {
        Assert.assertEquals(set("foo"),
                createElementWithClass("foo").getClassNames());
        Assert.assertEquals(set("foo", "bar"),
                createElementWithClass("foo bar").getClassNames());
        Assert.assertEquals(set("foo", "bar"),
                createElementWithClass("foo bar").getClassNames());
        Assert.assertEquals(set("foo", "bar"),
                createElementWithClass("foo bar foo").getClassNames());
        Assert.assertEquals(set("foo"),
                createElementWithClass("foo ").getClassNames());
        Assert.assertEquals(set("foo"),
                createElementWithClass(" foo").getClassNames());
        Assert.assertEquals(set(), createElementWithClass("").getClassNames());

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
        Assert.assertNull(TestBenchElement.wrapElement(null, null));
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
        Assert.assertNull(PublicTestBenchCommandExecutor
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
        Assert.assertEquals(unwrapped.size(), wrapped.size());

        for (int i = 0; i < unwrapped.size(); i++) {
            Object unwrappedObject = unwrapped.get(i);
            Object wrappedObject = wrapped.get(i);

            if (unwrappedObject instanceof WebElement) {
                WebElement webelement = (WebElement) unwrappedObject;
                TestBenchElement tbelement = (TestBenchElement) wrappedObject;
                Assert.assertSame(webelement, tbelement.getWrappedElement());
            } else if (unwrappedObject instanceof List) {
                assertWrappedList((List<Object>) unwrappedObject,
                        (List<Object>) wrappedObject);
            } else {
                Assert.assertSame(unwrappedObject, wrappedObject);
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
        Assert.assertEquals(e.lastScript, "return arguments[0].foo()");
        Assert.assertArrayEquals(new Object[] { e }, e.lastArgs);
    }

    @Test
    public void callFunctionOneArg() {
        DebugTestBenchElement e = new DebugTestBenchElement();
        e.callFunction("foo", 12.5);
        Assert.assertEquals(e.lastScript,
                "return arguments[0].foo(arguments[1])");
        Assert.assertArrayEquals(new Object[] { e, 12.5 }, e.lastArgs);
    }

    @Test
    public void callFunctionMultipleArgs() {
        DebugTestBenchElement e = new DebugTestBenchElement();
        e.callFunction("foo", 12.5, "foo", false);
        Assert.assertEquals(e.lastScript,
                "return arguments[0].foo(arguments[1],arguments[2],arguments[3])");
        Assert.assertArrayEquals(new Object[] { e, 12.5, "foo", false },
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
        Assert.assertNotNull(exceptionReceived);
        Assert.assertEquals("foobar", exceptionReceived.getMessage());
    }
}
