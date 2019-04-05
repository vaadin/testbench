package junit.com.vaadin.frp.functions;

import com.vaadin.frp.functions.CheckedBiFunction;
import com.vaadin.frp.model.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckedBiFunctionTest {

    @Test
    public void test001() {

        final Result<String> result = ((CheckedBiFunction<String, String, String>) (s1, s2) -> "ok")
                .apply("Hello", "World");

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("ok", result.get());
    }

    @Test
    public void test002() {

        final Result<String> result = ((CheckedBiFunction<String, String, String>) (s1, s2) -> {
            throw new RuntimeException("noop");
        })
                .apply("Hello", "World");

        assertNotNull(result);
        assertTrue(result.isAbsent());
    }
}
