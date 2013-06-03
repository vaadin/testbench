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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;

/**
 * @author Jonatan Kronqvist / Vaadin Ltd
 */
public class TestBenchTestCaseTest extends TestBenchTestCase {

    private WebDriver mockWebDriverFindElements(boolean empty) {
        WebDriver driver = EasyMock.createNiceMock(WebDriver.class);

        ArrayList<WebElement> result = new ArrayList<WebElement>();
        if (!empty) {
            result.add(EasyMock.createNiceMock(WebElement.class));
        }
        EasyMock.expect(
                driver.findElements(EasyMock
                        .isA(org.openqa.selenium.By.class))).andReturn(result);
        EasyMock.replay(driver);
        return driver;
    }

    @Test
    public void testIsElementPresent_elementFound() {
        setDriver(mockWebDriverFindElements(false));
        assertTrue(isElementPresent(By.id("thisIDExists")));
    }

    @Test
    public void testIsElementPresent_elementNotFound() throws Exception {
        setDriver(mockWebDriverFindElements(true));
        assertFalse(isElementPresent(By.id("thisIDDoesNotExist")));
    }
}
