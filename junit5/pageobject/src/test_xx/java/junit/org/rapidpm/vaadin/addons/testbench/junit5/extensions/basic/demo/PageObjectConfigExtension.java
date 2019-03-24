package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo;


import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.testbench.addons.testbench.junit5.extensions.container.NetworkFunctions;

public class PageObjectConfigExtension implements BeforeAllCallback, BeforeEachCallback,
    AfterEachCallback, AfterAllCallback, HasLogger {


  @Override
  public void afterAll(ExtensionContext context) throws Exception {

  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {

  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {

  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    System.setProperty(NetworkFunctions.SERVER_WEBAPP, DemoUI.class.getName());
  }
}
