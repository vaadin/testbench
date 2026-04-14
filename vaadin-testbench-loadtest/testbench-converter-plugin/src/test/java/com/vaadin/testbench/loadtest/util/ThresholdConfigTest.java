/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThresholdConfigTest {

    @Test
    void defaultThresholds() {
        String block = ThresholdConfig.DEFAULT.toK6ThresholdsBlock();
        assertTrue(block.contains("'p(95)<2000'"));
        assertTrue(block.contains("'p(99)<5000'"));
        assertTrue(block.contains("abortOnFail: true"));
    }

    @Test
    void disabledP95() {
        ThresholdConfig config = new ThresholdConfig(0, 5000, true);
        String block = config.toK6ThresholdsBlock();
        assertFalse(block.contains("p(95)"));
        assertTrue(block.contains("'p(99)<5000'"));
    }

    @Test
    void disabledP99() {
        ThresholdConfig config = new ThresholdConfig(2000, 0, true);
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("'p(95)<2000'"));
        assertFalse(block.contains("p(99)"));
    }

    @Test
    void checksWithoutAbort() {
        ThresholdConfig config = new ThresholdConfig(2000, 5000, false);
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("checks: ['rate==1']"));
        assertFalse(block.contains("abortOnFail"));
    }

    @Test
    void customThresholdAdded() {
        ThresholdConfig config = ThresholdConfig.DEFAULT
                .withCustomThreshold("http_req_failed", "rate<0.01");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("http_req_failed: ['rate<0.01']"));
    }

    @Test
    void multipleCustomThresholdsForSameMetric() {
        ThresholdConfig config = ThresholdConfig.DEFAULT
                .withCustomThreshold("http_req_waiting", "p(95)<500")
                .withCustomThreshold("http_req_waiting", "p(99)<1000");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block
                .contains("http_req_waiting: ['p(95)<500', 'p(99)<1000']"));
    }

    @Test
    void customDurationMergedWithDefaults() {
        ThresholdConfig config = ThresholdConfig.DEFAULT
                .withCustomThreshold("http_req_duration", "p(50)<1000");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("'p(95)<2000'"));
        assertTrue(block.contains("'p(99)<5000'"));
        assertTrue(block.contains("'p(50)<1000'"));
        // All in the same http_req_duration line
        assertTrue(block.contains(
                "http_req_duration: ['p(95)<2000', 'p(99)<5000', 'p(50)<1000']"));
    }

    @Test
    void customDurationWithDefaultsDisabled() {
        ThresholdConfig config = new ThresholdConfig(0, 0, true)
                .withCustomThreshold("http_req_duration", "p(90)<3000");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("http_req_duration: ['p(90)<3000']"));
        assertFalse(block.contains("p(95)"));
        assertFalse(block.contains("p(99)"));
    }

    @Test
    void editedDefaultThresholds() {
        ThresholdConfig config = new ThresholdConfig(1000, 3000, false);
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("'p(95)<1000'"));
        assertTrue(block.contains("'p(99)<3000'"));
        assertTrue(block.contains("checks: ['rate==1']"));
    }

    @Test
    void withCustomThresholdIsImmutable() {
        ThresholdConfig original = ThresholdConfig.DEFAULT;
        ThresholdConfig modified = original
                .withCustomThreshold("http_req_failed", "rate<0.01");
        // Original should not be affected
        assertFalse(original.toK6ThresholdsBlock().contains("http_req_failed"));
        assertTrue(modified.toK6ThresholdsBlock().contains("http_req_failed"));
    }

    @Test
    void parseCustomThresholdsFromString() {
        ThresholdConfig config = ThresholdConfig.DEFAULT.withCustomThresholds(
                "http_req_failed:rate<0.01,http_req_waiting:p(95)<500");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("http_req_failed: ['rate<0.01']"));
        assertTrue(block.contains("http_req_waiting: ['p(95)<500']"));
        // Defaults still present
        assertTrue(block.contains("'p(95)<2000'"));
        assertTrue(block.contains("'p(99)<5000'"));
    }

    @Test
    void parseCustomThresholdsIgnoresEmptyEntries() {
        ThresholdConfig config = ThresholdConfig.DEFAULT.withCustomThresholds(
                "http_req_failed:rate<0.01,,  ,http_reqs:count>100");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("http_req_failed: ['rate<0.01']"));
        assertTrue(block.contains("http_reqs: ['count>100']"));
    }

    @Test
    void parseCustomThresholdsInvalidFormatThrows() {
        try {
            ThresholdConfig.DEFAULT.withCustomThresholds("invalid-no-colon");
            throw new AssertionError("Should have thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Invalid custom threshold"));
        }
    }

    @Test
    void multipleCustomMetrics() {
        ThresholdConfig config = ThresholdConfig.DEFAULT
                .withCustomThreshold("http_req_failed", "rate<0.01")
                .withCustomThreshold("http_req_waiting", "p(95)<500")
                .withCustomThreshold("http_reqs", "count>100");
        String block = config.toK6ThresholdsBlock();

        // Verify all custom metrics are present
        assertTrue(block.contains("http_req_failed: ['rate<0.01']"));
        assertTrue(block.contains("http_req_waiting: ['p(95)<500']"));
        assertTrue(block.contains("http_reqs: ['count>100']"));

        // Verify defaults are still present
        assertTrue(block.contains("'p(95)<2000'"));
        assertTrue(block.contains("'p(99)<5000'"));
        assertTrue(block.contains("abortOnFail: true"));
    }

    @Test
    void defaultWithCustomWaitingThresholds() {
        ThresholdConfig config = ThresholdConfig.DEFAULT.withCustomThresholds(
                "http_req_waiting:p(95)<1000,http_req_waiting:p(99)<2000");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block
                .contains("http_req_waiting: ['p(95)<1000', 'p(99)<2000']"));
        // Defaults still present
        assertTrue(block.contains("'p(95)<2000'"));
        assertTrue(block.contains("'p(99)<5000'"));
        assertTrue(block.contains("abortOnFail: true"));
    }

    @Test
    void backwardsCompatibleConstructor() {
        ThresholdConfig config = new ThresholdConfig(2000, 5000, true);
        assertEquals(ThresholdConfig.DEFAULT.toK6ThresholdsBlock(),
                config.toK6ThresholdsBlock());
    }
}
