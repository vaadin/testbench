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

import com.vaadin.testbench.ElementQuery;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public abstract class AbstractVaadinPageObject
        extends AbstractPageObject
        implements VaadinPageObject {

    private TestBenchTestCase testCase = new TestBenchTestCase() { };

    public AbstractVaadinPageObject(WebDriver webdriver, ContainerInfo containerInfo) {
        super(webdriver, containerInfo);

        // Testbench-specific init.
        testCase.setDriver(webdriver);
        setDriver(testCase.getDriver());
    }

    public <T extends TestBenchElement> ElementQuery<T> $(Class<T> clazz) {
        return testCase.$(clazz);
    }

    public ElementQuery<TestBenchElement> $(String tagName) {
        return testCase.$(tagName);
    }

    public TestBenchCommandExecutor getCommandExecutor() {
        return testCase.getCommandExecutor();
    }

    public WebElement findElement(By by) {
        return testCase.findElement(by);
    }

    public List<WebElement> findElements(By by) {
        return testCase.findElements(by);
    }

    public TestBenchTestCase getTestCase() {
        return testCase;
    }
}
