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

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.commands.CanWaitForVaadin;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchElementCommands;

public class TestBenchElement implements WrapsElement, WebElement,
        TestBenchElementCommands, CanWaitForVaadin {
    // private static final Logger LOGGER = Logger
    // .getLogger(TestBenchElement.class.getName());

    private WebElement actualElement;
    private final TestBenchCommandExecutor tbCommandExecutor;

    protected TestBenchElement(WebElement element,
            TestBenchCommandExecutor tbCommandExecutor) {
        actualElement = element;
        this.tbCommandExecutor = tbCommandExecutor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.internal.WrapsElement#getWrappedElement()
     */
    @Override
    public WebElement getWrappedElement() {
        return actualElement;
    }

    @Override
    public void waitForVaadin() {
        tbCommandExecutor.waitForVaadin();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchElementCommands#closeNotification
     * ()
     */
    @Override
    public boolean closeNotification() {
        click();
        try {
            // Wait for 5000 ms or until the element is no longer visible.
            int times = 0;
            while (isDisplayed() || times > 25) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
                times++;
            }
            return !isDisplayed();
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("no longer attached")) {
                // This was some other exception than a no longer attached
                // exception. Rethrow.
                throw e;
            }
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.testbench.commands.TestBenchElementCommands#showTooltip()
     */
    @Override
    public void showTooltip() {
        new Actions(tbCommandExecutor.getWrappedDriver()).moveToElement(
                actualElement).perform();
        // Wait for a small moment for the tooltip to appear
        try {
            Thread.sleep(1000); // VTooltip.OPEN_DELAY = 750;
        } catch (InterruptedException ignored) {
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.testbench.commands.TestBenchElementCommands#scroll(int)
     */
    @Override
    public void scroll(int scrollTop) {
        JavascriptExecutor js = tbCommandExecutor;
        js.executeScript("arguments[0].scrollTop = " + scrollTop, actualElement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchElementCommands#scrollLeft(int)
     */
    @Override
    public void scrollLeft(int scrollLeft) {
        JavascriptExecutor js = tbCommandExecutor;
        js.executeScript("arguments[0].scrollLeft = " + scrollLeft,
                actualElement);
    }

    @Override
    public void click() {
        String tagName = actualElement.getTagName();
        actualElement.click();
        // Hack to make ChromeDriver and PhantomJSDriver correctly trigger
        // onchange events in ListSelects. See #12507
        if (needsOnChangeHack() && "option".equalsIgnoreCase(tagName)) {
            triggerEvent("change");
        }
    }

    /**
     * @return true if running on ChromeDriver or PhantomJSDriver, which require
     *         the onchange hack.
     */
    private boolean needsOnChangeHack() {
        return (isChromeDriver() || isPhantomJSDriver());
    }

    /**
     * @return true if we're running on PhantomJS
     */
    private boolean isPhantomJSDriver() {
        WebDriver driver = tbCommandExecutor.getDriver();
        if (driver instanceof RemoteWebDriver) {
            return "phantomjs".equalsIgnoreCase(((RemoteWebDriver) driver)
                    .getCapabilities().getBrowserName());
        }
        return false;
    }

    /**
     * @return true if we're running on Chrome
     */
    private boolean isChromeDriver() {
        WebDriver driver = tbCommandExecutor.getDriver();
        if (driver instanceof RemoteWebDriver) {
            return "chrome".equalsIgnoreCase(((RemoteWebDriver) driver)
                    .getCapabilities().getBrowserName());
        }
        return false;
    }

    /**
     * Triggers an HTML event on the element using JavaScript. Used internally
     * for hacking around bugs in driver implementations.
     * 
     * @param eventType
     *            the type of event (e.g. "change")
     */
    private void triggerEvent(String eventType) {
        String js = "var event;" + "if (document.createEvent) {"
                + "    event = document.createEvent('HTMLEvents');"
                + "    event.initEvent(arguments[1], true, true);" + "} else {"
                + "    event = document.createEventObject();"
                + "    event.eventType = arguments[1];" + "}" +

                "event.eventName = arguments[1];" +

                "if (document.createEvent) {"
                + "    arguments[0].dispatchEvent(event);" + "} else {"
                + "    arguments[0].fireEvent('on' + event.eventType, event);"
                + "}";
        tbCommandExecutor.executeScript(js, actualElement, eventType);
    }

    @Override
    public void submit() {
        actualElement.click();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        actualElement.sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        actualElement.clear();
    }

    @Override
    public String getTagName() {
        return actualElement.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        return actualElement.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return actualElement.isSelected();
    }

    /**
     * Returns whether the Vaadin component, that this element represents, is
     * enabled or not.
     * 
     * @return true if the component is enabled.
     */
    @Override
    public boolean isEnabled() {
        return !actualElement.getAttribute("class").contains("v-disabled")
                && actualElement.isEnabled();
    }

    @Override
    public String getText() {
        return actualElement.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> elements = actualElement.findElements(by);
        List<WebElement> testBenchElements = new ArrayList<WebElement>(
                elements.size());
        for (WebElement e : elements) {
            testBenchElements
                    .add(TestBench.createElement(e, tbCommandExecutor));
        }
        return testBenchElements;
    }

    @Override
    public WebElement findElement(By by) {
        return TestBench.createElement(actualElement.findElement(by),
                tbCommandExecutor);
    }

    @Override
    public boolean isDisplayed() {
        return actualElement.isDisplayed();
    }

    @Override
    public Point getLocation() {
        return actualElement.getLocation();
    }

    @Override
    public Dimension getSize() {
        return actualElement.getSize();
    }

    @Override
    public String getCssValue(String propertyName) {
        return actualElement.getCssValue(propertyName);
    }

    @Override
    public void click(int x, int y, Keys... modifiers) {
        Actions actions = new Actions(tbCommandExecutor.getWrappedDriver());
        actions.moveToElement(actualElement, x, y);
        // Press any modifier keys
        for (Keys modifier : modifiers) {
            actions.keyDown(modifier);
        }
        actions.click();
        // Release any modifier keys
        for (Keys modifier : modifiers) {
            actions.keyUp(modifier);
        }
        actions.build().perform();
    }
}
