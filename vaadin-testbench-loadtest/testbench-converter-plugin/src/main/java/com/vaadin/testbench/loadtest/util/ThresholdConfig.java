/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fluent configuration for k6 test thresholds. Controls when the load test is
 * considered failed based on response times and check pass rates.
 * <p>
 * Construct with {@code new ThresholdConfig()} and chain {@code withXyz(...)}
 * calls; each mutates this instance and returns it for chaining. Default
 * thresholds include {@code http_req_duration} p95/p99 and a
 * {@code checks rate>=0.99} threshold (allowing up to 1% failed checks).
 * Defaults can be overridden via the wither methods or disabled by passing 0.
 *
 * <pre>
 * ThresholdConfig config = new ThresholdConfig().withHttpReqDurationP95(1500)
 *         .withHttpReqDurationP99(4000).withChecksAllowedFailureRate(0.02);
 * </pre>
 */
public final class ThresholdConfig {

    /**
     * Default tolerated rate of failed checks before the test fails.
     */
    public static final double DEFAULT_CHECKS_ALLOWED_FAILURE_RATE = 0.01;

    private int httpReqDurationP95 = 2000;
    private int httpReqDurationP99 = 5000;
    private boolean checksAbortOnFail = true;
    private double checksAllowedFailureRate = DEFAULT_CHECKS_ALLOWED_FAILURE_RATE;
    private Map<String, List<String>> customThresholds = new LinkedHashMap<>();

    /**
     * Creates a new {@link ThresholdConfig} populated with the default values.
     * Use the {@code withXyz} methods to derive customised configurations.
     */
    public ThresholdConfig() {
    }

    /**
     * Sets the 95th percentile {@code http_req_duration} threshold (in ms) and
     * returns this instance for chaining. Set to {@code 0} to disable the
     * default p95 threshold.
     */
    public ThresholdConfig withHttpReqDurationP95(int httpReqDurationP95) {
        this.httpReqDurationP95 = httpReqDurationP95;
        return this;
    }

    /**
     * Sets the 99th percentile {@code http_req_duration} threshold (in ms) and
     * returns this instance for chaining. Set to {@code 0} to disable the
     * default p99 threshold.
     */
    public ThresholdConfig withHttpReqDurationP99(int httpReqDurationP99) {
        this.httpReqDurationP99 = httpReqDurationP99;
        return this;
    }

    /**
     * Sets the abort-on-fail flag and returns this instance for chaining. When
     * {@code true} (default), the test aborts immediately when the checks
     * threshold is breached.
     */
    public ThresholdConfig withChecksAbortOnFail(boolean checksAbortOnFail) {
        this.checksAbortOnFail = checksAbortOnFail;
        return this;
    }

    /**
     * Sets the allowed check-failure rate and returns this instance for
     * chaining. The value is the fraction of check failures tolerated before
     * the test is considered failed (e.g. {@code 0.01} = up to 1% of checks may
     * fail). Must be in {@code [0, 1)}; {@code 0} requires all checks to pass.
     *
     * @throws IllegalArgumentException
     *             if the rate is outside {@code [0, 1)}
     */
    public ThresholdConfig withChecksAllowedFailureRate(
            double checksAllowedFailureRate) {
        if (checksAllowedFailureRate >= 1 || checksAllowedFailureRate < 0) {
            throw new IllegalArgumentException(
                    "Given failure rate ouside accepted range [0, 1)");
        }
        this.checksAllowedFailureRate = checksAllowedFailureRate;
        return this;
    }

    /**
     * Adds a custom threshold expression for the given k6 metric and returns
     * this instance for chaining. Multiple expressions can be added for the
     * same metric by calling this method repeatedly.
     * <p>
     * Example usage:
     *
     * <pre>
     * ThresholdConfig config = new ThresholdConfig()
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
     * @return this instance for chaining
     */
    public ThresholdConfig withCustomThreshold(String metric,
            String expression) {
        this.customThresholds.computeIfAbsent(metric, k -> new ArrayList<>())
                .add(expression);
        return this;
    }

    /**
     * Parses a custom thresholds string and applies each entry to this config.
     * Format: {@code metric:expression,metric:expression,...}
     *
     * @param thresholds
     *            the custom thresholds string to parse
     * @return this instance for chaining
     * @throws IllegalArgumentException
     *             if the format is invalid
     */
    public ThresholdConfig withCustomThresholds(String thresholds) {
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
            withCustomThreshold(metric, expression);
        }
        return this;
    }

    /**
     * Generates the k6 thresholds block for use inside
     * {@code export const options}. Example output:
     *
     * <pre>
     *   thresholds: {
     *     checks: [{ threshold: 'rate>=0.99', abortOnFail: true, delayAbortEval: '5s' }],
     *     http_req_duration: ['p(95)&lt;2000', 'p(99)&lt;5000'],
     *     http_req_failed: ['rate&lt;0.01'],
     *   },
     * </pre>
     *
     * @return the k6 thresholds block as a string
     */
    public String toK6ThresholdsBlock() {
        return toK6ThresholdsBlock(List.of());
    }

    /**
     * Generates the k6 thresholds block including per-request sub-metric
     * thresholds. Adding a threshold for a tagged sub-metric forces k6 to
     * include it in {@code handleSummary(data)}, enabling per-request
     * statistics in the JSON export.
     *
     * @param requestTags
     *            list of request tag names (e.g., "1 GET init"); a no-op
     *            {@code max>=0} threshold is added for each to enable tracking
     * @return the k6 thresholds block as a string
     */
    public String toK6ThresholdsBlock(List<String> requestTags) {
        StringBuilder sb = new StringBuilder();
        sb.append("  thresholds: {\n");

        // checks threshold
        String checksExpression = buildChecksThresholdExpression();
        if (checksAbortOnFail) {
            sb.append("    checks: [{ threshold: '").append(checksExpression)
                    .append("', abortOnFail: true, delayAbortEval: '5s' }],\n");
        } else {
            sb.append("    checks: ['").append(checksExpression)
                    .append("'],\n");
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

        // Per-request sub-metric thresholds: a no-op threshold (max>=0)
        // forces k6 to include tagged sub-metrics in handleSummary(data)
        if (requestTags != null) {
            for (String tag : requestTags) {
                String escaped = tag.replace("'", "\\'");
                sb.append("    'http_req_duration{name:").append(escaped)
                        .append("}': ['max>=0'],\n");
            }
        }

        sb.append("  },\n");
        return sb.toString();
    }

    /**
     * Generates a k6 {@code --summary-trend-stats} value that includes all
     * percentiles referenced by the configured thresholds. Always includes
     * {@code avg, min, med, max} plus any {@code p(N)} found in the default
     * p95/p99 settings and custom threshold expressions.
     *
     * @return comma-separated stats string for the k6 CLI flag
     */
    public String toSummaryTrendStats() {
        Set<String> percentiles = new LinkedHashSet<>();
        if (httpReqDurationP95 > 0) {
            percentiles.add("p(95)");
        }
        if (httpReqDurationP99 > 0) {
            percentiles.add("p(99)");
        }
        // Extract p(N) references from custom threshold expressions
        Pattern pPattern = Pattern.compile("p\\(\\d+(?:\\.\\d+)?\\)");
        for (List<String> expressions : customThresholds.values()) {
            for (String expr : expressions) {
                Matcher m = pPattern.matcher(expr);
                while (m.find()) {
                    percentiles.add(m.group());
                }
            }
        }
        StringBuilder sb = new StringBuilder("avg,min,med,max");
        for (String p : percentiles) {
            sb.append(",").append(p);
        }
        return sb.toString();
    }

    /**
     * Builds the k6 threshold expression for the {@code checks} metric based on
     * the configured allowed failure rate. Returns {@code rate==1} when zero
     * failures are tolerated, otherwise {@code rate>=X} where X is the minimum
     * required pass rate.
     */
    private String buildChecksThresholdExpression() {
        if (checksAllowedFailureRate <= 0) {
            return "rate==1";
        }
        String passRate = BigDecimal.valueOf(1.0 - checksAllowedFailureRate)
                .setScale(4, RoundingMode.HALF_UP).stripTrailingZeros()
                .toPlainString();
        return "rate>=" + passRate;
    }
}
