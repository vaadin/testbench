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
package xxx.com.github.webdriverextensions.internal.utils;

import static org.apache.commons.lang3.StringUtils.contains;

public class StringUtils {

  private StringUtils() {
    throw new IllegalAccessError("Utitiy class");
  }

  public static String appendNewLineIfContainsNewLine(String string) {
    if (contains(string, "\n")) {
      return "\n" + string;
    }
    return string;
  }

  public static String surroundNewLinesIfContainsNewLine(String string) {
    if (contains(string, "\n")) {
      return "\n" + string + "\n";
    }
    return string;
  }

  public static String prependSpaceIfNotBlank(String string) {
    if (org.apache.commons.lang3.StringUtils.isNotBlank(string)) {
      return " " + string;
    }
    return string;
  }

  public static String quote(String text) {
    return "\"" + text + "\"";
  }

  public static String indent(String string, String indent) {
    return string.replaceAll("\n", "\n" + indent);
  }

  public static boolean isBlank(String string) {
    return org.apache.commons.lang3.StringUtils.isBlank(string);
  }
}
