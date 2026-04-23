/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.vaadin.testbench.loadtest.util.ActuatorMetrics.MetricsSummary;
import com.vaadin.testbench.loadtest.util.MetricsCollector.TimestampedMetrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetricsCollectorTest {

    /**
     * Stub actuator that returns a canned response without real HTTP calls.
     * Extending the real class keeps the code path identical for
     * MetricsCollector.
     */
    static class StubActuator extends ActuatorMetrics {
        private final Optional<MetricsSummary> response;

        StubActuator(Optional<MetricsSummary> response) {
            super("localhost", 0);
            this.response = response;
        }

        @Override
        public Optional<MetricsSummary> fetchMetrics() {
            return response;
        }
    }

    private static MetricsSummary sampleSummary() {
        return new MetricsSummary(42.0, 45.0, 1024L * 1024L, 4L * 1024 * 1024,
                512L * 1024, 2L, 3L, new LinkedHashMap<>());
    }

    @Test
    void collectBaseline_recordsFirstSnapshot() {
        ActuatorMetrics actuator = new StubActuator(
                Optional.of(sampleSummary()));

        MetricsCollector collector = new MetricsCollector(actuator, 1);
        collector.collectBaseline();

        List<TimestampedMetrics> snapshots = collector.getSnapshots();
        assertEquals(1, snapshots.size());
        assertEquals(42.0, snapshots.get(0).metrics().processCpuPercent());
    }

    @Test
    void collectBaseline_actuatorEmpty_noSnapshotStored() {
        ActuatorMetrics actuator = new StubActuator(Optional.empty());

        MetricsCollector collector = new MetricsCollector(actuator, 1);
        collector.collectBaseline();

        assertTrue(collector.getSnapshots().isEmpty());
    }

    @Test
    void startStop_producesNonEmptyTimeline() throws InterruptedException {
        ActuatorMetrics actuator = new StubActuator(
                Optional.of(sampleSummary()));

        MetricsCollector collector = new MetricsCollector(actuator, 1);
        collector.collectBaseline();
        collector.start();
        Thread.sleep(1200);
        collector.stop();

        assertFalse(collector.getSnapshots().isEmpty());
    }

    @Test
    void printReport_emptySnapshots_printsNoMetricsMessage() {
        ActuatorMetrics actuator = new StubActuator(Optional.empty());
        MetricsCollector collector = new MetricsCollector(actuator, 1);

        String output = captureStdout(collector::printReport);

        assertTrue(output.contains("No metrics collected"));
    }

    @Test
    void printReport_withSnapshots_printsExpectedHeaders() {
        ActuatorMetrics actuator = new StubActuator(
                Optional.of(sampleSummary()));
        MetricsCollector collector = new MetricsCollector(actuator, 1);
        collector.collectBaseline();

        String output = captureStdout(collector::printReport);

        assertTrue(output.contains("Server Metrics (via Spring Boot Actuator)"),
                "Output: " + output);
        assertTrue(output.contains("Time"));
        assertTrue(output.contains("CPU %"));
        assertTrue(output.contains("Heap Used"));
        assertTrue(output.contains("Heap Max"));
        assertTrue(output.contains("Non-Heap"));
        assertTrue(output.contains("Sessions"));
        assertTrue(output.contains("UIs"));
    }

    @Test
    void printReport_summaryRowAppearsWhenMultipleSnapshots() {
        ActuatorMetrics actuator = new StubActuator(
                Optional.of(sampleSummary()));
        MetricsCollector collector = new MetricsCollector(actuator, 1);
        collector.collectBaseline();
        collector.collectBaseline();

        String output = captureStdout(collector::printReport);
        assertTrue(output.contains("Summary:"),
                "Expected summary line for multi-snapshot report:\n" + output);
    }

    private static String captureStdout(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString();
    }
}
