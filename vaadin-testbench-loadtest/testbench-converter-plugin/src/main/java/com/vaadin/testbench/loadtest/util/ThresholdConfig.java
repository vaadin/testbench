package com.vaadin.testbench.loadtest.util;

/**
 * Configuration for k6 test thresholds.
 * Controls when the load test is considered failed based on response times
 * and check pass rates.
 *
 * @param httpReqDurationP95 95th percentile response time threshold in ms (0 to disable)
 * @param httpReqDurationP99 99th percentile response time threshold in ms (0 to disable)
 * @param checksAbortOnFail  if true, abort the test immediately when a check fails
 */
public record ThresholdConfig(
        int httpReqDurationP95,
        int httpReqDurationP99,
        boolean checksAbortOnFail
) {
    /** Default thresholds: p95 < 2000ms, p99 < 5000ms, abort on check failure. */
    public static final ThresholdConfig DEFAULT = new ThresholdConfig(2000, 5000, true);

    /**
     * Generates the k6 thresholds block for use inside {@code export const options}.
     * Example output:
     * <pre>
     *   thresholds: {
     *     checks: [{ threshold: 'rate==1', abortOnFail: true, delayAbortEval: '5s' }],
     *     http_req_duration: ['p(95)&lt;2000', 'p(99)&lt;5000'],
     *   },
     * </pre>
     */
    public String toK6ThresholdsBlock() {
        StringBuilder sb = new StringBuilder();
        sb.append("  thresholds: {\n");
        if (checksAbortOnFail) {
            sb.append("    checks: [{ threshold: 'rate==1', abortOnFail: true, delayAbortEval: '5s' }],\n");
        } else {
            sb.append("    checks: ['rate==1'],\n");
        }
        sb.append("    http_req_duration: [");
        boolean first = true;
        if (httpReqDurationP95 > 0) {
            sb.append("'p(95)<").append(httpReqDurationP95).append("'");
            first = false;
        }
        if (httpReqDurationP99 > 0) {
            if (!first) sb.append(", ");
            sb.append("'p(99)<").append(httpReqDurationP99).append("'");
        }
        sb.append("],\n");
        sb.append("  },\n");
        return sb.toString();
    }
}
