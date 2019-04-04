package junit.com.vaadin.frp;

import com.vaadin.frp.StreamFunctions;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreamFunctionsTest {

    @Test
    void test001() {

        final Set<Integer> set = StreamFunctions.<Integer>streamFilter()
                .apply(integer -> integer.equals(5))
                .apply(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9))
                .collect(toSet());

        assertEquals(1, set.size());
        set.forEach(i -> assertEquals(Long.valueOf(i), Long.valueOf(5)));
    }
}
