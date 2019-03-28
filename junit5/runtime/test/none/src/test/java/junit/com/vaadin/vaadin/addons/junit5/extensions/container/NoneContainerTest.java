package junit.com.vaadin.vaadin.addons.junit5.extensions.container;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfoExtension;
import com.vaadin.testbench.addons.junit5.extensions.container.ServletContainerExtension;

@ExtendWith(ServletContainerExtension.class)
class NoneContainerTest {

  @RegisterExtension ContainerInfoExtension config = new ContainerInfoExtension();

  @Test
  void test001() {
    Assertions.assertEquals("127.0.0.1", config.host());
    Assertions.assertEquals(8080, config.port());
//    Assertions.assertEquals("webApp", config.); x
  }


}
