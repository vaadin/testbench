/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

public class TestBenchElementTest {

    @Test
    public void testIsEnabled_VaadinComponentDisabled_returnsFalse()
            throws Exception {
        WebElement webElement = createMock(WebElement.class);
        expect(webElement.getAttribute("class"))
                .andStubReturn("v-button v-disabled");
        replay(webElement);

        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);
        assertFalse(element.isEnabled());

        verify(webElement);
    }

    @Test
    public void testIsReadOnly_VaadinComponentNotReadOnly_returnsFalse()
            throws Exception {
        WebElement webElement = createMock(WebElement.class);
        expect(webElement.getAttribute("readonly")).andStubReturn(null);
        replay(webElement);

        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);
        assertFalse(element.isReadOnly());

        verify(webElement);
    }

    @Test
    public void testIsReadOnly_VaadinComponentReadOnly_returnsTrue()
            throws Exception {
        WebElement webElement = createMock(WebElement.class);
        expect(webElement.getAttribute("readonly")).andStubReturn("true");
        replay(webElement);

        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);
        assertTrue(element.isReadOnly());

        verify(webElement);
    }

    @Test
    public void test_getId_VaadinComponentWithId_returnsId() throws Exception {
        WebElement webElement = createMock(WebElement.class);
        expect(webElement.getAttribute("id")).andStubReturn("identification");
        replay(webElement);

        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);
        assertEquals("identification", element.getId());
    }

    @Test
    public void testIsEnabled_VaadinComponentEnabled_inputDisabled_returnsFalse()
            throws Exception {
        // This would probably never happen, but just make sure it works
        WebElement webElement = createMock(WebElement.class);
        expect(webElement.getAttribute("class")).andStubReturn("v-textfield");
        expect(webElement.isEnabled()).andReturn(false);
        replay(webElement);

        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);
        assertFalse(element.isEnabled());

        verify(webElement);
    }

    @Test
    public void testIsEnabled_VaadinComponentEnabled_inputEnabled_returnsTrue()
            throws Exception {
        WebElement webElement = createMock(WebElement.class);
        expect(webElement.getAttribute("class")).andStubReturn("v-textfield");
        expect(webElement.isEnabled()).andReturn(true);
        replay(webElement);

        TestBenchElement element = TestBenchElement.wrapElement(webElement,
                null);
        assertTrue(element.isEnabled());

        verify(webElement);
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

        HashSet<WebElement> elements = new HashSet<WebElement>();
        elements.add(webElement);
        elements.add(element);
        assertEquals(1, elements.size());

    }

    @Test
    public void hasClassName() throws Exception {
        assertFalse(createElementWithClass("foo").hasClassName(""));
        assertTrue(createElementWithClass("foo").hasClassName("foo"));
        assertFalse(createElementWithClass("foo").hasClassName("fo"));
        assertFalse(createElementWithClass("v-foo").hasClassName("foo"));

        assertTrue(createElementWithClass("foo bar baz").hasClassName("foo"));
        assertTrue(createElementWithClass("foo bar baz").hasClassName("bar"));
        assertTrue(createElementWithClass("foo bar baz").hasClassName("baz"));
        assertFalse(createElementWithClass("foo bar baz").hasClassName("ba"));
    }

    @Test
    public void getClassName() throws Exception {
        assertEquals(set("foo"), createElementWithClass("foo").getClassNames());
        assertEquals(set("foo", "bar"),
                createElementWithClass("foo bar").getClassNames());
        assertEquals(set("foo", "bar"),
                createElementWithClass("foo bar").getClassNames());
        assertEquals(set("foo", "bar"),
                createElementWithClass("foo bar foo").getClassNames());
        assertEquals(set("foo"),
                createElementWithClass("foo ").getClassNames());
        assertEquals(set("foo"),
                createElementWithClass(" foo").getClassNames());
        assertEquals(set(), createElementWithClass("").getClassNames());

    }

    private <T> Set<T> set(T... items) {
        return new HashSet<T>(Arrays.asList(items));
    }

    private TestBenchElement createElementWithClass(String className) {
        WebElement webElement = createMock(WebElement.class);
        expect(webElement.getAttribute("class")).andStubReturn(className);
        replay(webElement);

        return TestBenchElement.wrapElement(webElement, null);
    }

    @Test
    public void wrapNullElement() {
        assertNull(TestBenchElement.wrapElement(null, null));
    }
}
