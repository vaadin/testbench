/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
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
