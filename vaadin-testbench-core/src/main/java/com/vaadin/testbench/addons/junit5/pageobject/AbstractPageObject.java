package com.vaadin.testbench.addons.junit5.pageobject;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Copyright (C) ${year} Vaadin Ltd
 * 
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

import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import org.openqa.selenium.WebDriver;

public abstract class AbstractPageObject implements PageObject {

    private WebDriver driver;
    private ContainerInfo containerInfo;

    public AbstractPageObject(WebDriver webdriver, ContainerInfo containerInfo) {
        setDriver(webdriver);
        setContainerInfo(containerInfo);
    }

    @Override
    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public ContainerInfo getContainerInfo() {
        return containerInfo;
    }

    public void setContainerInfo(ContainerInfo containerInfo) {
        this.containerInfo = containerInfo;
    }
}
