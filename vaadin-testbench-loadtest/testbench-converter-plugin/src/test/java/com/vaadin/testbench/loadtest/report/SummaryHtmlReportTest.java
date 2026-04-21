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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SummaryHtmlReportTest {

    @Test
    void generatesHtmlSiblingFileWithSameBaseName(@TempDir Path dir)
            throws IOException {
        Path summary = writeSummary(dir, "summary.json", "{}");

        SummaryHtmlReport.generate(summary);

        Path expected = dir.resolve("summary.html");
        assertTrue(Files.exists(expected),
                "HTML report should be created next to the JSON input");
    }

    @Test
    void embedsJsonDataInPlaceOfPlaceholder(@TempDir Path dir)
            throws IOException {
        String json = "{\"metrics\":{\"http_reqs\":{\"values\":{\"count\":42}}}}";
        Path summary = writeSummary(dir, "run.json", json);

        SummaryHtmlReport.generate(summary);

        String html = Files.readString(dir.resolve("run.html"));
        assertTrue(html.contains(json),
                "HTML should contain the raw summary JSON");
        assertFalse(html.contains("__K6_SUMMARY_DATA__"),
                "Placeholder token should be fully replaced");
    }

    @Test
    void htmlReportIsSelfContainedHtmlDocument(@TempDir Path dir)
            throws IOException {
        Path summary = writeSummary(dir, "summary.json", "{}");

        SummaryHtmlReport.generate(summary);

        String html = Files.readString(dir.resolve("summary.html"));
        assertTrue(html.startsWith("<!DOCTYPE html>"),
                "Output should be a complete HTML document");
        assertTrue(html.contains("</html>"));
        // Confirms the Maven resource-filtering fix hasn't regressed: the
        // template's JS template literal `${name}` must stay literal in the
        // shipped resource, not be expanded to the plugin's pom name.
        assertFalse(html.contains("TestBench Converter Maven Plugin"),
                "Template must not contain Maven-filtered tokens");
    }

    @Test
    void preservesJsonWithSpecialCharacters(@TempDir Path dir)
            throws IOException {
        // Backslashes and quotes are the obvious gotchas for naive string
        // replacement. `$` is also notable because String.replace uses literal
        // semantics (unlike replaceAll), but pin it anyway.
        String json = "{\"name\":\"a\\\"b$1\\\\c\"}";
        Path summary = writeSummary(dir, "summary.json", json);

        SummaryHtmlReport.generate(summary);

        String html = Files.readString(dir.resolve("summary.html"));
        assertTrue(html.contains(json));
    }

    @Test
    void overwritesExistingHtmlReport(@TempDir Path dir) throws IOException {
        Path summary = writeSummary(dir, "summary.json", "{\"run\":1}");
        Path html = dir.resolve("summary.html");
        Files.writeString(html, "stale contents");

        SummaryHtmlReport.generate(summary);

        String content = Files.readString(html);
        assertEquals(false, content.equals("stale contents"));
        assertTrue(content.contains("{\"run\":1}"));
    }

    private static Path writeSummary(Path dir, String name, String json)
            throws IOException {
        Path summary = dir.resolve(name);
        Files.writeString(summary, json);
        return summary;
    }
}
