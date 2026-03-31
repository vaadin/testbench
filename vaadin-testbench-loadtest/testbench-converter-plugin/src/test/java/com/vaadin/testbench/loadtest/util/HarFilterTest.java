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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HarFilterTest {

    @TempDir
    Path tempDir;

    @Test
    void connectRequestsAreFiltered() throws IOException {
        String har = createHarWithEntries(
                entry("CONNECT", "http://localhost:8080", null),
                entry("GET", "http://localhost:8080/", null));

        Path harFile = tempDir.resolve("test.har");
        Files.writeString(harFile, har);

        HarFilter filter = new HarFilter();
        HarFilter.FilterResult result = filter.filter(harFile);

        assertEquals(2, result.originalCount());
        assertEquals(1, result.filteredCount());
        assertEquals(1, result.remainingCount());
    }

    @Test
    void traceRequestsAreFiltered() throws IOException {
        String har = createHarWithEntries(
                entry("TRACE", "http://localhost:8080/", null),
                entry("GET", "http://localhost:8080/", null));

        Path harFile = tempDir.resolve("test.har");
        Files.writeString(harFile, har);

        HarFilter filter = new HarFilter();
        HarFilter.FilterResult result = filter.filter(harFile);

        assertEquals(1, result.filteredCount());
        assertEquals(1, result.remainingCount());
    }

    @Test
    void webSocketUpgradeRequestsAreFiltered() throws IOException {
        String har = createHarWithEntries(
                entry("GET", "http://localhost:8080/PUSH",
                        List.of(header("Upgrade", "websocket"),
                                header("Connection", "Upgrade"))),
                entry("GET", "http://localhost:8080/", null));

        Path harFile = tempDir.resolve("test.har");
        Files.writeString(harFile, har);

        HarFilter filter = new HarFilter();
        HarFilter.FilterResult result = filter.filter(harFile);

        assertEquals(1, result.filteredCount());
        assertEquals(1, result.remainingCount());
    }

    @Test
    void supportedMethodsAreKept() throws IOException {
        String har = createHarWithEntries(
                entry("GET", "http://localhost:8080/", null),
                entry("POST", "http://localhost:8080/api", null),
                entry("PUT", "http://localhost:8080/api/1", null),
                entry("PATCH", "http://localhost:8080/api/1", null),
                entry("DELETE", "http://localhost:8080/api/1", null),
                entry("HEAD", "http://localhost:8080/", null),
                entry("OPTIONS", "http://localhost:8080/api", null));

        Path harFile = tempDir.resolve("test.har");
        Files.writeString(harFile, har);

        HarFilter filter = new HarFilter();
        HarFilter.FilterResult result = filter.filter(harFile);

        assertEquals(0, result.filteredCount());
        assertEquals(7, result.remainingCount());
    }

    // --- Helper methods to build HAR JSON ---

    private String header(String name, String value) {
        return "{\"name\":\"" + name + "\",\"value\":\"" + value + "\"}";
    }

    private String entry(String method, String url, List<String> headerJsons) {
        String headers = headerJsons != null
                ? "[" + String.join(",", headerJsons) + "]"
                : "[]";
        return """
                {"startedDateTime":"2026-01-01T00:00:00.000Z","time":0,\
                "request":{"method":"%s","url":"%s","httpVersion":"HTTP/1.1",\
                "headers":%s,"queryString":[],"cookies":[],"headersSize":-1,\
                "bodySize":-1},\
                "response":{"status":200,"statusText":"OK","httpVersion":"HTTP/1.1",\
                "headers":[],"cookies":[],"content":{"size":0,"mimeType":"text/html"},\
                "redirectURL":"","headersSize":-1,"bodySize":-1},\
                "cache":{},"timings":{"blocked":0,"dns":0,"connect":0,"send":0,"wait":0,"receive":0}}"""
                .formatted(method, url, headers);
    }

    private String createHarWithEntries(String... entries) {
        return """
                {"log":{"version":"1.2","creator":{"name":"test","version":"1.0"},\
                "entries":[%s],"pages":[]}}"""
                .formatted(String.join(",", entries));
    }
}
