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

    private String createHar(String... entries) {
        return """
                {"log":{"version":"1.2","creator":{"name":"test","version":"1.0"},\
                "entries":[%s],"pages":[]}}"""
                .formatted(String.join(",", entries));
    }
}
