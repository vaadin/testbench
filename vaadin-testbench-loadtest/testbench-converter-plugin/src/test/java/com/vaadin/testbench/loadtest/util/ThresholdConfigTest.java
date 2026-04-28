/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThresholdConfigTest {

    @Test
    void defaultThresholds() {
        String block = new ThresholdConfig().toK6ThresholdsBlock();
        assertTrue(block.contains("'p(95)<2000'"));
        assertTrue(block.contains("'p(99)<5000'"));
        assertTrue(block.contains("abortOnFail: true"));
        // Default tolerates 1% failed checks
        assertTrue(block.contains("threshold: 'rate>=0.99'"));
    }

    @Test
    void disabledP95() {
        ThresholdConfig config = new ThresholdConfig()
                .withHttpReqDurationP95(0);
        String block = config.toK6ThresholdsBlock();
        assertFalse(block.contains("p(95)"));
        assertTrue(block.contains("'p(99)<5000'"));
    }

    @Test
    void disabledP99() {
        ThresholdConfig config = new ThresholdConfig()
                .withHttpReqDurationP99(0);
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("'p(95)<2000'"));
        assertFalse(block.contains("p(99)"));
    }

    @Test
    void checksWithoutAbort() {
        ThresholdConfig config = new ThresholdConfig()
                .withChecksAbortOnFail(false);
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("checks: ['rate>=0.99']"));
        assertFalse(block.contains("abortOnFail"));
    }

    @Test
    void customThresholdAdded() {
        ThresholdConfig config = new ThresholdConfig()
                .withCustomThreshold("http_req_failed", "rate<0.01");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("http_req_failed: ['rate<0.01']"));
    }

    @Test
    void multipleCustomThresholdsForSameMetric() {
        ThresholdConfig config = new ThresholdConfig()
                .withCustomThreshold("http_req_waiting", "p(95)<500")
                .withCustomThreshold("http_req_waiting", "p(99)<1000");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block
                .contains("http_req_waiting: ['p(95)<500', 'p(99)<1000']"));
    }

    @Test
    void customDurationOverridesMatchingDefaults() {
        ThresholdConfig config = new ThresholdConfig()
                .withCustomThreshold("http_req_duration", "p(95)<500");
        String block = config.toK6ThresholdsBlock();
        // Custom p(95) overrides default p(95)
        assertTrue(block.contains("'p(95)<500'"));
        assertFalse(block.contains("'p(95)<2000'"));
        // Default p(99) is kept
        assertTrue(block.contains("'p(99)<5000'"));
    }

    @Test
    void customDurationKeepsDefaultsWhenNoOverlap() {
        ThresholdConfig config = new ThresholdConfig()
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
        ThresholdConfig config = new ThresholdConfig().withHttpReqDurationP95(0)
                .withHttpReqDurationP99(0)
                .withCustomThreshold("http_req_duration", "p(90)<3000");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("http_req_duration: ['p(90)<3000']"));
        assertFalse(block.contains("p(95)"));
        assertFalse(block.contains("p(99)"));
    }

    @Test
    void customDurationOverridingDefaultsNoDuplicates() {
        ThresholdConfig config = new ThresholdConfig()
                .withCustomThreshold("http_req_duration", "p(95)<500")
                .withCustomThreshold("http_req_duration", "p(99)<1500");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("'p(95)<500'"));
        assertTrue(block.contains("'p(99)<1500'"));
        // No default values duplicated
        assertFalse(block.contains("'p(95)<2000'"));
        assertFalse(block.contains("'p(99)<5000'"));
        // Each percentile appears exactly once
        assertEquals(1, countOccurrences(block, "p(95)"));
        assertEquals(1, countOccurrences(block, "p(99)"));
    }

    private int countOccurrences(String text, String substring) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(substring, idx)) != -1) {
            count++;
            idx += substring.length();
        }
        return count;
    }

    @Test
    void editedDefaultThresholds() {
        ThresholdConfig config = new ThresholdConfig()
                .withHttpReqDurationP95(1000).withHttpReqDurationP99(3000)
                .withChecksAbortOnFail(false);
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("'p(95)<1000'"));
        assertTrue(block.contains("'p(99)<3000'"));
        assertTrue(block.contains("checks: ['rate>=0.99']"));
    }

    @Test
    void zeroAllowedFailureRateRequiresAllChecksToPass() {
        ThresholdConfig config = new ThresholdConfig()
                .withChecksAllowedFailureRate(0.0);
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("threshold: 'rate==1'"));
    }

    @Test
    void customAllowedFailureRate() {
        ThresholdConfig config = new ThresholdConfig()
                .withChecksAbortOnFail(false)
                .withChecksAllowedFailureRate(0.10);
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("checks: ['rate>=0.9']"));
    }

    @Test
    void invalidAllowedFailureRateThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ThresholdConfig().withChecksAllowedFailureRate(1.0),
                "Should have thrown for invalid failure rate");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ThresholdConfig().withChecksAllowedFailureRate(-0.01),
                "Should have thrown for negative failure rate");

    }

    @Test
    void allowedFailureRatePreservedThroughCustomThresholds() {
        ThresholdConfig config = new ThresholdConfig()
                .withChecksAllowedFailureRate(0.02)
                .withCustomThreshold("http_req_failed", "rate<0.01");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("threshold: 'rate>=0.98'"));
        assertTrue(block.contains("http_req_failed: ['rate<0.01']"));
    }

    @Test
    void parseCustomThresholdsFromString() {
        ThresholdConfig config = new ThresholdConfig().withCustomThresholds(
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
        ThresholdConfig config = new ThresholdConfig().withCustomThresholds(
                "http_req_failed:rate<0.01,,  ,http_reqs:count>100");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block.contains("http_req_failed: ['rate<0.01']"));
        assertTrue(block.contains("http_reqs: ['count>100']"));
    }

    @Test
    void parseCustomThresholdsInvalidFormatThrows() {
        try {
            new ThresholdConfig().withCustomThresholds("invalid-no-colon");
            throw new AssertionError("Should have thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Invalid custom threshold"));
        }
    }

    @Test
    void multipleCustomMetrics() {
        ThresholdConfig config = new ThresholdConfig()
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
        ThresholdConfig config = new ThresholdConfig().withCustomThresholds(
                "http_req_waiting:p(95)<1000,http_req_waiting:p(99)<2000");
        String block = config.toK6ThresholdsBlock();
        assertTrue(block
                .contains("http_req_waiting: ['p(95)<1000', 'p(99)<2000']"));
        // Defaults still present
        assertTrue(block.contains("'p(95)<2000'"));
        assertTrue(block.contains("'p(99)<5000'"));
        assertTrue(block.contains("abortOnFail: true"));
    }

}
