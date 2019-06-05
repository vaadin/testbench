package com.vaadin.testbench.addons.junit5.pageobject;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.testbench.HasElementQuery;
import com.vaadin.testbench.HasTestBenchCommandExecutor;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.proxy.TestBenchDriverProxy;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static com.vaadin.testbench.addons.testbench.TestbenchFunctions.unproxy;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;

public class VaadinPageObject extends AbstractPageObject implements HasElementQuery, HasTestBenchCommandExecutor {

    private static final String NO_DRIVER = "NoDriver";

    @Override
    public TestBenchCommandExecutor getCommandExecutor() {
        return ((HasTestBenchCommandExecutor) getDriver()).getCommandExecutor();
    }

    @Override
    public SearchContext getContext() {
        return getDriver();
    }

    @Override
    public void setDriver(WebDriver driver) {
        // Testbench-specific init.
        if (driver != null && !(driver instanceof TestBenchDriverProxy)) {
            driver = TestBench.createDriver(driver);
        }
        super.setDriver(driver);
    }

    public String driverName() {
        final WebDriver driver = getDriver();
        return driver == null ? NO_DRIVER : webdriverName(
                driver instanceof TestBenchDriverProxy ? unproxy(driver) : driver);
    }

    public WebElement findElement(By by) {
        return getContext().findElement(by);
    }

    public List<WebElement> findElements(By by) {
        return getContext().findElements(by);
    }

    public <T> T waitUntil(ExpectedCondition<T> condition,
                              long timeoutInSeconds) {
        return new WebDriverWait(getDriver(), timeoutInSeconds)
                .until(condition);
    }

    public <T> T waitUntil(ExpectedCondition<T> condition) {
        return waitUntil(condition, 10);
    }

    /**
     * Executes the given JavaScript in the context of the currently selected
     * frame or window. The script fragment provided will be executed as the
     * body of an anonymous function.
     * <p>
     * This method wraps any returned {@link WebElement} as
     * {@link TestBenchElement}.
     *
     * @param script the script to execute
     * @param args   the arguments, available in the script as
     *               {@code arguments[0]...arguments[N]}
     * @return whatever
     * {@link JavascriptExecutor#executeScript(String, Object...)}
     * returns
     * @throws UnsupportedOperationException if the underlying driver does not support JavaScript
     *                                       execution
     * @see JavascriptExecutor#executeScript(String, Object...)
     */
    public Object executeScript(String script, Object... args) {
        return getCommandExecutor().executeScript(script, args);
    }
}
