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
package junit.com.vaadin.testbench.tests.demo;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinWebUnitTest;

@VaadinWebUnitTest
class VaadinAppWebUnitTest {



  @DisplayName("Hello World - Click twice 001")
  @VaadinWebUnitTest
  void test001(VaadinAppPageObject pageObject) {
    pageObject.loadPage();
    assertEquals(0 , pageObject.clickCount());
    pageObject.click();
    assertEquals(1 , pageObject.clickCount());
  }

  @DisplayName("Hello World - Click twice 002")
  @VaadinWebUnitTest
  void test002(VaadinAppPageObject pageObject) {
    pageObject.loadPage();
    assertEquals(0 , pageObject.clickCount());
    pageObject.click();
    assertEquals(1 , pageObject.clickCount());
  }

  @DisplayName("Hello World - Click twice 003")
  @VaadinWebUnitTest
  void test003(VaadinAppPageObject pageObject) {
    pageObject.loadPage();
    assertEquals(0 , pageObject.clickCount());
    pageObject.click();
    assertEquals(1 , pageObject.clickCount());
  }

  @DisplayName("Hello World - Click twice 004")
  @VaadinWebUnitTest
  void test004(VaadinAppPageObject pageObject) {
    pageObject.loadPage();
    assertEquals(0 , pageObject.clickCount());
    pageObject.click();
    assertEquals(1 , pageObject.clickCount());
  }

}
