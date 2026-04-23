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

import com.vaadin.testbench.loadtest.util.K6ScenarioCombiner.ScenarioConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class K6ScenarioCombinerTest {

    @TempDir
    Path tempDir;

    private Path writeScenario(String name, String functionBody)
            throws IOException {
        String script = """
                import http from 'k6/http'
                import { sleep } from 'k6'

                export default function() {
                %s
                }
                """.formatted(functionBody);
        Path file = tempDir.resolve(name + ".js");
        Files.writeString(file, script);
        return file;
    }

    @Test
    void combine_equalWeights_splitsVusEvenly() throws IOException {
        Path a = writeScenario("alpha", "  http.get(`${BASE_URL}/a`);");
        Path b = writeScenario("beta", "  http.get(`${BASE_URL}/b`);");

        K6ScenarioCombiner combiner = new K6ScenarioCombiner();
        Path output = tempDir.resolve("combined.js");

        combiner.combine(List.of(new ScenarioConfig("alpha", a, 50),
                new ScenarioConfig("beta", b, 50)), output, 10, "30s");

        String content = Files.readString(output);

        // Each scenario gets floor(50 * 10 / 100) = 5 VUs.
        assertTrue(content.contains("alpha: {"));
        assertTrue(content.contains("beta: {"));
        assertTrue(content.contains("exec: 'alphaScenario'"));
        assertTrue(content.contains("exec: 'betaScenario'"));
        assertTrue(content.contains("export function alphaScenario()"));
        assertTrue(content.contains("export function betaScenario()"));
    }

    @Test
    void combine_unevenWeights_distributesVusProportionally()
            throws IOException {
        Path small = writeScenario("small", "  http.get(`${BASE_URL}/s`);");
        Path big = writeScenario("big", "  http.get(`${BASE_URL}/b`);");

        K6ScenarioCombiner combiner = new K6ScenarioCombiner();
        Path output = tempDir.resolve("combined.js");

        combiner.combine(List.of(new ScenarioConfig("small", small, 1),
                new ScenarioConfig("big", big, 3)), output, 100, "30s");

        String content = Files.readString(output);
        // small gets ~25 VUs (1/4 * 100), big gets ~75 (3/4 * 100).
        assertTrue(content.contains("small: {"));
        assertTrue(content.contains("big: {"));
        // The scenario named "big" must not collide with "small".
        int smallExec = content.indexOf("exec: 'smallScenario'");
        int bigExec = content.indexOf("exec: 'bigScenario'");
        assertTrue(smallExec >= 0);
        assertTrue(bigExec >= 0);
    }

    @Test
    void combine_singleScenario_stillProducesValidScript() throws IOException {
        Path only = writeScenario("only", "  http.get(`${BASE_URL}/`);");

        K6ScenarioCombiner combiner = new K6ScenarioCombiner();
        Path output = tempDir.resolve("single.js");

        combiner.combine(List.of(new ScenarioConfig("only", only, 100)), output,
                5, "10s");

        String content = Files.readString(output);
        assertTrue(content.contains("export function onlyScenario()"));
        assertTrue(content.contains("only: {"));
    }

    @Test
    void combine_includesThresholdsAndLoadProfileSections() throws IOException {
        Path a = writeScenario("alpha", "  http.get(`${BASE_URL}/a`);");
        Path b = writeScenario("beta", "  http.get(`${BASE_URL}/b`);");

        K6ScenarioCombiner combiner = new K6ScenarioCombiner();
        Path output = tempDir.resolve("combined.js");

        combiner.combine(
                List.of(new ScenarioConfig("alpha", a, 50),
                        new ScenarioConfig("beta", b, 50)),
                output, 10, "30s", ThresholdConfig.DEFAULT,
                LoadProfile.ramp("5s", "5s"));

        String content = Files.readString(output);
        assertTrue(content.contains("thresholds"),
                "Threshold block missing:\n" + content);
        assertTrue(content.contains("scenarios"),
                "Scenarios block missing:\n" + content);
        assertTrue(content.contains("handleSummary"),
                "Summary handler missing:\n" + content);
    }

    @Test
    void equalWeights_distributesWeightEvenly() throws IOException {
        Path a = writeScenario("alpha", "  http.get(`${BASE_URL}/a`);");
        Path b = writeScenario("beta", "  http.get(`${BASE_URL}/b`);");
        Path c = writeScenario("gamma", "  http.get(`${BASE_URL}/c`);");

        List<ScenarioConfig> configs = K6ScenarioCombiner
                .equalWeights(List.of(a, b, c));

        assertEquals(3, configs.size());
        for (ScenarioConfig config : configs) {
            assertEquals(33, config.weight());
        }
    }

    @Test
    void equalWeights_convertsKebabCaseToCamelCaseName() throws IOException {
        Path file = tempDir.resolve("my-scenario.js");
        Files.writeString(file, "export default function() { /* body */ }");

        List<ScenarioConfig> configs = K6ScenarioCombiner
                .equalWeights(List.of(file));

        assertEquals("myScenario", configs.get(0).name());
    }

    @Test
    void combine_noSharedArrayWhenAbsent() throws IOException {
        Path a = writeScenario("alpha", "  http.get(`${BASE_URL}/a`);");

        K6ScenarioCombiner combiner = new K6ScenarioCombiner();
        Path output = tempDir.resolve("combined.js");

        combiner.combine(List.of(new ScenarioConfig("alpha", a, 100)), output,
                5, "10s");

        String content = Files.readString(output);
        assertFalse(content.contains("new SharedArray"),
                "SharedArray import should not be present when unused:\n"
                        + content);
    }
}
