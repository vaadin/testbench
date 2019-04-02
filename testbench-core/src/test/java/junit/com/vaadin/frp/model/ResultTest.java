package junit.com.vaadin.frp.model;

import com.vaadin.frp.model.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultTest {

    @Test
    public void test001() {
        Result<String> hello = Result.success("Hello");
        Result<String> world = hello.thenCombine("World", (s, s2) -> Result.success(s + " - " + s2));
        assertEquals("Hello - World", world.get());
    }

    @Test
    public void test002() {
        Result<String> hello = Result.success("Hello");
        CompletableFuture<Result<String>> world = hello
                .thenCombineAsync("World", (s, s2) -> Result.success(s + " - " + s2));

        Result<String> result = world.join();
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("Hello - World", result.get());
    }

    @Test
    public void test003() {
        final Result<Integer> asFailure = Result.success("Hello").asFailure();
        assertTrue(asFailure.isAbsent());
        asFailure.ifPresent(v -> Assertions.fail("not good"));
    }

    @Test
    public void test004() {
        final Result<Integer> asFailure = Result.failure("Hello").asFailure();
        assertTrue(asFailure.isAbsent());
        asFailure.ifPresent(v -> Assertions.fail("not good"));
    }
}
