package junit.com.vaadin.frp.functions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import com.vaadin.frp.functions.CheckedPredicate;

/**
 *
 */
public class CheckedPredicateTest {

  @Test
  public void test001() {
    CheckedPredicate<String> p = s -> {
      throw new RuntimeException("foo");
    };
    assertFalse(p.test(""));
  }

  @Test
  public void test002()  {
    CheckedPredicate<String> p = s -> {
      throw new IOException("foo");
    };
    assertFalse(p.test(""));
  }

  @Test
  public void test003() {
    CheckedPredicate<String> p = s -> s.equals("foo");
    assertFalse(p.test(""));
    assertTrue(p.test("foo"));
  }


}
