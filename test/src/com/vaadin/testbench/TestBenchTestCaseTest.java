package com.vaadin.testbench;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
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
public class TestBenchTestCaseTest extends TestBenchTestCase {

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
        setDriver(mockWebDriverFindElementToReturnOrThrow(EasyMock
                .createNiceMock(WebElement.class)));
        assertTrue(isElementPresent(By.id("thisIDExists")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundThrowsNoSuchElementException() {
        setDriver(mockWebDriverFindElementToReturnOrThrow(new NoSuchElementException(
                "")));
        assertFalse(isElementPresent(By.id("thisIDDoesNotExist")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundThrowsInvalidSelectorException() {
        setDriver(mockWebDriverFindElementToReturnOrThrow(new InvalidSelectorException(
                "")));
        assertFalse(isElementPresent(By.id("thisIDDoesNotExist")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundThrowsElementNotVisibleException() {
        setDriver(mockWebDriverFindElementToReturnOrThrow(new ElementNotVisibleException(
                "")));
        assertFalse(isElementPresent(By.id("thisElementIsNotVisible")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundThrowsElementNotFoundException() {
        setDriver(mockWebDriverFindElementToReturnOrThrow(new ElementNotFoundException(
                "", "", "")));
        assertFalse(isElementPresent(By.id("thisIDDoesNotExist")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundThrowsNPE() {
        setDriver(mockWebDriverFindElementToReturnOrThrow(new NullPointerException(
                "")));
        assertFalse(isElementPresent(By.id("thisIDDoesNotExist")));
    }

    @Test
    public void testIsElementPresent_elementNotFoundReturnsNull() {
        setDriver(mockWebDriverFindElementToReturnOrThrow(null));
        assertFalse(isElementPresent(By.id("thisIDDoesNotExist")));
    }
}
