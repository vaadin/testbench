package com.vaadin.testbench.loadtest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Fetches metrics from Spring Boot Actuator endpoints.
 * <p>
 * <b>Requirements:</b> This utility requires the target application to have
 * Spring Boot Actuator configured with the metrics endpoint exposed:
 * <pre>
 * # In application.properties:
 * management.server.port=8082
 * management.endpoints.web.exposure.include=health,metrics
 * </pre>
 * <p>
 * Add the actuator dependency to your Spring Boot application:
 * <pre>
 * &lt;dependency&gt;
 *     &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
 *     &lt;artifactId&gt;spring-boot-starter-actuator&lt;/artifactId&gt;
 * &lt;/dependency&gt;
 * </pre>
 * <p>
 * <b>Vaadin Metrics:</b> For Vaadin-specific metrics collection, the target application
 * must implement custom VaadinActuator or equivalent that exposes Vaadin view metrics.
 * This is not part of standard Spring Boot Actuator and must be implemented by the user.
 *
 * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html">Spring Boot Actuator Endpoints</a>
 */
public class ActuatorMetrics {

    private static final Logger log = Logger.getLogger(ActuatorMetrics.class.getName());
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final boolean collectVaadinMetrics;

    /**
     * Creates an ActuatorMetrics instance.
     *
     * @param host                actuator host (e.g., "localhost")
     * @param managementPort      actuator management port (e.g., 8082)
     * @param collectVaadinMetrics whether to collect Vaadin-specific metrics (requires custom VaadinActuator implementation)
     */
    public ActuatorMetrics(String host, int managementPort, boolean collectVaadinMetrics) {
        this.baseUrl = "http://" + host + ":" + managementPort + "/actuator";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
        this.collectVaadinMetrics = collectVaadinMetrics;
    }

    /**
     * Creates an ActuatorMetrics instance (backward compatibility constructor).
     * Vaadin metrics collection is disabled by default.
     *
     * @param host            actuator host (e.g., "localhost")
     * @param managementPort  actuator management port (e.g., 8082)
     */
    public ActuatorMetrics(String host, int managementPort) {
        this(host, managementPort, false);
    }

    /**
     * Fetches and returns a summary of server metrics after a load test.
     *
     * @return metrics summary, or empty if actuator is not available
     */
    public Optional<MetricsSummary> fetchMetrics() {
        try {
            // Check if actuator is available
            if (!isActuatorAvailable()) {
                log.fine("Actuator endpoint not available at " + baseUrl);
                return Optional.empty();
            }

            Double cpuUsage = fetchMetricValue("process.cpu.usage");
            Double systemCpuUsage = fetchMetricValue("system.cpu.usage");
            Long heapUsed = fetchMetricValueAsLong("jvm.memory.used", "area", "heap");
            Long heapMax = fetchMetricValueAsLong("jvm.memory.max", "area", "heap");
            Long nonHeapUsed = fetchMetricValueAsLong("jvm.memory.used", "area", "nonheap");
            Long activeSessions = fetchMetricValueAsLong("tomcat.sessions.active.current");

            // Fetch Vaadin-specific metrics if enabled
            Long vaadinActiveUis = null;
            Map<String, Long> viewCounts = new LinkedHashMap<>();
            if (collectVaadinMetrics) {
                vaadinActiveUis = fetchMetricValueAsLong("vaadin.view.count");
                viewCounts = fetchViewCounts();
            }

            return Optional.of(new MetricsSummary(
                    cpuUsage != null ? cpuUsage * 100 : null,
                    systemCpuUsage != null ? systemCpuUsage * 100 : null,
                    heapUsed,
                    heapMax,
                    nonHeapUsed,
                    activeSessions,
                    vaadinActiveUis,
                    viewCounts
            ));

        } catch (Exception e) {
            log.fine("Failed to fetch actuator metrics: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Checks if the actuator health endpoint is available.
     */
    private boolean isActuatorAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/health"))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fetches a single metric value.
     */
    private Double fetchMetricValue(String metricName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/metrics/" + metricName))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode measurements = root.get("measurements");
                if (measurements != null && measurements.isArray() && !measurements.isEmpty()) {
                    return measurements.get(0).get("value").asDouble();
                }
            }
        } catch (IOException | InterruptedException e) {
            log.fine("Failed to fetch metric " + metricName + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Fetches a metric value as Long without tag filter.
     */
    private Long fetchMetricValueAsLong(String metricName) {
        Double value = fetchMetricValue(metricName);
        return value != null ? value.longValue() : null;
    }

    /**
     * Fetches a metric value with a specific tag filter.
     */
    private Long fetchMetricValueAsLong(String metricName, String tagName, String tagValue) {
        try {
            String url = baseUrl + "/metrics/" + metricName + "?tag=" + tagName + ":" + tagValue;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode measurements = root.get("measurements");
                if (measurements != null && measurements.isArray() && !measurements.isEmpty()) {
                    return measurements.get(0).get("value").asLong();
                }
            }
        } catch (IOException | InterruptedException e) {
            log.fine("Failed to fetch metric " + metricName + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Fetches view counts for all available views.
     * Returns a map of view name (simple class name) to count.
     */
    private Map<String, Long> fetchViewCounts() {
        Map<String, Long> viewCounts = new LinkedHashMap<>();
        try {
            // First, get available tags for vaadin.view.count
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/metrics/vaadin.view.count"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode availableTags = root.get("availableTags");
                if (availableTags != null && availableTags.isArray()) {
                    for (JsonNode tagNode : availableTags) {
                        if ("view".equals(tagNode.get("tag").asText())) {
                            JsonNode values = tagNode.get("values");
                            if (values != null && values.isArray()) {
                                for (JsonNode viewName : values) {
                                    String name = viewName.asText();
                                    Long count = fetchMetricValueAsLong("vaadin.view.count", "view", name);
                                    if (count != null) {
                                        viewCounts.put(name, count);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            log.fine("Failed to fetch view counts: " + e.getMessage());
        }
        return viewCounts;
    }

    /**
     * Summary of server metrics.
     *
     * @param processCpuPercent  process CPU usage percentage (0-100)
     * @param systemCpuPercent   system CPU usage percentage (0-100)
     * @param heapUsedBytes      heap memory used in bytes
     * @param heapMaxBytes       heap memory max in bytes
     * @param nonHeapUsedBytes   non-heap memory used in bytes
     * @param activeSessions     number of active HTTP sessions
     * @param vaadinActiveUis    number of active Vaadin UI instances (requires custom VaadinActuator implementation)
     * @param viewCounts         map of view name to active count (requires custom VaadinActuator implementation)
     */
    public record MetricsSummary(
            Double processCpuPercent,
            Double systemCpuPercent,
            Long heapUsedBytes,
            Long heapMaxBytes,
            Long nonHeapUsedBytes,
            Long activeSessions,
            Long vaadinActiveUis,
            Map<String, Long> viewCounts
    ) {
        /**
         * Formats bytes to human-readable format.
         */
        public String formatBytes(Long bytes) {
            if (bytes == null) return "N/A";
            String sign = bytes < 0 ? "-" : "";
            long abs = Math.abs(bytes);
            if (abs < 1024) return sign + abs + " B";
            if (abs < 1024 * 1024) return String.format("%s%.1f KB", sign, abs / 1024.0);
            if (abs < 1024 * 1024 * 1024) return String.format("%s%.1f MB", sign, abs / (1024.0 * 1024));
            return String.format("%s%.2f GB", sign, abs / (1024.0 * 1024 * 1024));
        }

        /**
         * Returns heap usage as a percentage.
         */
        public String heapUsagePercent() {
            if (heapUsedBytes == null || heapMaxBytes == null || heapMaxBytes == 0) {
                return "N/A";
            }
            return String.format("%.1f%%", (heapUsedBytes * 100.0) / heapMaxBytes);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Server Metrics (via Spring Boot Actuator):\n");
            if (processCpuPercent != null) {
                sb.append(String.format("  Process CPU: %.1f%%\n", processCpuPercent));
            }
            if (systemCpuPercent != null) {
                sb.append(String.format("  System CPU:  %.1f%%\n", systemCpuPercent));
            }
            if (heapUsedBytes != null) {
                sb.append(String.format("  Heap Used:   %s / %s (%s)\n",
                        formatBytes(heapUsedBytes),
                        formatBytes(heapMaxBytes),
                        heapUsagePercent()));
            }
            if (nonHeapUsedBytes != null) {
                sb.append(String.format("  Non-Heap:    %s\n", formatBytes(nonHeapUsedBytes)));
            }
            if (activeSessions != null) {
                sb.append(String.format("  HTTP Sessions: %d\n", activeSessions));
            }
            if (vaadinActiveUis != null) {
                sb.append(String.format("  Vaadin UIs:   %d\n", vaadinActiveUis));
            }
            if (viewCounts != null && !viewCounts.isEmpty()) {
                sb.append("  Views:\n");
                for (Map.Entry<String, Long> entry : viewCounts.entrySet()) {
                    sb.append(String.format("    %s: %d\n", entry.getKey(), entry.getValue()));
                }
            }
            return sb.toString();
        }
    }
}
