package com.vaadin.testbench.loadtest.util;

import com.vaadin.testbench.loadtest.util.ActuatorMetrics.MetricsSummary;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Collects server metrics periodically during a load test.
 * <p>
 * This collector runs in a background thread, fetching metrics from Spring Boot
 * Actuator at regular intervals. After the test completes, the collected snapshots
 * can be displayed as a time-series table showing how server metrics changed
 * under load.
 * <p>
 * Usage:
 * <pre>
 * MetricsCollector collector = new MetricsCollector(log, actuator, 10);
 * collector.start();
 * // ... run load test ...
 * collector.stop();
 * collector.printReport();
 * </pre>
 */
public class MetricsCollector implements Runnable {

    private static final Logger log = Logger.getLogger(MetricsCollector.class.getName());
    private final ActuatorMetrics actuator;
    private final int intervalSeconds;
    private final List<TimestampedMetrics> snapshots = Collections.synchronizedList(new ArrayList<>());
    private final Instant startTime;
    private volatile boolean running = true;
    private Thread collectorThread;

    /**
     * Creates a new metrics collector.
     *
     * @param actuator        actuator metrics client
     * @param intervalSeconds interval between metric snapshots in seconds
     */
    public MetricsCollector(ActuatorMetrics actuator, int intervalSeconds) {
        this.actuator = actuator;
        this.intervalSeconds = intervalSeconds;
        this.startTime = Instant.now();
    }

    /**
     * Collects a baseline snapshot synchronously before load test starts.
     * Call this before starting k6 to capture pre-test server state.
     */
    public void collectBaseline() {
        collectSnapshot();
        log.fine("Baseline metrics collected");
    }

    /**
     * Starts the background collection thread.
     */
    public void start() {
        collectorThread = new Thread(this, "metrics-collector");
        collectorThread.setDaemon(true);
        collectorThread.start();
        log.fine("Metrics collector started (interval: " + intervalSeconds + "s)");
    }

    /**
     * Stops the collection thread and waits for it to finish.
     */
    public void stop() {
        running = false;
        if (collectorThread != null) {
            collectorThread.interrupt();
            try {
                collectorThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.fine("Metrics collector stopped (" + snapshots.size() + " snapshots collected)");
    }

    @Override
    public void run() {
        // Note: baseline is collected separately via collectBaseline()
        // Wait for the first interval before collecting again
        while (running) {
            try {
                Thread.sleep(intervalSeconds * 1000L);
                if (running) {
                    collectSnapshot();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Collects a single metrics snapshot.
     */
    private void collectSnapshot() {
        try {
            actuator.fetchMetrics().ifPresent(metrics -> {
                Duration elapsed = Duration.between(startTime, Instant.now());
                snapshots.add(new TimestampedMetrics(elapsed, metrics));
            });
        } catch (Exception e) {
            log.fine("Failed to collect metrics snapshot: " + e.getMessage());
        }
    }

    /**
     * Returns the collected snapshots.
     */
    public List<TimestampedMetrics> getSnapshots() {
        return new ArrayList<>(snapshots);
    }

    /**
     * Prints a formatted report of collected metrics to stdout.
     * Uses System.out directly for clean table formatting.
     */
    public void printReport() {
        if (snapshots.isEmpty()) {
            System.out.println("No metrics collected (actuator may not be available)");
            return;
        }

        System.out.println();
        System.out.println("Server Metrics Over Time (via Spring Boot Actuator):");

        // Check if we have Vaadin metrics and collect unique view names
        boolean hasVaadinMetrics = snapshots.stream()
                .anyMatch(s -> s.metrics().vaadinActiveUis() != null);

        // Collect all unique view names (sorted for consistent column order)
        List<String> viewNames = snapshots.stream()
                .filter(s -> s.metrics().viewCounts() != null)
                .flatMap(s -> s.metrics().viewCounts().keySet().stream())
                .distinct()
                .sorted()
                .toList();

        boolean hasViewMetrics = !viewNames.isEmpty();

        // Build dynamic table
        printTableHeader(hasVaadinMetrics, hasViewMetrics, viewNames);

        MetricsSummary first = snapshots.get(0).metrics();
        MetricsSummary last = null;

        for (TimestampedMetrics snapshot : snapshots) {
            MetricsSummary m = snapshot.metrics();
            last = m;
            printTableRow(m, snapshot.elapsed(), hasVaadinMetrics, hasViewMetrics, viewNames);
        }

        printTableFooter(hasVaadinMetrics, hasViewMetrics, viewNames);

        // Print summary with deltas
        if (last != null && snapshots.size() > 1) {
            printSummary(first, last, viewNames);
        }
    }

    /**
     * Prints the table header.
     */
    private void printTableHeader(boolean hasVaadinMetrics, boolean hasViewMetrics, List<String> viewNames) {
        StringBuilder topBorder = new StringBuilder("+----------+----------+------------+------------+-----------+----------");
        StringBuilder header = new StringBuilder("| Time     | CPU (%)  | Heap Used  | Heap Max   | Non-Heap  | Sessions ");
        StringBuilder separator = new StringBuilder("+----------+----------+------------+------------+-----------+----------");

        if (hasVaadinMetrics) {
            topBorder.append("+----------");
            header.append("| UIs      ");
            separator.append("+----------");
        }

        if (hasViewMetrics) {
            for (String viewName : viewNames) {
                // Truncate view name to fit column (max 10 chars)
                String truncated = viewName.length() > 10 ? viewName.substring(0, 10) : viewName;
                topBorder.append("+----------");
                header.append(String.format("| %-8s ", truncated));
                separator.append("+----------");
            }
        }

        topBorder.append("+");
        header.append("|");
        separator.append("+");

        System.out.println(topBorder);
        System.out.println(header);
        System.out.println(separator);
    }

    /**
     * Prints a single table row.
     */
    private void printTableRow(MetricsSummary m, Duration elapsed, boolean hasVaadinMetrics,
                               boolean hasViewMetrics, List<String> viewNames) {
        String time = formatDuration(elapsed);
        String cpu = m.processCpuPercent() != null ? String.format(Locale.US, "%5.1f%%", m.processCpuPercent()) : "   N/A";
        String heapUsed = padLeft(m.formatBytes(m.heapUsedBytes()), 10);
        String heapMax = padLeft(m.formatBytes(m.heapMaxBytes()), 10);
        String nonHeap = padLeft(m.formatBytes(m.nonHeapUsedBytes()), 9);
        String sessions = m.activeSessions() != null ? String.format("%8d", m.activeSessions()) : "     N/A";

        StringBuilder row = new StringBuilder();
        row.append(String.format("| %-8s | %7s  | %s | %s | %s | %s ", time, cpu, heapUsed, heapMax, nonHeap, sessions));

        if (hasVaadinMetrics) {
            String vaadinUis = m.vaadinActiveUis() != null ? String.format("%8d", m.vaadinActiveUis()) : "     N/A";
            row.append(String.format("| %s ", vaadinUis));
        }

        if (hasViewMetrics) {
            for (String viewName : viewNames) {
                Long count = m.viewCounts() != null ? m.viewCounts().get(viewName) : null;
                String viewCount = count != null ? String.format("%8d", count) : "     N/A";
                row.append(String.format("| %s ", viewCount));
            }
        }

        row.append("|");
        System.out.println(row);
    }

    /**
     * Prints the table footer.
     */
    private void printTableFooter(boolean hasVaadinMetrics, boolean hasViewMetrics, List<String> viewNames) {
        StringBuilder bottomBorder = new StringBuilder("+----------+----------+------------+------------+-----------+----------");

        if (hasVaadinMetrics) {
            bottomBorder.append("+----------");
        }

        if (hasViewMetrics) {
            for (int i = 0; i < viewNames.size(); i++) {
                bottomBorder.append("+----------");
            }
        }

        bottomBorder.append("+");
        System.out.println(bottomBorder);
    }

    /**
     * Prints a summary comparing first and last snapshots.
     */
    private void printSummary(MetricsSummary first, MetricsSummary last, List<String> viewNames) {
        StringBuilder summary = new StringBuilder("Summary: ");
        List<String> changes = new ArrayList<>();

        // Heap delta
        if (first.heapUsedBytes() != null && last.heapUsedBytes() != null) {
            long delta = last.heapUsedBytes() - first.heapUsedBytes();
            String sign = delta >= 0 ? "+" : "";
            changes.add("Heap " + sign + first.formatBytes(delta));
        }

        // Sessions delta
        if (first.activeSessions() != null && last.activeSessions() != null) {
            long delta = last.activeSessions() - first.activeSessions();
            String sign = delta >= 0 ? "+" : "";
            changes.add("Sessions " + sign + delta);
        }

        // Vaadin UIs delta
        if (first.vaadinActiveUis() != null && last.vaadinActiveUis() != null) {
            long delta = last.vaadinActiveUis() - first.vaadinActiveUis();
            String sign = delta >= 0 ? "+" : "";
            changes.add("UIs " + sign + delta);
        }

        // Average CPU
        double avgCpu = snapshots.stream()
                .filter(s -> s.metrics().processCpuPercent() != null)
                .mapToDouble(s -> s.metrics().processCpuPercent())
                .average()
                .orElse(0);
        if (avgCpu > 0) {
            changes.add(String.format(Locale.US, "Avg CPU %.1f%%", avgCpu));
        }

        // Peak CPU
        double peakCpu = snapshots.stream()
                .filter(s -> s.metrics().processCpuPercent() != null)
                .mapToDouble(s -> s.metrics().processCpuPercent())
                .max()
                .orElse(0);
        if (peakCpu > 0) {
            changes.add(String.format(Locale.US, "Peak CPU %.1f%%", peakCpu));
        }

        summary.append(String.join(", ", changes));
        System.out.println(summary.toString());
    }

    /**
     * Formats a duration as MM:SS.
     */
    private String formatDuration(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Pads a string to the left to reach the specified width.
     */
    private String padLeft(String s, int width) {
        if (s.length() >= width) return s;
        return " ".repeat(width - s.length()) + s;
    }

    /**
     * A metrics snapshot with timestamp.
     *
     * @param elapsed time elapsed since collection started
     * @param metrics the metrics values at this point
     */
    public record TimestampedMetrics(Duration elapsed, MetricsSummary metrics) {}
}
