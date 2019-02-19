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
package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo.BasicTestPageObject;
import junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo.PageObjectConfigExtension;

@WebUnitTest
@ExtendWith(PageObjectConfigExtension.class)
class BasicUnitTest {

  @Test
  void test001(BasicTestPageObject pageObject) {
    WaitUtil waitUtil = new WaitUtil(pageObject.getDriver());
    pageObject.loadPage();
    waitUtil.waitForVaadin();
    assertThat(pageObject.getComponent().getSubComponents().size(), is(0));
    pageObject.getComponent().clickButton();
    waitUtil.waitForVaadin();
    assertThat(pageObject.getComponent().getSubComponents().size(), is(1));
    pageObject.screenshot();
  }
}
