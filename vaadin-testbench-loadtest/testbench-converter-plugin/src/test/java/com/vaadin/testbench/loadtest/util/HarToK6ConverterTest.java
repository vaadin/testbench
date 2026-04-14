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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HarToK6ConverterTest {

    @TempDir
    Path tempDir;

    @Test
    void connectMethodIsSkipped() throws IOException {
        String har = createHar(entry("CONNECT", "http://localhost:8080"),
                entry("GET", "http://localhost:8080/"));

        Path harFile = tempDir.resolve("test.har");
        Path outputFile = tempDir.resolve("test.js");
        Files.writeString(harFile, har);

        new HarToK6Converter().convert(harFile, outputFile);

        String script = Files.readString(outputFile);
        assertFalse(script.contains("http.connect("),
                "Generated script should not contain http.connect()");
        assertTrue(script.contains("http.get("),
                "Generated script should contain http.get()");
    }

    @Test
    void deleteMethodUsesDelFunction() throws IOException {
        String har = createHar(entry("GET", "http://localhost:8080/"),
                entry("DELETE", "http://localhost:8080/api/1"));

        Path harFile = tempDir.resolve("test.har");
        Path outputFile = tempDir.resolve("test.js");
        Files.writeString(harFile, har);

        new HarToK6Converter().convert(harFile, outputFile);

        String script = Files.readString(outputFile);
        assertTrue(script.contains("http.del("),
                "Generated script should use http.del() for DELETE");
        assertFalse(script.contains("http.delete("),
                "Generated script should not contain http.delete()");
    }

    @Test
    void msyncValueWithCommaIsPreservedInCsv() throws IOException {
        String value = "Cronan's Guide to Nanomixology, 2nd ed.";
        Path csvFile = convertMsyncValue(value, "msync-comma");

        String csv = Files.readString(csvFile);
        List<List<String>> records = parseCsvRecords(csv);
        assertEquals(2, records.size(), "CSV should have header + 1 data row");
        assertEquals("input_1", records.get(0).get(0));
        assertEquals(value, records.get(1).get(0),
                "Round-trip should preserve value with comma");
    }

    @Test
    void msyncValueWithJsonEscapedQuotesIsCaptured() throws IOException {
        // In the JSON body the value appears as: \"Quoted Title\"
        // The regex captures the raw JSON content including the backslash
        // escapes, so the captured string is: \"Quoted Title\"
        String jsonEscapedValue = "\\\"Quoted Title\\\"";
        Path csvFile = convertMsyncValue(jsonEscapedValue, "msync-quotes");

        String csv = Files.readString(csvFile);
        List<List<String>> records = parseCsvRecords(csv);
        assertEquals(2, records.size(), "CSV should have header + 1 data row");
        assertEquals(jsonEscapedValue, records.get(1).get(0),
                "Round-trip should preserve value with escaped quotes");
    }

    @Test
    void msyncValueWithNewlineIsPreservedInCsv() throws IOException {
        // In JSON the value is: line1\nline2 (escaped newline)
        String value = "line1\\nline2";
        Path csvFile = convertMsyncValue(value, "msync-newline");

        String csv = Files.readString(csvFile);
        List<List<String>> records = parseCsvRecords(csv);
        assertEquals(2, records.size(), "CSV should have header + 1 data row");
        assertEquals(value, records.get(1).get(0),
                "Round-trip should preserve value with newline escape");
    }

    @Test
    void csvRoundTripPreservesMultipleSpecialValues() throws IOException {
        // Two mSync fields in one request: one with comma, one with quotes
        String initEntry = entry("GET", "http://localhost:8080/?v-r=init");
        String msyncBody = "{\"csrfToken\":\"abc-123\"," + "\"rpc\":["
                + "{\"type\":\"mSync\",\"node\":42,\"feature\":1,"
                + "\"property\":\"value\"," + "\"value\":\"hello, world\"},"
                + "{\"type\":\"mSync\",\"node\":43,\"feature\":1,"
                + "\"property\":\"value\"," + "\"value\":\"say \\\"hi\\\"\"}],"
                + "\"syncId\":1,\"clientId\":1}";
        String postEntry = entryWithBody("POST",
                "http://localhost:8080/?v-r=uidl&v-uiId=0", msyncBody);

        Path harFile = tempDir.resolve("msync-multi.har");
        Path outputFile = tempDir.resolve("msync-multi.js");
        Files.writeString(harFile, createHar(initEntry, postEntry));
        new HarToK6Converter().convert(harFile, outputFile);

        Path csvFile = tempDir.resolve("msync-multi-data.csv");
        String csv = Files.readString(csvFile);
        List<List<String>> records = parseCsvRecords(csv);
        assertEquals(2, records.size());
        assertEquals(List.of("input_1", "input_2"), records.get(0));
        assertEquals("hello, world", records.get(1).get(0),
                "First field round-trip should preserve comma");
        assertEquals("say \\\"hi\\\"", records.get(1).get(1),
                "Second field round-trip should preserve escaped quotes");
    }

    /**
     * Helper: converts a single mSync value through the full HAR→k6 pipeline
     * and returns the generated CSV file path.
     */
    private Path convertMsyncValue(String value, String name)
            throws IOException {
        String initEntry = entry("GET", "http://localhost:8080/?v-r=init");
        String msyncBody = "{\"csrfToken\":\"abc-123\","
                + "\"rpc\":[{\"type\":\"mSync\",\"node\":42,\"feature\":1,"
                + "\"property\":\"value\"," + "\"value\":\"" + value + "\"}],"
                + "\"syncId\":1,\"clientId\":1}";
        String postEntry = entryWithBody("POST",
                "http://localhost:8080/?v-r=uidl&v-uiId=0", msyncBody);

        Path harFile = tempDir.resolve(name + ".har");
        Path outputFile = tempDir.resolve(name + ".js");
        Files.writeString(harFile, createHar(initEntry, postEntry));
        new HarToK6Converter().convert(harFile, outputFile);

        Path csvFile = tempDir.resolve(name + "-data.csv");
        assertTrue(Files.exists(csvFile), "CSV data file should be created");

        // Verify the generated script uses the RFC 4180 parser
        String script = Files.readString(outputFile);
        assertTrue(script.contains("parseCsvRecords"),
                "Generated script should use RFC 4180 CSV parser");
        return csvFile;
    }

    /**
     * RFC 4180 CSV parser (Java mirror of the generated k6 parseCsvRecords
     * function). Used to verify that escapeCsvValue output round-trips
     * correctly.
     */
    static List<List<String>> parseCsvRecords(String text) {
        List<List<String>> records = new ArrayList<>();
        int i = 0;
        int n = text.length();
        while (i < n) {
            List<String> row = new ArrayList<>();
            // parse first field
            int[] ref = { i };
            row.add(parseField(text, ref, n));
            i = ref[0];
            while (i < n && text.charAt(i) == ',') {
                i++;
                ref[0] = i;
                row.add(parseField(text, ref, n));
                i = ref[0];
            }
            if (i < n && text.charAt(i) == '\r')
                i++;
            if (i < n && text.charAt(i) == '\n')
                i++;
            if (row.size() > 1 || !row.get(0).isEmpty()) {
                records.add(row);
            }
        }
        return records;
    }

    private static String parseField(String text, int[] pos, int n) {
        int i = pos[0];
        if (i < n && text.charAt(i) == '"') {
            i++; // opening quote
            StringBuilder f = new StringBuilder();
            while (i < n) {
                if (text.charAt(i) == '"' && i + 1 < n
                        && text.charAt(i + 1) == '"') {
                    f.append('"');
                    i += 2;
                } else if (text.charAt(i) == '"') {
                    i++;
                    pos[0] = i;
                    return f.toString();
                } else {
                    f.append(text.charAt(i));
                    i++;
                }
            }
            pos[0] = i;
            return f.toString();
        }
        StringBuilder f = new StringBuilder();
        while (i < n && text.charAt(i) != ',' && text.charAt(i) != '\r'
                && text.charAt(i) != '\n') {
            f.append(text.charAt(i));
            i++;
        }
        pos[0] = i;
        return f.toString();
    }

    // --- Helper methods to build HAR JSON ---

    private String entry(String method, String url) {
        return """
                {"startedDateTime":"2026-01-01T00:00:00.000Z","time":0,\
                "request":{"method":"%s","url":"%s","httpVersion":"HTTP/1.1",\
                "headers":[],"queryString":[],"cookies":[],"headersSize":-1,\
                "bodySize":-1},\
                "response":{"status":200,"statusText":"OK","httpVersion":"HTTP/1.1",\
                "headers":[],"cookies":[],"content":{"size":0,"mimeType":"text/html"},\
                "redirectURL":"","headersSize":-1,"bodySize":-1},\
                "cache":{},"timings":{"blocked":0,"dns":0,"connect":0,"send":0,"wait":0,"receive":0}}"""
                .formatted(method, url);
    }

    private String entryWithBody(String method, String url, String body) {
        // Escape the body for embedding in HAR JSON
        String escapedBody = body.replace("\\", "\\\\").replace("\"", "\\\"");
        return """
                {"startedDateTime":"2026-01-01T00:00:00.000Z","time":0,\
                "request":{"method":"%s","url":"%s","httpVersion":"HTTP/1.1",\
                "headers":[],"queryString":[],"cookies":[],"headersSize":-1,\
                "bodySize":-1,\
                "postData":{"mimeType":"application/json","text":"%s"}},\
                "response":{"status":200,"statusText":"OK","httpVersion":"HTTP/1.1",\
                "headers":[],"cookies":[],"content":{"size":0,"mimeType":"text/html"},\
                "redirectURL":"","headersSize":-1,"bodySize":-1},\
                "cache":{},"timings":{"blocked":0,"dns":0,"connect":0,"send":0,"wait":0,"receive":0}}"""
                .formatted(method, url, escapedBody);
    }

    private String createHar(String... entries) {
        return """
                {"log":{"version":"1.2","creator":{"name":"test","version":"1.0"},\
                "entries":[%s],"pages":[]}}"""
                .formatted(String.join(",", entries));
    }
}
