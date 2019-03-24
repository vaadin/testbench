package xxx.com.github.webdriverextensions;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class WebPageTest {

  private WebPage opendPage    = new WebPage() {

    @Override
    public void open(Object... arguments) {
      // NoOp - just unit test
    }

    @Override
    public void assertIsOpen(Object... arguments) throws AssertionError {
      // NoOP - page is open
    }

  };
  private WebPage notOpendPage = new WebPage() {

    @Override
    public void open(Object... arguments) {
      // NoOp - just unit test
    }

    @Override
    public void assertIsOpen(Object... arguments) throws AssertionError {
      throw new AssertionError();
    }
  };

  @Test
  public void testAssertIsNotOpen_notOpendPage() {
    try {
      notOpendPage.assertIsNotOpen();
    } catch (AssertionError e) {
      fail("No AssertionError expected");
    }
  }

  @Test
  public void testAssertIsNotOpen_opendPage() {
    Assertions.assertThrows(AssertionError.class, () -> opendPage.assertIsNotOpen());
  }

  @Test
  public void testAssertIsOpen_notOpendPage() {
    Assertions.assertThrows(AssertionError.class, () -> notOpendPage.assertIsOpen());
  }

  @Test
  public void testAssertIsOpen_opendPage() {
    try {
      opendPage.assertIsOpen();
    } catch (AssertionError e) {
      fail("No AssertionError expected");
    }
  }
}
