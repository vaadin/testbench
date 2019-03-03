/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.com.vaadin.vaadin.addons.testbench;

import static com.vaadin.dependencies.core.properties.PropertiesResolver.propertyReader;
import static com.vaadin.frp.matcher.Case.match;
import static com.vaadin.frp.matcher.Case.matchCase;
import static com.vaadin.frp.model.Result.failure;
import static com.vaadin.frp.model.Result.success;
import static java.lang.Boolean.TRUE;
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.testbench.addons.webdriver.BrowserDriverFunctions;

public class BrowserDriverFunctionsTest implements HasLogger {

  @Test
  @DisplayName("test reading properties")
  @Disabled("test is not not generic enough")
  void test001() {
    Properties properties = propertyReader()
        .apply(BrowserDriverFunctions.CONFIG_FOLDER + "config").get();


    final String unittestingTarget = valueOf(properties.get("unittesting.target")).trim();
    logger().info("unittestingTarget -> #" + unittestingTarget + "#");
    match(
        matchCase(() -> failure("no matching unittesting.target..")) ,
        matchCase(() -> "locale".equals(unittestingTarget) , () -> success(TRUE)) ,
        matchCase(() -> "selenoid.rapidpm.org".equals(unittestingTarget) , () -> success(TRUE)) ,
        matchCase(() -> "selenoid-server".equals(unittestingTarget) , () -> success(TRUE))
    )
        .ifAbsent(fail("no expected property value found for unittesting.target.. " + unittestingTarget));

  }
}
