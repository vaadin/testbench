/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultHandlerTest {

    private static final String BASE_SUMMARY = "{\n  \"metrics\": {}\n}";

    @Test
    void missingCsvLeavesSummaryUntouched(@TempDir Path dir)
            throws IOException {
        Path summary = writeSummary(dir, BASE_SUMMARY);
        Path csv = dir.resolve("does-not-exist.csv");

        ResultHandler.injectCsvMetrics(csv, summary);

        assertEquals(BASE_SUMMARY, Files.readString(summary));
    }

    @Test
    void csvWithoutKnownMetricsLeavesSummaryUntouched(@TempDir Path dir)
            throws IOException {
        Path summary = writeSummary(dir, BASE_SUMMARY);
        Path csv = writeCsv(dir, """
                metric_name,timestamp,metric_value
                iteration_duration,1000,12.3
                data_sent,1000,456
                """);

        ResultHandler.injectCsvMetrics(csv, summary);

        assertEquals(BASE_SUMMARY, Files.readString(summary));
    }

    @Test
    void vuSamplesProduceVuTimeline(@TempDir Path dir) throws IOException {
        Path summary = writeSummary(dir, BASE_SUMMARY);
        Path csv = writeCsv(dir, """
                metric_name,timestamp,metric_value
                vus,1000,5
                vus,1002,10
                vus,1005,7
                """);

        ResultHandler.injectCsvMetrics(csv, summary);

        JsonNode root = parse(summary);
        JsonNode timeline = root.get("vu_timeline");
        assertNotNull(timeline, "vu_timeline should be injected");
        assertEquals(3, timeline.size());
        assertEquals(0, timeline.get(0).get("elapsed").asInt());
        assertEquals(5, timeline.get(0).get("vus").asInt());
        assertEquals(2, timeline.get(1).get("elapsed").asInt());
        assertEquals(10, timeline.get(1).get("vus").asInt());
        assertEquals(5, timeline.get(2).get("elapsed").asInt());
        assertEquals(7, timeline.get(2).get("vus").asInt());
        assertTrue(root.get("request_timeline") == null
                || root.get("request_timeline").isMissingNode());
    }

    @Test
    void durationSamplesProduceRequestTimelineWithAvgAndMax(@TempDir Path dir)
            throws IOException {
        Path summary = writeSummary(dir, BASE_SUMMARY);
        // Two samples in the same bucket (elapsed=0): avg=150.4, max=200.3
        // One sample in bucket elapsed=2: single value is both avg and max
        Path csv = writeCsv(dir, """
                metric_name,timestamp,metric_value
                http_req_duration,1000,100.5
                http_req_duration,1000,200.3
                http_req_duration,1002,42.0
                """);

        ResultHandler.injectCsvMetrics(csv, summary);

        JsonNode timeline = parse(summary).get("request_timeline");
        assertNotNull(timeline);
        assertEquals(2, timeline.size());

        JsonNode first = timeline.get(0);
        assertEquals(0, first.get("elapsed").asInt());
        assertEquals(2, first.get("count").asInt());
        assertEquals(0, first.get("failed").asInt());
        assertEquals(150.4, first.get("avg").asDouble(), 0.05);
        assertEquals(200.3, first.get("max").asDouble(), 0.05);

        JsonNode second = timeline.get(1);
        assertEquals(2, second.get("elapsed").asInt());
        assertEquals(1, second.get("count").asInt());
        assertEquals(42.0, second.get("avg").asDouble(), 0.05);
        assertEquals(42.0, second.get("max").asDouble(), 0.05);
    }

    @Test
    void failedFlagIncrementsFailedCountInMatchingBucket(@TempDir Path dir)
            throws IOException {
        Path summary = writeSummary(dir, BASE_SUMMARY);
        // Two requests in the same bucket, one marked failed. The failed flag
        // only counts when value > 0; the zero-value row must not contribute.
        Path csv = writeCsv(dir, """
                metric_name,timestamp,metric_value
                http_req_duration,1000,100.0
                http_req_duration,1000,100.0
                http_req_failed,1000,1
                http_req_failed,1000,0
                """);

        ResultHandler.injectCsvMetrics(csv, summary);

        JsonNode bucket = parse(summary).get("request_timeline").get(0);
        assertEquals(2, bucket.get("count").asInt());
        assertEquals(1, bucket.get("failed").asInt());
    }

    @Test
    void failedOnlyBucketWithoutDurationIsDropped(@TempDir Path dir)
            throws IOException {
        // A bucket that has a failed flag but no http_req_duration sample has
        // count=0, which the writer skips — so it must not appear in the
        // timeline.
        Path summary = writeSummary(dir, BASE_SUMMARY);
        Path csv = writeCsv(dir, """
                metric_name,timestamp,metric_value
                http_req_duration,1000,50.0
                http_req_failed,1002,1
                """);

        ResultHandler.injectCsvMetrics(csv, summary);

        JsonNode timeline = parse(summary).get("request_timeline");
        assertEquals(1, timeline.size());
        assertEquals(0, timeline.get(0).get("elapsed").asInt());
    }

    @Test
    void injectedSummaryPreservesOriginalFields(@TempDir Path dir)
            throws IOException {
        Path summary = writeSummary(dir,
                "{\n  \"metrics\": {\"http_reqs\": {\"values\": {\"count\": 3}}}\n}");
        Path csv = writeCsv(dir, """
                metric_name,timestamp,metric_value
                vus,1000,1
                """);

        ResultHandler.injectCsvMetrics(csv, summary);

        JsonNode root = parse(summary);
        assertEquals(3, root.get("metrics").get("http_reqs").get("values")
                .get("count").asInt());
        assertNotNull(root.get("vu_timeline"));
    }

    @Test
    void malformedCsvRowDoesNotCorruptSummary(@TempDir Path dir)
            throws IOException {
        // A non-numeric timestamp triggers NumberFormatException, which the
        // handler catches and logs. The summary must remain valid JSON even
        // though partial state was accumulated in memory.
        Path summary = writeSummary(dir, BASE_SUMMARY);
        Path csv = writeCsv(dir, """
                metric_name,timestamp,metric_value
                vus,not-a-number,5
                """);

        ResultHandler.injectCsvMetrics(csv, summary);

        // Summary still parses as valid JSON.
        assertNotNull(parse(summary).get("metrics"));
    }

    private static Path writeSummary(Path dir, String json) throws IOException {
        Path summary = dir.resolve("summary.json");
        Files.writeString(summary, json);
        return summary;
    }

    private static Path writeCsv(Path dir, String contents) throws IOException {
        Path csv = dir.resolve("metrics.csv");
        Files.writeString(csv, contents);
        return csv;
    }

    private static JsonNode parse(Path file) throws IOException {
        return new ObjectMapper().readTree(Files.readString(file));
    }
}
