package com.vaadin.testbench.addons.junit5.pageobject;

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
