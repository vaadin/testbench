package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.github.webdriverextensions.vaadin.VaadinConditions;


public class WaitUtil {
  private final WebDriver webDriver;

  public WaitUtil(WebDriver driver) {
    super();
    this.webDriver = driver;
  }

  public void waitForVaadin() {
    new WebDriverWait(webDriver, 1).until(webDriver -> VaadinConditions.ajaxCallsCompleted().test(webDriver));
  }
}
