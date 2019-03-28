package com.vaadin.testbench.addons.junit5.pageobject;

import org.openqa.selenium.WebDriver;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;

public abstract class AbstractPageObject implements PageObject {


  public AbstractPageObject(WebDriver webdriver , ContainerInfo containerInfo) {
    setDriver(webdriver);
    setContainerInfo(containerInfo);
  }

  private WebDriver driver;
  private ContainerInfo containerInfo;


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
