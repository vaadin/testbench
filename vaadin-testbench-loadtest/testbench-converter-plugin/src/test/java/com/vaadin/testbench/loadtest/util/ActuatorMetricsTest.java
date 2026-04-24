/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.testbench.loadtest.util.ActuatorMetrics.MetricsSummary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActuatorMetricsTest {

    private HttpServer server;
    private int port;
    private final ConcurrentHashMap<String, Handler> handlers = new ConcurrentHashMap<>();

    @FunctionalInterface
    interface Handler extends Function<String, String> {
    }

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getRawPath();
            String query = exchange.getRequestURI().getRawQuery();
            String key = query == null ? path : path + "?" + query;
            Handler handler = handlers.get(key);
            if (handler == null) {
                handler = handlers.get(path);
            }
            if (handler == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            String body = handler.apply(key);
            byte[] bytes = body.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();
        port = server.getAddress().getPort();
    }

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
        handlers.clear();
    }

    private void stub(String pathAndQuery, String body) {
        handlers.put(pathAndQuery, ignored -> body);
    }

    private ActuatorMetrics newActuator(boolean vaadin) {
        return new ActuatorMetrics("localhost", port, vaadin);
    }

    @Test
    void fetchMetrics_actuatorUnavailable_returnsEmpty() {
        // No /actuator/health handler registered → server returns 404 →
        // isActuatorAvailable returns false → fetchMetrics is empty.
        ActuatorMetrics actuator = newActuator(false);
        assertTrue(actuator.fetchMetrics().isEmpty());
    }

    @Test
    void fetchMetrics_malformedJson_doesNotFailFetch() {
        stub("/actuator/health", "{\"status\":\"UP\"}");
        // Malformed JSON for every metric endpoint is tolerated: each call
        // catches its parse error and returns null — the outer Optional is
        // still present.
        handlers.put("/actuator/metrics/process.cpu.usage",
                ignored -> "not-json");
        // All other /actuator/metrics/* endpoints → 404 which also yields
        // null metric values but not an empty Optional.

        ActuatorMetrics actuator = newActuator(false);
        Optional<MetricsSummary> result = actuator.fetchMetrics();

        // Either the health check succeeded and all values are null (present),
        // or the top-level catch swallowed a Jackson exception (empty). Both
        // are acceptable outcomes — what matters is no exception leaks out.
        if (result.isPresent()) {
            assertNull(result.get().processCpuPercent());
        }
    }

    @Test
    void fetchMetrics_emptyMeasurements_yieldsNullValues() {
        stub("/actuator/health", "{\"status\":\"UP\"}");
        stub("/actuator/metrics/process.cpu.usage",
                "{\"name\":\"x\",\"measurements\":[]}");

        ActuatorMetrics actuator = newActuator(false);
        Optional<MetricsSummary> result = actuator.fetchMetrics();

        assertTrue(result.isPresent());
        assertNull(result.get().processCpuPercent());
    }

    @Test
    void fetchMetrics_parsesMeasurements() {
        stub("/actuator/health", "{\"status\":\"UP\"}");
        stub("/actuator/metrics/process.cpu.usage", measurement(0.42));
        stub("/actuator/metrics/jvm.memory.used?tag=area:heap",
                measurement(1048576.0));

        ActuatorMetrics actuator = newActuator(false);
        Optional<MetricsSummary> result = actuator.fetchMetrics();

        assertTrue(result.isPresent());
        MetricsSummary s = result.get();
        assertNotNull(s.processCpuPercent());
        assertEquals(42.0, s.processCpuPercent(), 0.001);
        assertEquals(1048576L, s.heapUsedBytes());
        // Vaadin metrics disabled:
        assertNull(s.vaadinActiveUis());
        assertTrue(s.viewCounts().isEmpty());
    }

    @Test
    void fetchMetrics_withVaadinMetricsFlag_fetchesVaadinCount() {
        stub("/actuator/health", "{\"status\":\"UP\"}");
        stub("/actuator/metrics/vaadin.view.count",
                "{\"name\":\"vaadin.view.count\",\"measurements\":[{\"statistic\":\"VALUE\",\"value\":3}],\"availableTags\":[]}");

        ActuatorMetrics actuator = newActuator(true);
        Optional<MetricsSummary> result = actuator.fetchMetrics();

        assertTrue(result.isPresent());
        assertEquals(3L, result.get().vaadinActiveUis());
    }

    @Test
    void metricsSummary_formatBytes_handlesScales() {
        MetricsSummary summary = new MetricsSummary(null, null, null, null,
                null, null, null, java.util.Map.of());
        assertEquals("N/A", summary.formatBytes(null));
        assertEquals("512 B", summary.formatBytes(512L));
        assertTrue(summary.formatBytes(2048L).endsWith("KB"));
        assertTrue(summary.formatBytes(5L * 1024 * 1024).endsWith("MB"));
        assertTrue(summary.formatBytes(2L * 1024 * 1024 * 1024).endsWith("GB"));
    }

    @Test
    void metricsSummary_heapUsagePercent() {
        MetricsSummary summary = new MetricsSummary(null, null, 50L, 200L, null,
                null, null, java.util.Map.of());
        // Locale-tolerant — both "25.0%" (en) and "25,0%" (de/fi) are valid.
        String percent = summary.heapUsagePercent();
        assertTrue(percent.matches("25[.,]0%"),
                "Unexpected heap percent: " + percent);

        MetricsSummary empty = new MetricsSummary(null, null, null, null, null,
                null, null, java.util.Map.of());
        assertEquals("N/A", empty.heapUsagePercent());
    }

    @Test
    void metricsSummary_toString_includesPresentValues() {
        MetricsSummary summary = new MetricsSummary(45.0, 55.0, 1024L, 2048L,
                512L, 3L, 2L, java.util.Map.of("MainView", 1L));
        String text = summary.toString();
        assertTrue(text.contains("Process CPU"));
        assertTrue(text.contains("Heap Used"));
        assertTrue(text.contains("HTTP Sessions"));
        assertTrue(text.contains("Vaadin UIs"));
        assertTrue(text.contains("MainView"));
        assertFalse(text.contains("N/A"));
    }

    private static String measurement(double value) {
        return "{\"name\":\"x\",\"measurements\":[{\"statistic\":\"VALUE\",\"value\":"
                + value + "}]}";
    }
}
