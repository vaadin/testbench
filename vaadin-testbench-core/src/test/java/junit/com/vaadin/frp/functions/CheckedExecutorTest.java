package junit.com.vaadin.frp.functions;

import com.vaadin.frp.functions.CheckedExecutor;
import com.vaadin.frp.model.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckedExecutorTest {

    @Test
    public void test001() {

        final CheckedExecutor e = () -> { /* do magic here */};
        final Result<Void> result = e.execute();
        assertNotNull(result);

        assertFalse(result.isPresent());
        assertTrue(result.isAbsent());

        assertTrue(result instanceof Result.Success);
    }

    @Test
    public void test002() {

        final CheckedExecutor e = () -> {
            throw new RuntimeException("noop");
        };
        final Result<Void> result = e.execute();
        assertNotNull(result);

        assertFalse(result.isPresent());
        assertTrue(result.isAbsent());

        assertTrue(result instanceof Result.Failure);

    }
}
