package com.vaadin.testbench.addons.junit5.pageobject;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.vaadin.testbench.ElementQuery;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;

/**
 *
 */
public abstract class AbstractVaadinPageObject
    extends AbstractPageObject
    implements VaadinPageObject {

  private TestBenchTestCase testCase = new TestBenchTestCase() {};

  public AbstractVaadinPageObject(WebDriver webdriver , ContainerInfo containerInfo) {
    super(webdriver , containerInfo);
    //testbench specific init
    testCase.setDriver(webdriver);
    setDriver(testCase.getDriver());
  }


  //compat Method
  public <T extends TestBenchElement> ElementQuery<T> $(Class<T> clazz) {return testCase.$(clazz);}

  public ElementQuery<TestBenchElement> $(String tagName) {return testCase.$(tagName);}

  public TestBenchCommandExecutor getCommandExecutor() {return testCase.getCommandExecutor();}

  public WebElement findElement(By by) {return testCase.findElement(by);}

  public List<WebElement> findElements(By by) {return testCase.findElements(by);}

}
