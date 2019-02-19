/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
