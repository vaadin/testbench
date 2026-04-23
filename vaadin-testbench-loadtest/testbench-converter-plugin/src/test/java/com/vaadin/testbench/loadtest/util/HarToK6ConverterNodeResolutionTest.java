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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that {@link HarToK6Converter} rewrites positive server-assigned
 * {@code "node":N} UIDL references against a stable key map, so that generated
 * k6 scripts survive UI reorders and conditional rendering.
 */
class HarToK6ConverterNodeResolutionTest {

    @TempDir
    Path tempDir;

    @Test
    void idAttributeYieldsStableKey() throws IOException {
        String initUrl = "http://localhost:8080/?v-r=init&location=";
        String uidlUrl = "http://localhost:8080/?v-r=uidl&v-uiId=0";

        // Init response declares node 42 with id="login-button".
        String initResponse = forEvery(
                "{\"changes\":[{\"node\":42,\"type\":\"put\",\"key\":\"id\",\"value\":\"login-button\"}]}");

        // Follow-up UIDL request references node 42 in an RPC payload.
        String uidlBody = uidlBodyWithNode(42);

        Path outputFile = runConvert("login-flow",
                vaadinInitEntryWithResponse(initUrl, initResponse),
                vaadinUidlEntry(uidlUrl, uidlBody));

        String script = Files.readString(outputFile);
        assertTrue(
                script.contains(
                        "\"node\":${resolveNode(nodeMap, 'login-button')}"),
                "Generated script should reference the id-based stable key");
        assertFalse(script.contains("\"node\":42"),
                "The recorded literal node ID should be fully substituted");
        assertTrue(script.contains("let nodeMap = {}"),
                "Init block should declare the runtime nodeMap");
        assertTrue(
                script.contains(
                        "nodeMap = updateNodeMap(nodeMap, response.body)"),
                "UIDL response extraction should refresh the nodeMap");
        assertTrue(script.contains(
                "import { updateNodeMap, resolveNode } from '../utils/vaadin-k6-helpers.js'"),
                "Import should be pulled from vaadin-k6-helpers.js");
    }

    @Test
    void tagFallbackYieldsOrdinalKey() throws IOException {
        String initUrl = "http://localhost:8080/?v-r=init&location=";
        String uidlUrl = "http://localhost:8080/?v-r=uidl&v-uiId=0";

        // Response declares node 55 of tag vaadin-button, no id attribute.
        String initResponse = forEvery(
                "{\"changes\":[{\"node\":55,\"type\":\"put\",\"key\":\"tag\",\"value\":\"vaadin-button\"}]}");
        String uidlBody = uidlBodyWithNode(55);

        Path outputFile = runConvert("button-tag",
                vaadinInitEntryWithResponse(initUrl, initResponse),
                vaadinUidlEntry(uidlUrl, uidlBody));

        String script = Files.readString(outputFile);
        assertTrue(
                script.contains(
                        "\"node\":${resolveNode(nodeMap, 'vaadin-button#1')}"),
                "Generated script should reference the tag#ordinal fallback key");
        assertFalse(script.contains("\"node\":55"),
                "Recorded literal node ID should be substituted");
    }

    @Test
    void duplicateTagsProduceSequentialOrdinals() throws IOException {
        String initUrl = "http://localhost:8080/?v-r=init&location=";
        String uidlUrl1 = "http://localhost:8080/?v-r=uidl&v-uiId=0";
        String uidlUrl2 = "http://localhost:8080/?v-r=uidl&v-uiId=0";

        String initResponse = forEvery("{\"changes\":["
                + "{\"node\":10,\"type\":\"put\",\"key\":\"tag\",\"value\":\"vaadin-button\"},"
                + "{\"node\":11,\"type\":\"put\",\"key\":\"tag\",\"value\":\"vaadin-button\"}"
                + "]}");

        // Two follow-up requests each target a different button, exercising
        // both ordinal keys.
        Path outputFile = runConvert("duplicate-tags",
                vaadinInitEntryWithResponse(initUrl, initResponse),
                vaadinUidlEntry(uidlUrl1, uidlBodyWithNode(10)),
                vaadinUidlEntry(uidlUrl2, uidlBodyWithNode(11)));

        String script = Files.readString(outputFile);
        assertTrue(
                script.contains(
                        "\"node\":${resolveNode(nodeMap, 'vaadin-button#1')}"),
                "First vaadin-button should resolve as ordinal 1");
        assertTrue(
                script.contains(
                        "\"node\":${resolveNode(nodeMap, 'vaadin-button#2')}"),
                "Second vaadin-button should resolve as ordinal 2");
    }

    @Test
    void negativeNodeIdsAreLeftAsLiterals() throws IOException {
        String initUrl = "http://localhost:8080/?v-r=init&location=";
        String uidlUrl = "http://localhost:8080/?v-r=uidl&v-uiId=0";

        // Response has no changes relevant to the negative ID.
        String initResponse = forEvery("{\"changes\":[]}");

        // RPC body with a negative (client-side temp) node reference.
        String body = "{\"csrfToken\":\"abcd1234-abcd-abcd-abcd-abcd12345678\","
                + "\"rpc\":[{\"type\":\"mSync\",\"node\":-3,\"feature\":1}],"
                + "\"syncId\":0,\"clientId\":0}";

        Path outputFile = runConvert("negative-node",
                vaadinInitEntryWithResponse(initUrl, initResponse),
                vaadinUidlEntry(uidlUrl, body));

        String script = Files.readString(outputFile);
        assertTrue(script.contains("\"node\":-3"),
                "Negative node IDs must be preserved verbatim (client-side temp)");
        assertFalse(script.contains("resolveNode(nodeMap, '-3')"),
                "Negative IDs must never reach the resolver");
    }

    @Test
    void unresolvedNodesAreLeftWithDiagnosticComment() throws IOException {
        String initUrl = "http://localhost:8080/?v-r=init&location=";
        String uidlUrl = "http://localhost:8080/?v-r=uidl&v-uiId=0";

        // Empty changes — node 99 is never registered.
        String initResponse = forEvery("{\"changes\":[]}");

        Path outputFile = runConvert("unresolved-node",
                vaadinInitEntryWithResponse(initUrl, initResponse),
                vaadinUidlEntry(uidlUrl, uidlBodyWithNode(99)));

        String script = Files.readString(outputFile);
        assertTrue(script.contains("\"node\":99"),
                "Unresolved positive node IDs should be left as literals");
        assertFalse(script.contains("resolveNode(nodeMap, '99')"),
                "Unresolved IDs must not reach the resolver");
        assertTrue(script.contains("/* unresolved node 99 */"),
                "An unresolved node ID should surface a diagnostic comment");
    }

    @Test
    void optOutFlagProducesLegacyByteIdenticalOutput() throws IOException {
        String initUrl = "http://localhost:8080/?v-r=init&location=";
        String uidlUrl = "http://localhost:8080/?v-r=uidl&v-uiId=0";

        // Even with a fully-populated changes stream, opting out must suppress
        // every node-resolution side-effect (imports, nodeMap, substitutions,
        // diagnostic comments).
        String initResponse = forEvery(
                "{\"changes\":[{\"node\":42,\"type\":\"put\",\"key\":\"id\",\"value\":\"login-button\"}]}");
        String uidlBody = uidlBodyWithNode(42);

        Path harFile = tempDir.resolve("opt-out.har");
        Files.writeString(harFile,
                createHar(vaadinInitEntryWithResponse(initUrl, initResponse),
                        vaadinUidlEntry(uidlUrl, uidlBody)));

        // Baseline: legacy literal-number emitter (resolveNodeIds=false).
        Path baselineFile = tempDir.resolve("baseline.js");
        new HarToK6Converter(false).convert(harFile, baselineFile);

        // Second legacy run must match the first byte-for-byte.
        Path legacyFile = tempDir.resolve("legacy.js");
        new HarToK6Converter(false).convert(harFile, legacyFile);

        String baselineScript = Files.readString(baselineFile);
        String legacyScript = Files.readString(legacyFile);
        // Rename-away scenario tag differences so we're comparing generator
        // output shape, not file-name-derived tag prefixes.
        String normalisedBaseline = baselineScript.replace("baseline", "X");
        String normalisedLegacy = legacyScript.replace("legacy", "X");
        assertEquals(normalisedLegacy, normalisedBaseline,
                "Legacy opt-out mode must be deterministic");

        assertTrue(baselineScript.contains("\"node\":42"),
                "Legacy mode should emit the recorded literal node ID");
        assertFalse(baselineScript.contains("resolveNode(nodeMap"),
                "Legacy mode must not emit the resolver call");
        assertFalse(baselineScript.contains("let nodeMap"),
                "Legacy mode must not declare nodeMap");
        assertFalse(baselineScript.contains("updateNodeMap"),
                "Legacy mode must not emit updateNodeMap invocations");
        assertFalse(
                baselineScript
                        .contains("import { updateNodeMap, resolveNode }"),
                "Legacy mode must not import the runtime resolver helpers");
    }

    // --- Fixture builders (inline HAR JSON, matching HarToK6ConverterTest) ---

    private Path runConvert(String name, String... entries) throws IOException {
        Path harFile = tempDir.resolve(name + ".har");
        Path outputFile = tempDir.resolve(name + ".js");
        Files.writeString(harFile, createHar(entries));
        new HarToK6Converter().convert(harFile, outputFile);
        return outputFile;
    }

    /**
     * Wraps a single-entry UIDL response shape in the `for(;;);[...]` framing
     * Vaadin uses on the wire.
     */
    private String forEvery(String entryJson) {
        return "for(;;);[" + entryJson + "]";
    }

    private String uidlBodyWithNode(int node) {
        return "{\"csrfToken\":\"abcd1234-abcd-abcd-abcd-abcd12345678\","
                + "\"rpc\":[{\"type\":\"mSync\",\"node\":" + node
                + ",\"feature\":1}]," + "\"syncId\":0,\"clientId\":0}";
    }

    private String vaadinInitEntryWithResponse(String url,
            String responseText) {
        String escaped = escapeForHarJson(responseText);
        return """
                {"startedDateTime":"2026-01-01T00:00:00.000Z","time":0,\
                "request":{"method":"GET","url":"%s","httpVersion":"HTTP/1.1",\
                "headers":[],"queryString":[],"cookies":[],"headersSize":-1,\
                "bodySize":-1},\
                "response":{"status":200,"statusText":"OK","httpVersion":"HTTP/1.1",\
                "headers":[],"cookies":[],"content":{"size":0,"mimeType":"application/json","text":"%s"},\
                "redirectURL":"","headersSize":-1,"bodySize":-1},\
                "cache":{},"timings":{"blocked":0,"dns":0,"connect":0,"send":0,"wait":0,"receive":0}}"""
                .formatted(url, escaped);
    }

    private String vaadinUidlEntry(String url, String body) {
        String escapedBody = escapeForHarJson(body);
        return """
                {"startedDateTime":"2026-01-01T00:00:01.000Z","time":0,\
                "request":{"method":"POST","url":"%s","httpVersion":"HTTP/1.1",\
                "headers":[],"queryString":[],"cookies":[],\
                "postData":{"mimeType":"application/json","text":"%s"},\
                "headersSize":-1,"bodySize":-1},\
                "response":{"status":200,"statusText":"OK","httpVersion":"HTTP/1.1",\
                "headers":[],"cookies":[],"content":{"size":0,"mimeType":"application/json"},\
                "redirectURL":"","headersSize":-1,"bodySize":-1},\
                "cache":{},"timings":{"blocked":0,"dns":0,"connect":0,"send":0,"wait":0,"receive":0}}"""
                .formatted(url, escapedBody);
    }

    private String createHar(String... entries) {
        return """
                {"log":{"version":"1.2","creator":{"name":"test","version":"1.0"},\
                "entries":[%s],"pages":[]}}"""
                .formatted(String.join(",", entries));
    }

    private String escapeForHarJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
