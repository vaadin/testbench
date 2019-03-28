package com.vaadin.testbench.addons.junit5.pageobject;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

public class WebElementUtil {

  private WebElementUtil() {
    throw new IllegalAccessError("Utitlity class");
  }
  /* Class */
  /**
   * Returns the {@link org.openqa.selenium.WebElement} class attribute.
   *
   * <p>
   * If the {@link org.openqa.selenium.WebElement} does not exist in the html a
   * {@code org.openqa.selenium.NoSuchElementException} will be thrown.
   * </p>
   *
   * <p>
   * <b>Examples:</b>
   *
   * <pre>
   * {@code
   * <input class="a-class another-class"/>
   * classIn(input) = "a-class another-class"
   *
   * no input in html
   * classIn(input) throws org.openqa.selenium.NoSuchElementException}
   * </pre>
   * <p> </p>
   *
   * @param webElement the {@link org.openqa.selenium.WebElement} containing a class attribute
   * @return the class attribute
   */
  public static String classIn(WebElement webElement) {
    return attributeIn("class", webElement);
  }

  /**
   * Returns the classes in the {@link org.openqa.selenium.WebElement} class attribute.
   *
   * <p>
   * If the {@link org.openqa.selenium.WebElement} does not exist in the html a
   * {@code org.openqa.selenium.NoSuchElementException} will be thrown.
   * </p>
   *
   * <p>
   * <b>Examples:</b>
   *
   * <pre>
   * {@code
   * <input class=" a-class   another-class "/>
   * classesIn(input) = "a-class", "another-class"
   *
   * no input in html
   * classIn(input) throws org.openqa.selenium.NoSuchElementException}
   * </pre>
   * <p> </p>
   *
   * @param webElement the {@link org.openqa.selenium.WebElement} containing a class attribute
   * @return the classes in the class attribute
   */
  public static List<String> classesIn(WebElement webElement) {
    return Arrays.asList(StringUtils.split(classIn(webElement)));
  }

  public static boolean hasClass(WebElement webElement) {
    return hasAttribute("class", webElement);
  }

  public static boolean hasNotClass(WebElement webElement) {
    return hasNotAttribute("class", webElement);
  }

  public static boolean hasClass(String className, WebElement webElement) {
    List<String> classes = classesIn(webElement);
    for (String clazz : classes) {
      if (StringUtils.equals(className, clazz)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasNotClass(String className, WebElement webElement) {
    return !hasClass(className, webElement);
  }

  public static boolean hasClassContaining(String searchText, WebElement webElement) {
    List<String> classes = classesIn(webElement);
    for (String clazz : classes) {
      if (StringUtils.contains(searchText, clazz)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasNotClassContaining(String searchText, WebElement webElement) {
    return !hasClassContaining(searchText, webElement);
  }

  public static boolean hasClassStartingWith(String prefix, WebElement webElement) {
    List<String> classes = classesIn(webElement);
    for (String clazz : classes) {
      if (StringUtils.startsWith(prefix, clazz)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasNotClassStartingWith(String prefix, WebElement webElement) {
    return !hasClassStartingWith(prefix, webElement);
  }

  public static boolean hasClassEndingWith(String suffix, WebElement webElement) {
    List<String> classes = classesIn(webElement);
    for (String clazz : classes) {
      if (StringUtils.endsWith(suffix, clazz)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasNotClassEndingWith(String suffix, WebElement webElement) {
    return !hasClassEndingWith(suffix, webElement);
  }

  public static boolean hasClassMatching(String regExp, WebElement webElement) {
    List<String> classes = classesIn(webElement);
    for (String clazz : classes) {
      if (matches(regExp, clazz)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasNotClassMatching(String regExp, WebElement webElement) {
    return !hasClassMatching(regExp, webElement);
  }

  /* Attribute */
  /**
   * Returns a {@link org.openqa.selenium.WebElement} attribute value.
   *
   * <p>
   * If the {@link org.openqa.selenium.WebElement} does not exist in the html a
   * {@code org.openqa.selenium.NoSuchElementException} will be thrown.
   * </p>
   *
   * <p>
   * <b>Examples:</b>
   *
   * <pre>
   * {@code
   * <input title="Some title"/>
   * attributeIn("title", input) = "Some title"
   *
   * no input in html
   * attributeIn("title", "input) throws org.openqa.selenium.NoSuchElementException}
   * </pre>
   * <p> </p>
   *
   * @param webElement the {@link org.openqa.selenium.WebElement} containing an attribute to return
   * @return the id attribute
   */
  public static String attributeIn(String name, WebElement webElement) {
    return webElement.getAttribute(name);
  }

  public static boolean hasAttribute(String name, WebElement webElement) {
    return webElement.getAttribute(name) != null;
  }

  public static boolean hasNotAttribute(String name, WebElement webElement) {
    return !hasAttribute(name, webElement);
  }

  private static boolean matches(String regularExpression, String text) {
    if (text == null || regularExpression == null) {
      return false;
    }
    return text.matches(regularExpression);
  }
}
