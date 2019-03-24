package junit.com.vaadin.vaadin.addons.junit5.extensions.container;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.vaadin.testbench.addons.junit5.extensions.container.UpperCaseService;

public class UpperCaseServiceTest {

  @Test
  void test001() {
    Assertions.assertEquals("HALLO", new UpperCaseService().upperCase("hallo"));
  }
}
