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

    @Test
    void customInitCheckAppearsInInitBlock() throws IOException {
        // Build a HAR with a Vaadin init request so the init check block is
        // generated
        String har = createHar(
                vaadinInitEntry("http://localhost:8080/?v-r=init&location="));

        Path harFile = tempDir.resolve("test.har");
        Path outputFile = tempDir.resolve("test.js");
        Files.writeString(harFile, har);

        ResponseCheckConfig checks = ResponseCheckConfig.EMPTY.withCheck(
                ResponseCheckConfig.Scope.INIT, "has page title",
                "(r) => r.body.includes('<title>')");

        new HarToK6Converter().convert(harFile, outputFile,
                new RecorderOptions(new ThresholdConfig(), checks));

        String script = Files.readString(outputFile);
        assertTrue(
                script.contains(
                        "'has page title': (r) => r.body.includes('<title>'),"),
                "Generated script should contain the custom init check");
        // Built-in checks should still be present
        assertTrue(script.contains("'init request succeeded'"),
                "Built-in init checks should still be present");
    }

    @Test
    void customUidlCheckAppearsInUidlBlock() throws IOException {
        // Build a HAR with init + UIDL request
        String har = createHar(
                vaadinInitEntry("http://localhost:8080/?v-r=init&location="),
                vaadinUidlEntry("http://localhost:8080/?v-r=uidl&v-uiId=0"));

        Path harFile = tempDir.resolve("test.har");
        Path outputFile = tempDir.resolve("test.js");
        Files.writeString(harFile, har);

        ResponseCheckConfig checks = ResponseCheckConfig.EMPTY.withCheck(
                ResponseCheckConfig.Scope.UIDL, "no timeout",
                "(r) => !r.body.includes('timeout')");

        new HarToK6Converter().convert(harFile, outputFile,
                new RecorderOptions(new ThresholdConfig(), checks));

        String script = Files.readString(outputFile);
        assertTrue(
                script.contains(
                        "'no timeout': (r) => !r.body.includes('timeout'),"),
                "Generated script should contain the custom UIDL check");
        // Built-in checks should still be present
        assertTrue(script.contains("'UIDL request succeeded'"),
                "Built-in UIDL checks should still be present");
    }

    @Test
    void allScopeCheckAppearsInBothBlocks() throws IOException {
        String har = createHar(
                vaadinInitEntry("http://localhost:8080/?v-r=init&location="),
                vaadinUidlEntry("http://localhost:8080/?v-r=uidl&v-uiId=0"));

        Path harFile = tempDir.resolve("test.har");
        Path outputFile = tempDir.resolve("test.js");
        Files.writeString(harFile, har);

        ResponseCheckConfig checks = ResponseCheckConfig.EMPTY.withCheck(
                ResponseCheckConfig.Scope.ALL, "fast response",
                "(r) => r.timings.duration < 3000");

        new HarToK6Converter().convert(harFile, outputFile,
                new RecorderOptions(new ThresholdConfig(), checks));

        String script = Files.readString(outputFile);
        // Count occurrences — should appear in both init and UIDL blocks
        int count = countOccurrences(script, "'fast response':");
        assertTrue(count >= 2,
                "ALL-scoped check should appear in both init and UIDL blocks, found "
                        + count);
    }

    @Test
    void emptyResponseCheckConfigProducesStandardOutput() throws IOException {
        String har = createHar(
                vaadinInitEntry("http://localhost:8080/?v-r=init&location="));

        Path harFile = tempDir.resolve("test.har");
        Path outputFile = tempDir.resolve("test.js");
        Files.writeString(harFile, har);

        // Explicit DEFAULT options
        new HarToK6Converter().convert(harFile, outputFile,
                RecorderOptions.DEFAULT);
        String withExplicit = Files.readString(outputFile);

        // Two-argument convenience overload should produce identical output
        Path outputFile2 = tempDir.resolve("test.js");
        new HarToK6Converter().convert(harFile, outputFile2);
        String withoutConfig = Files.readString(outputFile2);

        assertEquals(withoutConfig, withExplicit,
                "Explicit DEFAULT options should match the no-args overload");
    }

    @Test
    void formEncodedPostBodyIsRebuiltFromParamsWhenTextIsNull()
            throws IOException {
        // Reproduces the BrowserMob behaviour described in issue #2227: the
        // recorded HAR carries postData.text=null and the form fields in
        // postData.params. The converter must reconstruct a URL-encoded body
        // so the request actually authenticates instead of POSTing an empty
        // string.
        String loginEntry = formLoginEntryWithParams(
                "http://localhost:8081/login",
                "_csrf=NwhHbrWHhCCjCu328-eiLRpD9O2I3dq_evx", "username=user",
                "password=password");

        Path harFile = tempDir.resolve("login.har");
        Path outputFile = tempDir.resolve("login.js");
        Files.writeString(harFile,
                createHar(entry("GET", "http://localhost:8081/"), loginEntry));

        new HarToK6Converter().convert(harFile, outputFile);

        String script = Files.readString(outputFile);
        assertFalse(script.contains("'http://localhost:8081/login',\n    '',"),
                "Form-encoded POST should not ship an empty body. Got:\n"
                        + script);
        assertTrue(script.contains("username=user"),
                "Reconstructed body should contain URL-encoded username field. Got:\n"
                        + script);
        assertTrue(script.contains("password=password"),
                "Reconstructed body should contain URL-encoded password field. Got:\n"
                        + script);
    }

    @Test
    void formEncodedCsrfFieldIsTemplatedToHillaToken() throws IOException {
        String loginEntry = formLoginEntryWithParams(
                "http://localhost:8081/login",
                "_csrf=NwhHbrWHhCCjCu328-eiLRpD9O2I3dq_evx", "username=user",
                "password=password");

        Path harFile = tempDir.resolve("login.har");
        Path outputFile = tempDir.resolve("login.js");
        Files.writeString(harFile,
                createHar(entry("GET", "http://localhost:8081/"), loginEntry));

        new HarToK6Converter().convert(harFile, outputFile);

        String script = Files.readString(outputFile);
        assertTrue(script.contains("_csrf=${hillaCsrfToken}"),
                "Recorded _csrf value should be substituted with the dynamic Hilla CSRF token. Got:\n"
                        + script);
        assertFalse(script.contains("NwhHbrWHhCCjCu328-eiLRpD9O2I3dq_evx"),
                "Recorded _csrf literal should be gone. Got:\n" + script);
    }

    @Test
    void formEncodedBodyTextTakesPrecedenceOverParams() throws IOException {
        // Some HAR producers populate both fields. The recorded text should
        // win so the byte-for-byte recording is preserved when available.
        String body = "username=alice&password=secret";
        String loginEntry = """
                {"startedDateTime":"2026-01-01T00:00:00.000Z","time":0,\
                "request":{"method":"POST","url":"http://localhost:8081/login","httpVersion":"HTTP/1.1",\
                "headers":[],"queryString":[],"cookies":[],"headersSize":-1,\
                "bodySize":-1,\
                "postData":{"mimeType":"application/x-www-form-urlencoded","text":"%s",\
                "params":[{"name":"username","value":"OTHER"},{"name":"password","value":"OTHER"}]}},\
                "response":{"status":200,"statusText":"OK","httpVersion":"HTTP/1.1",\
                "headers":[],"cookies":[],"content":{"size":0,"mimeType":"text/html"},\
                "redirectURL":"","headersSize":-1,"bodySize":-1},\
                "cache":{},"timings":{"blocked":0,"dns":0,"connect":0,"send":0,"wait":0,"receive":0}}"""
                .formatted(body);

        Path harFile = tempDir.resolve("login.har");
        Path outputFile = tempDir.resolve("login.js");
        Files.writeString(harFile,
                createHar(entry("GET", "http://localhost:8081/"), loginEntry));

        new HarToK6Converter().convert(harFile, outputFile);

        String script = Files.readString(outputFile);
        assertTrue(script.contains("username=alice&password=secret"),
                "Recorded text body should win when both text and params are present. Got:\n"
                        + script);
        assertFalse(script.contains("OTHER"),
                "Params should not be used when text is non-empty. Got:\n"
                        + script);
    }

    @Test
    void formEncodedSpecialCharactersAreUrlEncoded() throws IOException {
        // Spaces, ampersands and unicode in form values must be encoded so
        // the reconstructed body parses back to the same fields server-side.
        String loginEntry = formLoginEntryWithParams(
                "http://localhost:8081/login", "username=John Doe",
                "password=p@ss&word");

        Path harFile = tempDir.resolve("login.har");
        Path outputFile = tempDir.resolve("login.js");
        Files.writeString(harFile,
                createHar(entry("GET", "http://localhost:8081/"), loginEntry));

        new HarToK6Converter().convert(harFile, outputFile);

        String script = Files.readString(outputFile);
        assertTrue(script.contains("username=John+Doe"),
                "Spaces should be URL-encoded as +. Got:\n" + script);
        assertTrue(script.contains("password=p%40ss%26word"),
                "@ and & in values should be percent-encoded. Got:\n" + script);
    }

    @Test
    void nonFormEncodedPostBodyIsNotRebuiltFromParams() throws IOException {
        // A JSON POST with empty text and (theoretically) populated params
        // should NOT be reconstructed — only form-encoded recordings are
        // affected by the BrowserMob.
        String jsonEntry = """
                {"startedDateTime":"2026-01-01T00:00:00.000Z","time":0,\
                "request":{"method":"POST","url":"http://localhost:8081/api","httpVersion":"HTTP/1.1",\
                "headers":[],"queryString":[],"cookies":[],"headersSize":-1,\
                "bodySize":-1,\
                "postData":{"mimeType":"application/json","text":null,\
                "params":[{"name":"x","value":"1"}]}},\
                "response":{"status":200,"statusText":"OK","httpVersion":"HTTP/1.1",\
                "headers":[],"cookies":[],"content":{"size":0,"mimeType":"text/html"},\
                "redirectURL":"","headersSize":-1,"bodySize":-1},\
                "cache":{},"timings":{"blocked":0,"dns":0,"connect":0,"send":0,"wait":0,"receive":0}}""";

        Path harFile = tempDir.resolve("api.har");
        Path outputFile = tempDir.resolve("api.js");
        Files.writeString(harFile,
                createHar(entry("GET", "http://localhost:8081/"), jsonEntry));

        new HarToK6Converter().convert(harFile, outputFile);

        String script = Files.readString(outputFile);
        assertFalse(script.contains("x=1"),
                "JSON POSTs should not have their body reconstructed from params. Got:\n"
                        + script);
    }

    // --- Helper methods to build HAR JSON ---

    private String vaadinInitEntry(String url) {
        return """
                {"startedDateTime":"2026-01-01T00:00:00.000Z","time":0,\
                "request":{"method":"GET","url":"%s","httpVersion":"HTTP/1.1",\
                "headers":[],"queryString":[],"cookies":[],"headersSize":-1,\
                "bodySize":-1},\
                "response":{"status":200,"statusText":"OK","httpVersion":"HTTP/1.1",\
                "headers":[],"cookies":[],"content":{"size":0,"mimeType":"text/html"},\
                "redirectURL":"","headersSize":-1,"bodySize":-1},\
                "cache":{},"timings":{"blocked":0,"dns":0,"connect":0,"send":0,"wait":0,"receive":0}}"""
                .formatted(url);
    }

    private String vaadinUidlEntry(String url) {
        return """
                {"startedDateTime":"2026-01-01T00:00:01.000Z","time":0,\
                "request":{"method":"POST","url":"%s","httpVersion":"HTTP/1.1",\
                "headers":[],"queryString":[],"cookies":[],\
                "postData":{"mimeType":"application/json","text":"{\\"csrfToken\\":\\"abcd1234-abcd-abcd-abcd-abcd12345678\\",\\"syncId\\":0,\\"clientId\\":0}"},\
                "headersSize":-1,"bodySize":-1},\
                "response":{"status":200,"statusText":"OK","httpVersion":"HTTP/1.1",\
                "headers":[],"cookies":[],"content":{"size":0,"mimeType":"application/json"},\
                "redirectURL":"","headersSize":-1,"bodySize":-1},\
                "cache":{},"timings":{"blocked":0,"dns":0,"connect":0,"send":0,"wait":0,"receive":0}}"""
                .formatted(url);
    }

    private int countOccurrences(String text, String search) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(search, idx)) != -1) {
            count++;
            idx += search.length();
        }
        return count;
    }

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

    /**
     * Builds a HAR entry that mimics the BrowserMob output for a form login
     * (text=null, mimeType=application/x-www-form-urlencoded, params
     * populated). Each param string is "name=value" — the {@code =} is
     * interpreted as the separator, so the value can be a raw (unencoded)
     * recorded token.
     */
    private String formLoginEntryWithParams(String url,
            String... nameEqualsValue) {
        StringBuilder paramsJson = new StringBuilder();
        for (int i = 0; i < nameEqualsValue.length; i++) {
            if (i > 0) {
                paramsJson.append(',');
            }
            String[] kv = nameEqualsValue[i].split("=", 2);
            String escapedValue = kv[1].replace("\\", "\\\\").replace("\"",
                    "\\\"");
            paramsJson.append("{\"name\":\"").append(kv[0])
                    .append("\",\"value\":\"").append(escapedValue)
                    .append("\"}");
        }
        return """
                {"startedDateTime":"2026-01-01T00:00:00.000Z","time":0,\
                "request":{"method":"POST","url":"%s","httpVersion":"HTTP/1.1",\
                "headers":[],"queryString":[],"cookies":[],"headersSize":-1,\
                "bodySize":-1,\
                "postData":{"mimeType":"application/x-www-form-urlencoded","text":null,\
                "params":[%s]}},\
                "response":{"status":200,"statusText":"OK","httpVersion":"HTTP/1.1",\
                "headers":[],"cookies":[],"content":{"size":0,"mimeType":"text/html"},\
                "redirectURL":"","headersSize":-1,"bodySize":-1},\
                "cache":{},"timings":{"blocked":0,"dns":0,"connect":0,"send":0,"wait":0,"receive":0}}"""
                .formatted(url, paramsJson.toString());
    }

    private String createHar(String... entries) {
        return """
                {"log":{"version":"1.2","creator":{"name":"test","version":"1.0"},\
                "entries":[%s],"pages":[]}}"""
                .formatted(String.join(",", entries));
    }
}
