package junit.com.vaadin.frp.functions;

import com.vaadin.frp.functions.CheckedPredicate;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckedPredicateTest {

    @Test
    public void test001() {
        CheckedPredicate<String> p = s -> {
            throw new RuntimeException("foo");
        };
        assertFalse(p.test(""));
    }

    @Test
    public void test002() {
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
