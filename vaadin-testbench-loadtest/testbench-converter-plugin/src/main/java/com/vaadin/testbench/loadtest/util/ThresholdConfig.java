/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for k6 test thresholds. Controls when the load test is
 * considered failed based on response times and check pass rates.
 * <p>
 * Default thresholds include {@code http_req_duration} p95/p99 and
 * {@code checks rate==1}. Additional thresholds for any k6 metric can be added
 * via {@link #withCustomThreshold(String, String)}, and the defaults can be
 * edited via the constructor parameters or disabled by passing 0.
 *
 * @param httpReqDurationP95
 *            95th percentile response time threshold in ms (0 to disable)
 * @param httpReqDurationP99
 *            99th percentile response time threshold in ms (0 to disable)
 * @param checksAbortOnFail
 *            if true, abort the test immediately when a check fails
 * @param customThresholds
 *            additional thresholds keyed by k6 metric name, each with a list of
 *            threshold expressions (e.g. {@code "p(95)<500"} or
 *            {@code "rate<0.01"})
 */
public record ThresholdConfig(int httpReqDurationP95, int httpReqDurationP99,
        boolean checksAbortOnFail, Map<String, List<String>> customThresholds) {

    /**
     * Canonical constructor that makes a defensive copy of custom thresholds.
     */
    public ThresholdConfig {
        customThresholds = customThresholds != null
                ? new LinkedHashMap<>(customThresholds)
                : new LinkedHashMap<>();
    }

    /**
     * Backwards-compatible constructor without custom thresholds.
     *
     * @param httpReqDurationP95
     *            95th percentile response time threshold in ms (0 to disable)
     * @param httpReqDurationP99
     *            99th percentile response time threshold in ms (0 to disable)
     * @param checksAbortOnFail
     *            if true, abort the test immediately when a check fails
     */
    public ThresholdConfig(int httpReqDurationP95, int httpReqDurationP99,
            boolean checksAbortOnFail) {
        this(httpReqDurationP95, httpReqDurationP99, checksAbortOnFail,
                new LinkedHashMap<>());
    }

    /**
     * Default thresholds: p95 &lt; 2000ms, p99 &lt; 5000ms, abort on check
     * failure.
     */
    public static final ThresholdConfig DEFAULT = new ThresholdConfig(2000,
            5000, true);

    /**
     * Returns a new {@link ThresholdConfig} with an additional custom threshold
     * expression for the given k6 metric. Multiple expressions can be added for
     * the same metric by calling this method repeatedly.
     * <p>
     * Example usage:
     * 
     * <pre>
     * ThresholdConfig config = ThresholdConfig.DEFAULT
     *         .withCustomThreshold("http_req_waiting", "p(95)&lt;500")
     *         .withCustomThreshold("http_req_failed", "rate&lt;0.01")
     *         .withCustomThreshold("http_req_duration", "p(50)&lt;1000");
     * </pre>
     * <p>
     * Custom thresholds for {@code http_req_duration} override matching default
     * thresholds (e.g. a custom {@code p(95)} replaces the default p95, while
     * the default p99 is kept unless also overridden).
     *
     * @param metric
     *            the k6 metric name (e.g. {@code "http_req_waiting"},
     *            {@code "http_req_failed"}, {@code "http_reqs"})
     * @param expression
     *            the threshold expression (e.g. {@code "p(95)<500"},
     *            {@code "rate<0.01"}, {@code "count>100"})
     * @return a new ThresholdConfig with the additional threshold
     */
    public ThresholdConfig withCustomThreshold(String metric,
            String expression) {
        Map<String, List<String>> merged = new LinkedHashMap<>(
                customThresholds);
        merged.computeIfAbsent(metric, k -> new ArrayList<>()).add(expression);
        return new ThresholdConfig(httpReqDurationP95, httpReqDurationP99,
                checksAbortOnFail, merged);
    }

    /**
     * Parses a custom thresholds string and applies each entry to this config.
     * Format: {@code metric:expression,metric:expression,...}
     *
     * @param thresholds
     *            the custom thresholds string to parse
     * @return a new ThresholdConfig with the custom thresholds applied
     * @throws IllegalArgumentException
     *             if the format is invalid
     */
    public ThresholdConfig withCustomThresholds(String thresholds) {
        ThresholdConfig result = this;
        for (String entry : thresholds.split(",")) {
            entry = entry.trim();
            if (entry.isEmpty()) {
                continue;
            }
            int colonIndex = entry.indexOf(':');
            if (colonIndex <= 0 || colonIndex >= entry.length() - 1) {
                throw new IllegalArgumentException(
                        "Invalid custom threshold format: '" + entry
                                + "'. Expected 'metric:expression' (e.g., 'http_req_failed:rate<0.01')");
            }
            String metric = entry.substring(0, colonIndex).trim();
            String expression = entry.substring(colonIndex + 1).trim();
            result = result.withCustomThreshold(metric, expression);
        }
        return result;
    }

    /**
     * Generates the k6 thresholds block for use inside
     * {@code export const options}. Example output:
     *
     * <pre>
     *   thresholds: {
     *     checks: [{ threshold: 'rate==1', abortOnFail: true, delayAbortEval: '5s' }],
     *     http_req_duration: ['p(95)&lt;2000', 'p(99)&lt;5000'],
     *     http_req_failed: ['rate&lt;0.01'],
     *   },
     * </pre>
     *
     * @return the k6 thresholds block as a string
     */
    public String toK6ThresholdsBlock() {
        StringBuilder sb = new StringBuilder();
        sb.append("  thresholds: {\n");

        // checks threshold
        if (checksAbortOnFail) {
            sb.append(
                    "    checks: [{ threshold: 'rate==1', abortOnFail: true, delayAbortEval: '5s' }],\n");
        } else {
            sb.append("    checks: ['rate==1'],\n");
        }

        // http_req_duration: custom expressions override matching defaults
        List<String> durationExpressions = new ArrayList<>();
        List<String> customDuration = customThresholds
                .getOrDefault("http_req_duration", List.of());
        boolean customHasP95 = customDuration.stream()
                .anyMatch(e -> e.startsWith("p(95)"));
        boolean customHasP99 = customDuration.stream()
                .anyMatch(e -> e.startsWith("p(99)"));
        if (httpReqDurationP95 > 0 && !customHasP95) {
            durationExpressions.add("p(95)<" + httpReqDurationP95);
        }
        if (httpReqDurationP99 > 0 && !customHasP99) {
            durationExpressions.add("p(99)<" + httpReqDurationP99);
        }
        durationExpressions.addAll(customDuration);
        if (!durationExpressions.isEmpty()) {
            sb.append("    http_req_duration: [");
            for (int i = 0; i < durationExpressions.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append("'").append(durationExpressions.get(i)).append("'");
            }
            sb.append("],\n");
        }

        // Additional custom thresholds (excluding http_req_duration already
        // handled above)
        for (Map.Entry<String, List<String>> entry : customThresholds
                .entrySet()) {
            if ("http_req_duration".equals(entry.getKey())) {
                continue;
            }
            sb.append("    ").append(entry.getKey()).append(": [");
            List<String> expressions = entry.getValue();
            for (int i = 0; i < expressions.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append("'").append(expressions.get(i)).append("'");
            }
            sb.append("],\n");
        }

        sb.append("  },\n");
        return sb.toString();
    }
}
