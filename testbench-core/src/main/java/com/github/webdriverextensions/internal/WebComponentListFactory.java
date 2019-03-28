package com.github.webdriverextensions.internal;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.github.webdriverextensions.WebComponent;

public interface WebComponentListFactory {

  <T extends WebComponent> List<T> create(Class<T> webComponentClass , List<WebElement> webElements ,
                                          WebDriver driver , ParameterizedType genericTypeArguments);
}
