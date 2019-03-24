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
