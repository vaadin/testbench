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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Generates a self-contained HTML report from a k6 summary JSON file. The HTML
 * template is loaded from a classpath resource and the JSON data is embedded
 * into it.
 */
public class SummaryHtmlReport {

    private static final Logger log = Logger
            .getLogger(SummaryHtmlReport.class.getName());

    private static final String TEMPLATE_RESOURCE = "/report/summary-report.html";

    /**
     * Generates an HTML report from a k6 summary JSON file. The output file is
     * placed next to the JSON file with the same base name and an
     * {@code .html} extension.
     *
     * @param summaryJsonFile
     *            path to the k6 summary JSON file
     * @throws IOException
     *             if reading the JSON or writing the HTML fails
     */
    public static void generate(Path summaryJsonFile) throws IOException {
        String json = Files.readString(summaryJsonFile);
        Path htmlFile = summaryJsonFile.resolveSibling(
                summaryJsonFile.getFileName().toString()
                        .replace(".json", ".html"));

        String template;
        try (InputStream is = SummaryHtmlReport.class
                .getResourceAsStream(TEMPLATE_RESOURCE)) {
            if (is == null) {
                throw new IOException(
                        "Report template not found: " + TEMPLATE_RESOURCE);
            }
            template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        String html = template.replace("__K6_SUMMARY_DATA__", json);
        Files.writeString(htmlFile, html);
        log.info("HTML report generated: " + htmlFile);
    }
}
