package junit.com.vaadin.frp.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import com.vaadin.frp.functions.CheckedSupplier;
import com.vaadin.frp.model.Result;

/**
 *
 */
public class CheckedSupplierTest {

  @Test
  public void test001() throws Exception {
    Result<String> result = ((CheckedSupplier<String>) () -> "Hello").get();
    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals("Hello", result.get());
  }

  @Test
  public void test002() throws Exception {
    Result<String> result = ((CheckedSupplier<String>) () -> {
      throw new RuntimeException("Hello");
    })
        .get();
    assertNotNull(result);
    assertTrue(result.isAbsent());
  }
}
