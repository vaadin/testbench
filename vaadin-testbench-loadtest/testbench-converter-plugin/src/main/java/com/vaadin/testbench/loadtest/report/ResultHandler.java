/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.report;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Post-processes k6 result files. Reads the raw CSV metrics stream produced by
 * k6 and enriches the summary JSON with timeline data used by the HTML report.
 */
public class ResultHandler {

    private static final Logger log = Logger
            .getLogger(ResultHandler.class.getName());

    private ResultHandler() {
        // Utility class
    }

    /**
     * Extracts runtime metrics from the k6 CSV output and injects them into the
     * summary JSON:
     * <ul>
     * <li>{@code vu_timeline} — VU count per second</li>
     * <li>{@code request_timeline} — per-second buckets with request count,
     * pass/fail counts, and avg/max response time</li>
     * </ul>
     *
     * <p>
     * The CSV file is k6's native CSV output (produced with
     * {@code --out csv=<file>}). It contains one row per metric sample with a
     * header row listing the column names. The columns consumed here are:
     * <ul>
     * <li>{@code metric_name} — the k6 metric identifier, e.g. {@code vus},
     * {@code http_req_duration}, {@code http_req_failed}</li>
     * <li>{@code timestamp} — Unix epoch seconds at which the sample was
     * taken; the first seen timestamp is treated as t=0 and all subsequent
     * samples are bucketed by elapsed seconds from that point</li>
     * <li>{@code metric_value} — the numeric sample value (VU count, request
     * duration in milliseconds, or {@code 0}/{@code 1} for the failed
     * flag)</li>
     * </ul>
     * Additional columns that k6 may emit (e.g. {@code check}, {@code error},
     * {@code method}, {@code name}, {@code status}, {@code url},
     * {@code scenario}, {@code group}, {@code extra_tags}) are ignored.
     *
     * <p>
     * Only three metrics are read: {@code vus} drives the VU timeline;
     * {@code http_req_duration} samples are aggregated per elapsed second into
     * count/sum/max for the request timeline; {@code http_req_failed} samples
     * with a non-zero value increment the failed count for the same bucket.
     * The file may be absent (k6 CSV output was not requested or the run
     * aborted early) — in that case the method logs and returns without
     * touching the summary.
     *
     * @param csvFile
     *            the k6 CSV metrics output file
     * @param summaryFile
     *            the summary JSON file to inject the data into
     */
    public static void injectCsvMetrics(Path csvFile, Path summaryFile) {
        if (!Files.exists(csvFile)) {
            log.fine("No CSV metrics file found: " + csvFile);
            return;
        }
        try {
            // k6 CSV header: metric_name,timestamp,metric_value,...
            StringBuilder vuJson = new StringBuilder("[");
            boolean hasVu = false;
            long firstTimestamp = -1;

            // Per-second request buckets: elapsed -> {count, failed,
            // sumDuration, maxDuration}
            var buckets = new TreeMap<Long, long[]>();

            try (BufferedReader reader = Files.newBufferedReader(csvFile)) {
                String line;
                int metricCol = -1, tsCol = -1, valueCol = -1;
                while ((line = reader.readLine()) != null) {
                    String[] cols = line.split(",");
                    if (metricCol < 0) {
                        for (int i = 0; i < cols.length; i++) {
                            switch (cols[i].trim()) {
                            case "metric_name" -> metricCol = i;
                            case "timestamp" -> tsCol = i;
                            case "metric_value" -> valueCol = i;
                            }
                        }
                        continue;
                    }
                    if (metricCol >= cols.length || tsCol >= cols.length
                            || valueCol >= cols.length) {
                        continue;
                    }
                    String metric = cols[metricCol].trim();
                    long ts = Long.parseLong(cols[tsCol].trim());
                    double value = Double.parseDouble(cols[valueCol].trim());
                    if (firstTimestamp < 0) {
                        firstTimestamp = ts;
                    }
                    long elapsed = ts - firstTimestamp;

                    if ("vus".equals(metric)) {
                        if (hasVu)
                            vuJson.append(",");
                        vuJson.append("{\"elapsed\":").append(elapsed)
                                .append(",\"vus\":").append((int) value)
                                .append("}");
                        hasVu = true;
                    } else if ("http_req_duration".equals(metric)) {
                        // [count, failed, sumDuration(x100 for precision),
                        // maxDuration(x100)]
                        long[] b = buckets.computeIfAbsent(elapsed,
                                k -> new long[4]);
                        b[0]++;
                        b[2] += (long) (value * 100);
                        b[3] = Math.max(b[3], (long) (value * 100));
                    } else if ("http_req_failed".equals(metric) && value > 0) {
                        long[] b = buckets.computeIfAbsent(elapsed,
                                k -> new long[4]);
                        b[1]++;
                    }
                }
            }
            vuJson.append("]");

            // Build request timeline JSON
            StringBuilder reqJson = new StringBuilder("[");
            boolean firstBucket = true;
            for (var entry : buckets.entrySet()) {
                long[] b = entry.getValue();
                if (b[0] == 0)
                    continue;
                if (!firstBucket)
                    reqJson.append(",");
                double avg = (b[2] / 100.0) / b[0];
                double max = b[3] / 100.0;
                reqJson.append("{\"elapsed\":").append(entry.getKey())
                        .append(",\"count\":").append(b[0])
                        .append(",\"failed\":").append(b[1]).append(",\"avg\":")
                        .append(String.format(java.util.Locale.US, "%.1f", avg))
                        .append(",\"max\":")
                        .append(String.format(java.util.Locale.US, "%.1f", max))
                        .append("}");
                firstBucket = false;
            }
            reqJson.append("]");

            if (!hasVu && firstBucket) {
                log.fine("No runtime data points found in CSV");
                return;
            }

            // Inject into summary JSON before the closing brace
            String json = Files.readString(summaryFile);
            int lastBrace = json.lastIndexOf('}');
            if (lastBrace > 0) {
                StringBuilder extra = new StringBuilder();
                if (hasVu) {
                    extra.append(",\n  \"vu_timeline\": ").append(vuJson);
                }
                if (!firstBucket) {
                    extra.append(",\n  \"request_timeline\": ").append(reqJson);
                }
                String injected = json.substring(0, lastBrace) + extra + "\n}";
                Files.writeString(summaryFile, injected);
                log.info("Runtime metrics injected into summary");
            }
        } catch (IOException | NumberFormatException e) {
            log.warning("Failed to extract runtime metrics from CSV: "
                    + e.getMessage());
        }
    }
}
