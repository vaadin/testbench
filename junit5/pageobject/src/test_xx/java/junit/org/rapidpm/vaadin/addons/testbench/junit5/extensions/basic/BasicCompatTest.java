package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import com.vaadin.testbench.addons.testbench.junit5.extension.compattest.WebCompatTest;
import junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo.BasicTestPageObject;
import junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo.PageObjectConfigExtension;

/**
 *
 */
@WebCompatTest
@ExtendWith(PageObjectConfigExtension.class)
class BasicCompatTest {

  @TestTemplate
  void testTemplate(BasicTestPageObject pageObject) {
    WaitUtil waitUtil = new WaitUtil(pageObject.getDriver());

    pageObject.loadPage();
    waitUtil.waitForVaadin();

    assertThat(pageObject.getComponent().getSubComponents().size(), is(0));
    pageObject.screenshot();
  }
}
