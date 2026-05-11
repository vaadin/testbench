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

        // Each scenario gets floor(50 * 10 / 100) = 5 VUs and uses the default
        // ramping-vus executor with 10s ramp-up / 10s sustain / 10s ramp-down.
        String alphaBlock = extractScenarioBlock(content, "alpha");
        String betaBlock = extractScenarioBlock(content, "beta");

        assertTrue(alphaBlock.contains("executor: 'ramping-vus'"),
                "alpha should use ramping-vus executor:\n" + alphaBlock);
        assertTrue(betaBlock.contains("executor: 'ramping-vus'"),
                "beta should use ramping-vus executor:\n" + betaBlock);

        String expectedStages = """
                stages: [
                        { duration: '10s', target: 5 },
                        { duration: '10s', target: 5 },
                        { duration: '10s', target: 0 },
                      ],""";
        assertTrue(alphaBlock.contains(expectedStages),
                "alpha stages should target 5 VUs:\n" + alphaBlock);
        assertTrue(betaBlock.contains(expectedStages),
                "beta stages should target 5 VUs:\n" + betaBlock);

        assertTrue(alphaBlock.contains("exec: 'alphaScenario'"));
        assertTrue(betaBlock.contains("exec: 'betaScenario'"));
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

        // small gets 1/4 * 100 = 25 VUs, big gets 3/4 * 100 = 75 VUs — each
        // under its own scenario key with an independent stages block.
        String smallBlock = extractScenarioBlock(content, "small");
        String bigBlock = extractScenarioBlock(content, "big");

        assertTrue(smallBlock.contains("target: 25"),
                "small should target 25 VUs:\n" + smallBlock);
        assertFalse(smallBlock.contains("target: 75"),
                "small block must not leak big's VU target:\n" + smallBlock);
        assertTrue(bigBlock.contains("target: 75"),
                "big should target 75 VUs:\n" + bigBlock);
        assertFalse(bigBlock.contains("target: 25"),
                "big block must not leak small's VU target:\n" + bigBlock);

        assertTrue(smallBlock.contains("exec: 'smallScenario'"),
                "small scenario exec missing:\n" + smallBlock);
        assertTrue(bigBlock.contains("exec: 'bigScenario'"),
                "big scenario exec missing:\n" + bigBlock);
    }

    /**
     * Extracts the body of a named scenario object from the generated
     * {@code scenarios: { ... }} block so individual scenarios can be asserted
     * on independently.
     */
    private static String extractScenarioBlock(String content, String name) {
        String marker = name + ": {";
        int start = content.indexOf(marker);
        assertTrue(start >= 0,
                "Scenario '" + name + "' missing in:\n" + content);
        int braceStart = content.indexOf('{', start);
        int depth = 1;
        int i = braceStart + 1;
        while (i < content.length() && depth > 0) {
            char c = content.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
            }
            i++;
        }
        return content.substring(start, i);
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
                output, 10, "30s", new ThresholdConfig(),
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
    void combine_scenarioWithUnbalancedBracesInRegexAndCommentsClosesCleanly()
            throws IOException {
        // The brace walker must skip braces inside regex literals, comments,
        // and strings. Each line of the body below contains a shape that
        // broke the previous naive counter (regex `\{`, isolated `{` in a
        // comment, isolated `{` and `}` in string literals); without
        // lexer-aware skipping, the walker either truncates the body at a
        // `}` inside a string or overshoots and slurps the trailing
        // handleSummary export.
        List<String> bodyLines = List.of(
                "// anchor on `{` to skip change-record keys",
                "let body = 'a { is hard to count'",
                "let body2 = 'and so is a } on its own'",
                "var found = body.match(/\\{\"key\":\"[^\"]+\"/g)",
                "if (found) gridKeys = found.map(s => s.split('\"')[3])");
        Path scenario = writeScenario("alpha", """
                  // anchor on `{` to skip change-record keys
                  let body = 'a { is hard to count'
                  let body2 = 'and so is a } on its own'
                  var found = body.match(/\\{"key":"[^"]+"/g)
                  if (found) gridKeys = found.map(s => s.split('"')[3])
                """);
        String existing = Files.readString(scenario);
        Files.writeString(scenario, existing
                + "\nexport function handleSummary(data) {\n  return {}\n}\n");

        K6ScenarioCombiner combiner = new K6ScenarioCombiner();
        Path output = tempDir.resolve("combined.js");
        combiner.combine(List.of(new ScenarioConfig("alpha", scenario, 100)),
                output, 5, "10s");

        String content = Files.readString(output);

        // Overshoot guard: only the combiner's own handleSummary should
        // survive. A second one means the walker absorbed the scenario
        // file's trailing handleSummary into the wrapped body.
        int handleSummaryCount = 0;
        int idx = 0;
        while ((idx = content.indexOf("function handleSummary", idx)) != -1) {
            handleSummaryCount++;
            idx += "function handleSummary".length();
        }
        assertEquals(1, handleSummaryCount,
                "Combined script must export exactly one handleSummary. Got:\n"
                        + content);

        // Undershoot guard: every body line must appear between the wrapper
        // opening and the combiner-appended handleSummary. A `}` inside a
        // string literal misread as a real closing brace would truncate the
        // body, dropping the lines after it from the output entirely.
        int wrapperStart = content.indexOf("export function alphaScenario() {");
        assertTrue(wrapperStart >= 0,
                "Scenario function should be wrapped:\n" + content);
        int handleSummaryStart = content.indexOf("function handleSummary",
                wrapperStart);
        String wrappedRegion = content.substring(wrapperStart,
                handleSummaryStart);
        for (String line : bodyLines) {
            assertTrue(wrappedRegion.contains(line),
                    "Wrapped scenario should contain body line: " + line
                            + "\nWrapped region was:\n" + wrappedRegion);
        }
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
