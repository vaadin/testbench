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
package org.testbench.integration.tests.vaadinfinder;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

public class ElementQueryTest extends TestBenchTestCase {

    @ServerClass("example.element")
    public static class ExampleElement extends AbstractElement {

    }

    @ServerClass("OtherElement")
    public static class OtherElement extends AbstractElement {

    }

    @ServerClass("third")
    public static class ThirdElement extends AbstractElement {

    }

    WebDriver mockDriver = createMock(FirefoxDriver.class);
    WebElement mockElement = createMock(WebElement.class);

    @Before
    public void setUp() {
        setDriver(mockDriver);
        // Waiting for vaadin is unnecessary.
        testBench().disableWaitForVaadin();
    }

    private void expectSelector(String vaadinSelector) {
        expect(((JavascriptExecutor) mockDriver).executeScript(
                anyObject(String.class), contains(vaadinSelector)))
                        .andReturn(Arrays.asList(mockElement)).once();
    }

    private void expectSelectorStartingAt(String vaadinSelector) {
        expect(((JavascriptExecutor) mockDriver).executeScript(
                anyObject(String.class), contains(vaadinSelector),
                anyObject(WebElement.class)))
                        .andReturn(Arrays.asList(mockElement)).once();

    }

    @Test
    public void testElementQueryGeneratesCorrectSelectors() {
        // First ComboBox
        expectSelector("//example.element");
        // All ComboBoxes with caption "Country" somewhere inside a
        // VerticalLayout
        expectSelector(
                "//OtherElement" + "//example.element[caption=\"Country\"]");
        // First ComboBox with caption in VerticalLayout with id "vl1" which is
        // a direct child of second Panel in application
        expectSelector("//third[1]" + "/OtherElement[id=\"vl2\"]"
                + "//example.element[caption=\"Country\"]");

        // Another way to split the search in two parts. First with the ID and
        // then just ComboBox StartingAt
        expectSelector("//third[1]" + "/OtherElement[id=\"vl1\"]");
        expectSelectorStartingAt("//example.element[caption=\"Country\"]");
        replay(mockDriver);

        $(ExampleElement.class).first();
        $(OtherElement.class).$(ExampleElement.class).caption("Country")
                .first();

        $(ExampleElement.class).caption("Country")
                .in($(OtherElement.class).state("id", "vl2"))
                .child($(ThirdElement.class).index(1)).first();

        $(ThirdElement.class).index(1).$$(OtherElement.class).id("vl1")
                .$(ExampleElement.class).caption("Country").first();
    }

    @After
    public void verification() {
        verify(mockDriver);
    }
}
