package com.vaadin.testbench;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

/**
 * @author Jonatan Kronqvist / Vaadin Ltd
 */
public class TestBenchTestCaseTest {

    private TestBenchTestCase testBenchTestCase;

    @Before
    public void setUp() {
        testBenchTestCase = new TestBenchTestCase() {
        };
    }

    @After
    public void tareDown() {

    }

    private WebDriver mockWebDriverFindElementToReturnOrThrow(
            Object toReturnOrThrow) {
        WebDriver driver = EasyMock.createNiceMock(WebDriver.class);
        if (toReturnOrThrow instanceof Throwable) {
            EasyMock.expect(
                    driver.findElement(EasyMock
                            .isA(org.openqa.selenium.By.class))).andThrow(
                    (Throwable) toReturnOrThrow);
        } else {
            EasyMock.expect(
                    driver.findElement(EasyMock
                            .isA(org.openqa.selenium.By.class))).andReturn(
                    (WebElement) toReturnOrThrow);
        }
        EasyMock.replay(driver);
        return driver;
    }

    @Test
    public void testIsElementPresent_elementFound() {
        testBenchTestCase
                .setDriver(mockWebDriverFindElementToReturnOrThrow(EasyMock
                        .createNiceMock(WebElement.class)));
        assertTrue(testBenchTestCase.isElementPresent(By.id("thisIDExists")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundThrowsNoSuchElementException() {
        testBenchTestCase
                .setDriver(mockWebDriverFindElementToReturnOrThrow(new NoSuchElementException(
                        "")));
        assertFalse(testBenchTestCase.isElementPresent(By
                .id("thisIDDoesNotExist")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundThrowsInvalidSelectorException() {
        testBenchTestCase
                .setDriver(mockWebDriverFindElementToReturnOrThrow(new InvalidSelectorException(
                        "")));
        assertFalse(testBenchTestCase.isElementPresent(By
                .id("thisIDDoesNotExist")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundThrowsElementNotVisibleException() {
        testBenchTestCase
                .setDriver(mockWebDriverFindElementToReturnOrThrow(new ElementNotVisibleException(
                        "")));
        assertFalse(testBenchTestCase.isElementPresent(By
                .id("thisElementIsNotVisible")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundThrowsElementNotFoundException() {
        testBenchTestCase
                .setDriver(mockWebDriverFindElementToReturnOrThrow(new ElementNotFoundException(
                        "", "", "")));
        assertFalse(testBenchTestCase.isElementPresent(By
                .id("thisIDDoesNotExist")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundThrowsNPE() {
        testBenchTestCase
                .setDriver(mockWebDriverFindElementToReturnOrThrow(new NullPointerException(
                        "")));
        assertFalse(testBenchTestCase.isElementPresent(By
                .id("thisIDDoesNotExist")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundReturnsNull() {
        testBenchTestCase
                .setDriver(mockWebDriverFindElementToReturnOrThrow(null));
        assertFalse(testBenchTestCase.isElementPresent(By
                .id("thisIDDoesNotExist")));
    }
}
