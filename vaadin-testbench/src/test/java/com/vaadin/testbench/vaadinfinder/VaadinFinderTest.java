package com.vaadin.testbench.vaadinfinder;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.finder.Vaadin;

public class VaadinFinderTest {

    private WebDriver webDriverMock;

    @Before
    public void setUp() throws Exception {
        webDriverMock = EasyMock.createMock(WebDriver.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindWithNullParameter_throwsIllegalArgument()
            throws Exception {
        Vaadin.find(null, null);
        Vaadin.find(null, webDriverMock);
    }

    /**
     * Very basic implementation sanity check. Captures implementation errors in
     * VaadinComponentFinder early.
     * 
     * The multiple return values returned from this query help verify that only
     * one value is returned, and that lists are correctly handled.
     * 
     * @throws Exception
     */
    @Test
    public void testFindButton() throws Exception {
        expect(webDriverMock.findElements(isA(By.ByVaadin.class))).andReturn(
                Arrays.<WebElement> asList(
                        createNiceMock(TestBenchElement.class),
                        createNiceMock(TestBenchElement.class),
                        createNiceMock(TestBenchElement.class),
                        createNiceMock(TestBenchElement.class)));
        replay(webDriverMock);
        assertNotNull(Vaadin.find(Vaadin.button(), webDriverMock).done());
        verify(webDriverMock);
    }

    /**
     * Sanity check. Captures implementation errors in VaadinComponentFinder
     * early.
     * 
     * @throws Exception
     */
    @Test
    public void testFindButtonWithCaption() throws Exception {
        expect(webDriverMock.findElements(isA(By.ByVaadin.class))).andReturn(
                asList((WebElement) createNiceMock(TestBenchElement.class)));
        replay(webDriverMock);
        assertNotNull(Vaadin.find(Vaadin.button(), webDriverMock)
                .withCaption("Foo").done());
        verify(webDriverMock);
    }

    @Test
    public void testFindTextField_usesPathLocator() throws Exception {
        expect(webDriverMock.findElements(isA(By.ByVaadin.class))).andReturn(
                new ArrayList<WebElement>());
        replay(webDriverMock);

        Vaadin.find(Vaadin.textField(), webDriverMock).done();

        verify(webDriverMock);
    }

    @Test
    public void testFindTextField_noResults_returnsNull() throws Exception {
        expect(webDriverMock.findElements(isA(By.ByVaadin.class))).andReturn(
                new ArrayList<WebElement>());
        replay(webDriverMock);

        TestBenchElement result = Vaadin
                .find(Vaadin.textField(), webDriverMock).done();

        assertNull(result);
        verify(webDriverMock);
    }

    @Test
    public void testFindTextField_returnValueIncludesAllResults()
            throws Exception {
        int expectedSize = 5;
        List<WebElement> elements = new ArrayList<WebElement>();
        for (int i = 0; i < expectedSize; i++) {
            elements.add(createNiceMock(TestBenchElement.class));
        }
        expect(webDriverMock.findElements(isA(By.ByVaadin.class)))
                .andStubReturn(elements);
        replay(webDriverMock);

        assertEquals(elements.get(1),
                Vaadin.find(Vaadin.textField(), webDriverMock).atIndex(1)
                        .done());
        assertEquals(elements.get(3),
                Vaadin.find(Vaadin.textField(), webDriverMock).atIndex(3)
                        .done());
        assertEquals(elements.get(4),
                Vaadin.find(Vaadin.textField(), webDriverMock).atIndex(4)
                        .done());
        verify(webDriverMock);
    }

    @Test
    public void testFindTextField_usesValidPath() throws Exception {
        Capture<By.ByVaadin> by = new Capture<By.ByVaadin>();
        expect(
                webDriverMock.findElements(and(isA(By.ByVaadin.class),
                        capture(by))))
                .andReturn(
                        Arrays.<WebElement> asList(createNiceMock(TestBenchElement.class)));
        replay(webDriverMock);

        Vaadin.find(Vaadin.textField(), webDriverMock).done();

        assertEquals("By.vaadin: //VTextField", by.getValue().toString());
        verify(webDriverMock);
    }

    @Test
    public void testFindTextFieldAtIndex_usesValidPath() throws Exception {
        Capture<By.ByVaadin> by = new Capture<By.ByVaadin>();
        expect(
                webDriverMock.findElements(and(isA(By.ByVaadin.class),
                        capture(by)))).andReturn(
                Arrays.<WebElement> asList(
                        createNiceMock(TestBenchElement.class),
                        createNiceMock(TestBenchElement.class),
                        createNiceMock(TestBenchElement.class),
                        createNiceMock(TestBenchElement.class),
                        createNiceMock(TestBenchElement.class),
                        createNiceMock(TestBenchElement.class)));
        replay(webDriverMock);

        assertNotNull(Vaadin.find(Vaadin.textField(), webDriverMock).atIndex(4)
                .done());

        assertEquals("By.vaadin: //VTextField[4]", by.getValue().toString());
        verify(webDriverMock);
    }

    @Test
    public void testFindTextFieldByCaption_usesValidPath() throws Exception {
        Capture<By.ByVaadin> by = new Capture<By.ByVaadin>();
        expect(
                webDriverMock.findElements(and(isA(By.ByVaadin.class),
                        capture(by))))
                .andReturn(
                        Arrays.<WebElement> asList(createNiceMock(TestBenchElement.class)));
        replay(webDriverMock);

        assertNotNull(Vaadin.find(Vaadin.textField(), webDriverMock)
                .withCaption("One").done());

        assertEquals("By.vaadin: //VTextField[caption=\"One\"]", by.getValue()
                .toString());

        verify(webDriverMock);
    }

    @Test
    public void testFindTextFieldByCaption_notFound_returnsNull()
            throws Exception {
        expect(webDriverMock.findElements(isA(By.ByVaadin.class))).andReturn(
                new ArrayList<WebElement>());
        replay(webDriverMock);

        TestBenchElement result = Vaadin
                .find(Vaadin.textField(), webDriverMock).withCaption("One")
                .done();

        assertNull(result);
        verify(webDriverMock);
    }

    @Test
    public void testFindTextFieldByCaption_differentCaption() throws Exception {
        Capture<By.ByVaadin> by = new Capture<By.ByVaadin>();
        expect(
                webDriverMock.findElements(and(isA(By.ByVaadin.class),
                        capture(by))))
                .andReturn(
                        Arrays.<WebElement> asList(createNiceMock(TestBenchElement.class)));
        replay(webDriverMock);

        Vaadin.find(Vaadin.textField(), webDriverMock).withCaption("Two")
                .done();

        assertEquals("By.vaadin: //VTextField[caption=\"Two\"]", by.getValue()
                .toString());
        verify(webDriverMock);
    }

}
