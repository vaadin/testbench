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

import com.vaadin.testbench.ElementQuery;
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

    @Test
    public void testElementQueryGeneratesCorrectSelectors() {
        // First ComboBox
        expectSelector("(//com.vaadin.ui.ComboBox)[0]");
        // All ComboBoxes with caption "Country" somewhere inside a
        // VerticalLayout
        expectSelector("//com.vaadin.ui.VerticalLayout"
                + "//com.vaadin.ui.ComboBox[caption=\"Country\"]");
        // First ComboBox with caption in VerticalLayout with id "vl2" which is
        // a direct child of second Panel in application
        expectSelector("(" + "//com.vaadin.ui.Panel[1]"
                + "/com.vaadin.ui.VerticalLayout[id=\"vl2\"]"
                + "//com.vaadin.ui.ComboBox[caption=\"Country\"]" + ")[0]");
        replay(mockDriver);
        ElementQuery<ComboBoxElement> cbQuery = $(ComboBoxElement.class);
        cbQuery.first();
        cbQuery.caption("Country").in(VerticalLayoutElement.class).all();
        cbQuery.id("vl2").childOf(PanelElement.class).index(1).first();
    }

    @After
    public void verification() {
        verify(mockDriver);
    }
}
