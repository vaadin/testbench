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
package com.vaadin.testbench.addons.junit5.pageobject;

import static com.vaadin.frp.matcher.Case.match;
import static com.vaadin.frp.matcher.Case.matchCase;
import static com.vaadin.frp.model.Result.failure;
import static com.vaadin.frp.model.Result.success;
import static com.vaadin.testbench.addons.testbench.TestbenchFunctions.unproxy;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;

import org.openqa.selenium.WebDriver;
import com.vaadin.testbench.TestBenchDriverProxy;

/**
 *
 */
public interface VaadinPageObject extends GenericVaadinAppSpecific {


  String NO_DRIVER = "NoDriver";

  default String drivername() {
    final WebDriver driver = getDriver();
    return match(
        matchCase(() -> failure("no driver present")) ,
        matchCase(() -> driver instanceof TestBenchDriverProxy , () -> success(webdriverName().apply(unproxy().apply(driver)))) ,
        matchCase(() -> driver != null , () -> success(webdriverName().apply(driver)))
    )
        .ifFailed(failed -> logger().warning(failed))
        .getOrElse(() -> NO_DRIVER);
  }

//  default void loadPage(Class<? extends Component> route) {
////    UI.getCurrent().navigate(route);
//    logger().info("Navigate browser to " + url + route);
//    getDriver().get(url + route);
//  }


}
