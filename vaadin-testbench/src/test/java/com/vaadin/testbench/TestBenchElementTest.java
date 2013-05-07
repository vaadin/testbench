package com.vaadin.testbench;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

public class TestBenchElementTest {

    @Test
    public void testIsEnabled_VaadinComponentDisabled_returnsFalse()
            throws Exception {
        WebElement webElement = createMock(WebElement.class);
        expect(webElement.getAttribute("class")).andStubReturn(
                "v-button v-disabled");
        replay(webElement);

        TestBenchElement element = new TestBenchElement(webElement, null);
        assertFalse(element.isEnabled());

        verify(webElement);
    }

    @Test
    public void testIsEnabled_VaadinComponentEnabled_inputDisabled_returnsFalse()
            throws Exception {
        // This would probably never happen, but just make sure it works
        WebElement webElement = createMock(WebElement.class);
        expect(webElement.getAttribute("class")).andStubReturn("v-textfield");
        expect(webElement.isEnabled()).andReturn(false);
        replay(webElement);

        TestBenchElement element = new TestBenchElement(webElement, null);
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

        TestBenchElement element = new TestBenchElement(webElement, null);
        assertTrue(element.isEnabled());

        verify(webElement);
    }
}
