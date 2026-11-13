/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

/**
 * Polls a condition until it holds, as Selenium's {@code WebDriverWait} does:
 *
 * <pre>{@code
 * TestBenchElement row = Wait.until(() -> grid.$("tr").single(),
 *         Duration.ofSeconds(10));
 * }</pre>
 *
 * The condition holds once it returns neither {@code null} nor {@code false}.
 * It runs every 500 ms, and once more after the timeout. A
 * {@link NoSuchElementException} counts as "not yet"; any other exception ends
 * the wait.
 */
public final class Wait {

    private static final Duration POLLING_INTERVAL = Duration.ofMillis(500);

    private Wait() {
    }

    /**
     * @param condition
     *            its {@code toString()} names it in the timeout's message
     * @return the condition's first value that is neither {@code null} nor
     *         {@code false}
     * @throws TimeoutException
     *             if the condition does not hold within {@code timeout}; its
     *             cause is the {@link NoSuchElementException} of the last try,
     *             if that try threw one
     */
    public static <T> T until(Supplier<? extends @Nullable T> condition,
            Duration timeout) {
        long end = System.nanoTime() + timeout.toNanos();
        while (true) {
            NoSuchElementException lastException = null;
            try {
                @Nullable
                T value = condition.get();
                if (value != null && !Boolean.FALSE.equals(value)) {
                    return value;
                }
            } catch (NoSuchElementException e) {
                lastException = e;
            }
            if (System.nanoTime() - end > 0) {
                String message = String.format(
                        "Expected condition failed: waiting for %s%n"
                                + "(tried for %s with %d milliseconds interval)",
                        condition, formatTimeout(timeout),
                        POLLING_INTERVAL.toMillis());
                throw lastException == null ? new TimeoutException(message)
                        : new TimeoutException(message, lastException);
            }
            try {
                Thread.sleep(POLLING_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                        "Interrupted while waiting for " + condition, e);
            }
        }
    }

    // "10 seconds", "1 second", "0.5 seconds"
    private static String formatTimeout(Duration timeout) {
        BigDecimal seconds = BigDecimal.valueOf(timeout.toMillis())
                .divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
        return seconds.stripTrailingZeros().toPlainString()
                + (seconds.compareTo(BigDecimal.ONE) == 0 ? " second"
                        : " seconds");
    }
}
