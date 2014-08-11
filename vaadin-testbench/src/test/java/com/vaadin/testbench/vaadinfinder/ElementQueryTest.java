/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.vaadinfinder;

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
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;

public class ElementQueryTest extends TestBenchTestCase {

    WebDriver mockDriver = createMock(FirefoxDriver.class);
    WebElement mockElement = createMock(WebElement.class);

    @Before
    public void setUp() {
        setDriver(mockDriver);
        // Waiting for vaadin is unnecessary.
        testBench().disableWaitForVaadin();
    }

    private void expectSelector(String vaadinSelector) {
        expect(
                ((JavascriptExecutor) mockDriver).executeScript(
                        anyObject(String.class), contains(vaadinSelector)))
                .andReturn(Arrays.asList(mockElement)).once();
    }

    private void expectSelectorStartingAt(String vaadinSelector) {
        expect(
                ((JavascriptExecutor) mockDriver).executeScript(
                        anyObject(String.class), contains(vaadinSelector),
                        anyObject(WebElement.class))).andReturn(
                Arrays.asList(mockElement)).once();

    }

    @Test
    public void testElementQueryGeneratesCorrectSelectors() {
        // First ComboBox
        expectSelector("//com.vaadin.ui.ComboBox");
        // All ComboBoxes with caption "Country" somewhere inside a
        // VerticalLayout
        expectSelector("//com.vaadin.ui.VerticalLayout"
                + "//com.vaadin.ui.ComboBox[caption=\"Country\"]");
        // First ComboBox with caption in VerticalLayout with id "vl1" which is
        // a direct child of second Panel in application
        expectSelector("//com.vaadin.ui.Panel[1]"
                + "/com.vaadin.ui.VerticalLayout[id=\"vl2\"]"
                + "//com.vaadin.ui.ComboBox[caption=\"Country\"]");

        // Another way to split the search in two parts. First with the ID and
        // then just ComboBox StartingAt
        expectSelector("//com.vaadin.ui.Panel[1]"
                + "/com.vaadin.ui.VerticalLayout[id=\"vl1\"]");
        expectSelectorStartingAt("//com.vaadin.ui.ComboBox[caption=\"Country\"]");
        replay(mockDriver);

        $(ComboBoxElement.class).first();
        $(VerticalLayoutElement.class).$(ComboBoxElement.class)
                .caption("Country").first();

        $(ComboBoxElement.class).caption("Country")
                .in($(VerticalLayoutElement.class).state("id", "vl2"))
                .child($(PanelElement.class).index(1)).first();

        $(PanelElement.class).index(1).$$(VerticalLayoutElement.class)
                .id("vl1").$(ComboBoxElement.class).caption("Country").first();
    }

    @After
    public void verification() {
        verify(mockDriver);
    }
}
