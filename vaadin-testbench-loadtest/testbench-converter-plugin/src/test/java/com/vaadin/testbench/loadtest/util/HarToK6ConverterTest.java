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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
        // Init request establishes a Vaadin session so mSync detection activates
        String initEntry = entry("GET", "http://localhost:8080/?v-r=init");
        // POST with mSync value containing a comma
        String msyncBody = "{\"csrfToken\":\"abc-123\","
                + "\"rpc\":[{\"type\":\"mSync\",\"node\":42,\"feature\":1,"
                + "\"property\":\"value\","
                + "\"value\":\"Cronan's Guide to Nanomixology, 2nd ed.\"}],"
                + "\"syncId\":1,\"clientId\":1}";
        String postEntry = entryWithBody("POST",
                "http://localhost:8080/?v-r=uidl&v-uiId=0", msyncBody);

        Path harFile = tempDir.resolve("msync-comma.har");
        Path outputFile = tempDir.resolve("msync-comma.js");
        Files.writeString(harFile, createHar(initEntry, postEntry));

        new HarToK6Converter().convert(harFile, outputFile);

        // CSV should contain the full value (with comma) properly quoted
        Path csvFile = tempDir.resolve("msync-comma-data.csv");
        assertTrue(Files.exists(csvFile), "CSV data file should be created");
        String csv = Files.readString(csvFile);
        assertTrue(csv.contains("input_1"), "CSV should have header");
        // Value contains a comma, so it must be RFC 4180 quoted
        assertTrue(
                csv.contains(
                        "\"Cronan's Guide to Nanomixology, 2nd ed.\""),
                "CSV should contain properly quoted value with comma");

        // The generated script should use parseCsvLine (not naive split)
        String script = Files.readString(outputFile);
        assertTrue(script.contains("parseCsvLine"),
                "Generated script should use RFC 4180 CSV parser");
        assertTrue(script.contains("${inputRow.input_1}"),
                "Generated script should reference CSV input");
    }

    @Test
    void msyncValueWithJsonEscapedQuotesIsCaptured() throws IOException {
        String initEntry = entry("GET", "http://localhost:8080/?v-r=init");
        // POST with mSync value containing JSON-escaped quotes
        String msyncBody = "{\"csrfToken\":\"abc-123\","
                + "\"rpc\":[{\"type\":\"mSync\",\"node\":42,\"feature\":1,"
                + "\"property\":\"value\","
                + "\"value\":\"\\\"Quoted Title\\\"\"}],"
                + "\"syncId\":1,\"clientId\":1}";
        String postEntry = entryWithBody("POST",
                "http://localhost:8080/?v-r=uidl&v-uiId=0", msyncBody);

        Path harFile = tempDir.resolve("msync-quotes.har");
        Path outputFile = tempDir.resolve("msync-quotes.js");
        Files.writeString(harFile, createHar(initEntry, postEntry));

        new HarToK6Converter().convert(harFile, outputFile);

        Path csvFile = tempDir.resolve("msync-quotes-data.csv");
        assertTrue(Files.exists(csvFile), "CSV data file should be created");
        String csv = Files.readString(csvFile);
        assertTrue(csv.contains("input_1"), "CSV should have header");
        // The captured value includes JSON escape sequences; CSV must
        // double-quote them per RFC 4180
        assertTrue(csv.contains("\"\""),
                "CSV should contain escaped quotes");
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
