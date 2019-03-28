package com.github.webdriverextensions;


import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class PropertyUtils {

  private PropertyUtils() { }

  public static boolean isTrue(String key) {
    return BooleanUtils.toBoolean(System.getProperty(key));
  }

  public static boolean propertyExists(String key) {
    return System.getProperty(key) != null;
  }

  public static void setPropertyIfNotExists(String key, String value) {
    if (value == null || StringUtils.isBlank(value)) {
      return;
    }
    if (!propertyExists(key)) {
      System.setProperty(key, value);
    }
  }
}
