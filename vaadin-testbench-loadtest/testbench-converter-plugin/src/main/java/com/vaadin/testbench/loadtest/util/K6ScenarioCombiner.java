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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Combines multiple k6 test files into a single test with weighted scenarios.
 * Uses k6's built-in scenario feature to run different user workflows in
 * parallel with configurable weights (percentage of VUs assigned to each
 * scenario).
 */
public class K6ScenarioCombiner {

    /**
     * Creates a new K6ScenarioCombiner instance.
     */
    public K6ScenarioCombiner() {
    }

    /**
     * Represents a scenario with its source file and weight.
     */
    public record ScenarioConfig(String name, Path testFile, int weight) {
    }

    /**
     * Combines multiple k6 test files into a single test with weighted
     * scenarios using default thresholds and ramping load.
     *
     * @param scenarios
     *            list of scenario configurations with weights
     * @param outputFile
     *            path for the combined output file
     * @param totalVus
     *            total number of virtual users to distribute
     * @param duration
     *            test duration (e.g., "30s", "1m")
     * @throws IOException
     *             if file operations fail
     */
    public void combine(List<ScenarioConfig> scenarios, Path outputFile,
            int totalVus, String duration) throws IOException {
        combine(scenarios, outputFile, totalVus, duration,
                new ThresholdConfig(), LoadProfile.ramp("10s", "10s"));
    }

    /**
     * Combines multiple k6 test files into a single test with weighted
     * scenarios using ramping load.
     *
     * @param scenarios
     *            list of scenario configurations with weights
     * @param outputFile
     *            path for the combined output file
     * @param totalVus
     *            total number of virtual users to distribute
     * @param duration
     *            test duration (e.g., "30s", "1m")
     * @param thresholdConfig
     *            threshold configuration for the combined script
     * @throws IOException
     *             if file operations fail
     */
    public void combine(List<ScenarioConfig> scenarios, Path outputFile,
            int totalVus, String duration, ThresholdConfig thresholdConfig)
            throws IOException {
        combine(scenarios, outputFile, totalVus, duration, thresholdConfig,
                LoadProfile.ramp("10s", "10s"));
    }

    /**
     * Combines multiple k6 test files into a single test with weighted
     * scenarios using a load profile for ramping configuration.
     *
     * @param scenarios
     *            list of scenario configurations with weights
     * @param outputFile
     *            path for the combined output file
     * @param totalVus
     *            total number of virtual users to distribute
     * @param duration
     *            test duration (e.g., "30s", "1m")
     * @param thresholdConfig
     *            threshold configuration for the combined script
     * @param loadProfile
     *            load profile controlling ramping behavior
     * @throws IOException
     *             if file operations fail
     */
    public void combine(List<ScenarioConfig> scenarios, Path outputFile,
            int totalVus, String duration, ThresholdConfig thresholdConfig,
            LoadProfile loadProfile) throws IOException {

        StringBuilder sb = new StringBuilder();

        // Pre-pass: extract SharedArray blocks and request tags from
        // scenarios.
        // Each scenario gets a uniquely-named variable (e.g.
        // crudExampleInputData)
        // so multiple scenarios with CSV data don't collide.
        Map<String, String> sharedArrayBlocks = new LinkedHashMap<>();
        List<String> allRequestTags = new ArrayList<>();
        String csvParserFunction = null;
        Pattern tagPattern = Pattern
                .compile("tags:\\s*\\{\\s*name:\\s*'([^']+)'");
        for (ScenarioConfig config : scenarios) {
            String content = Files.readString(config.testFile());
            String block = extractSharedArrayBlock(content);
            if (block != null) {
                // Rename inputData → {name}InputData and SharedArray label
                String renamed = block
                        .replace("inputData", config.name() + "InputData")
                        .replace("'input data'",
                                "'" + config.name() + " input data'");
                sharedArrayBlocks.put(config.name(), renamed);
                // Extract the parseCsvRecords function once (it is identical
                // across all generated scripts)
                if (csvParserFunction == null) {
                    csvParserFunction = extractCsvParserFunction(content);
                }
            }
            // Extract request tag names for per-request sub-metrics
            Matcher tagMatcher = tagPattern.matcher(content);
            while (tagMatcher.find()) {
                allRequestTags.add(tagMatcher.group(1));
            }
        }
        boolean needsSharedArray = !sharedArrayBlocks.isEmpty();

        // Header
        sb.append("// Combined k6 test with weighted scenarios\n");
        sb.append("// Auto-generated by testbench-converter-plugin\n\n");
        sb.append("import http from 'k6/http'\n");
        sb.append("import { sleep, check, fail } from 'k6'\n");
        if (needsSharedArray) {
            sb.append("import { SharedArray } from 'k6/data'\n");
        }
        sb.append(
                "import {extractJSessionId, getHillaCsrfToken, getVaadinPushId, getVaadinSecurityKey, getVaadinUiId, updateNodeMap, resolveNode} from '../utils/vaadin-k6-helpers.js'\n");
        sb.append(
                "import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js'\n\n");

        // Configuration variables
        sb.append(
                "// Server configuration - can be overridden with: k6 run -e APP_IP=192.168.1.100 -e APP_PORT=8081 script.js\n");
        sb.append("const APP_IP = __ENV.APP_IP || 'localhost';\n");
        sb.append("const APP_PORT = __ENV.APP_PORT || '8080';\n");
        sb.append("const BASE_URL = `http://${APP_IP}:${APP_PORT}`;\n\n");

        // Add parseCsvRecords function and SharedArray blocks at module scope
        // (must be in init context for k6)
        if (csvParserFunction != null) {
            sb.append(csvParserFunction).append("\n\n");
        }
        for (Map.Entry<String, String> entry : sharedArrayBlocks.entrySet()) {
            sb.append(entry.getValue()).append("\n\n");
        }

        // Calculate VUs for each scenario based on weights
        int totalWeight = scenarios.stream().mapToInt(ScenarioConfig::weight)
                .sum();

        // Generate options with scenarios and configurable thresholds
        sb.append("export const options = {\n");
        sb.append(thresholdConfig.toK6ThresholdsBlock(allRequestTags));
        sb.append("  scenarios: {\n");

        for (int i = 0; i < scenarios.size(); i++) {
            ScenarioConfig config = scenarios.get(i);
            int vusForScenario = Math.max(1,
                    (config.weight() * totalVus) / totalWeight);

            sb.append("    ").append(config.name()).append(": {\n");
            sb.append(loadProfile.toK6ScenarioProperties(vusForScenario,
                    duration, "      "));
            sb.append("      exec: '").append(config.name())
                    .append("Scenario',\n");
            sb.append("    }");
            if (i < scenarios.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  },\n");
        sb.append("};\n\n");

        // Extract and add each scenario function
        for (ScenarioConfig config : scenarios) {
            String scenarioCode = extractScenarioFunction(config.testFile,
                    config.name(),
                    sharedArrayBlocks.containsKey(config.name()));
            sb.append(scenarioCode).append("\n\n");
        }

        // Summary handler for per-request metrics in JSON export
        sb.append(HarToK6Converter.HANDLE_SUMMARY_FUNCTION);

        // Write combined file
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, sb.toString());
    }

    /**
     * Extracts the default function body from a k6 test and wraps it as a named
     * function.
     *
     * @param testFile
     *            the scenario test file
     * @param scenarioName
     *            the scenario name (used for the exported function name)
     * @param hasCsvData
     *            if true, renames inputData references to
     *            {scenarioName}InputData
     * @return the extracted and renamed function as a string
     * @throws IOException
     *             if reading the test file fails
     */
    private String extractScenarioFunction(Path testFile, String scenarioName,
            boolean hasCsvData) throws IOException {
        String content = Files.readString(testFile);

        // Find the default function body
        Pattern pattern = Pattern.compile(
                "export\\s+default\\s+function\\s*\\([^)]*\\)\\s*\\{",
                Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            throw new IOException(
                    "Could not find 'export default function' in " + testFile);
        }

        int functionStart = matcher.end();
        // matcher.end() points one past the opening `{`. Step back to it so
        // the helper can use a uniform "index of opening brace" contract.
        int functionEnd = findMatchingClosingBrace(content, functionStart - 1);
        // functionEnd points one past the closing `}` — drop it so the wrapper
        // below can re-add a single `}` of its own.
        String functionBody = content.substring(functionStart, functionEnd - 1);

        // Rename inputData references to scenario-specific name for combined
        // scripts
        if (hasCsvData) {
            functionBody = functionBody.replace("inputData",
                    scenarioName + "InputData");
        }

        // Create named export function
        StringBuilder sb = new StringBuilder();
        sb.append("// Scenario: ").append(scenarioName).append("\n");
        sb.append(
                "// Weight-based VU distribution - runs in parallel with other scenarios\n");
        sb.append("export function ").append(scenarioName)
                .append("Scenario() {");
        sb.append(functionBody);
        sb.append("}");

        return sb.toString();
    }

    /**
     * Extracts the parseCsvRecords function from a k6 script, if present.
     *
     * @param content
     *            the k6 script content
     * @return the parseCsvRecords function definition, or {@code null} if not
     *         found
     */
    private String extractCsvParserFunction(String content) {
        String funcMarker = "function parseCsvRecords(text) {";
        int funcStart = content.indexOf(funcMarker);
        if (funcStart < 0) {
            return null;
        }

        // Include the preceding comment line
        int lineStart = content.lastIndexOf('\n', funcStart - 1);
        if (lineStart >= 0) {
            String precedingLine = content.substring(lineStart + 1, funcStart)
                    .trim();
            if (precedingLine.startsWith("//")) {
                funcStart = lineStart + 1;
            }
        }

        // Find the matching closing brace of the function
        int braceStart = content.indexOf('{', funcStart);
        int afterClose = findMatchingClosingBrace(content, braceStart);
        return content.substring(funcStart, afterClose);
    }

    /**
     * Returns the index one past the closing `}` that matches the opening `{`
     * at {@code openBraceIndex}. Skips over single-line comments
     * ({@code // ...}), block comments ({@code /* ... *}{@code /}), and string
     * literals ({@code "..."}, {@code '...'}, {@code `...`}) so braces inside
     * them do not affect the depth count. A backslash escape outside a string
     * ({@code \{} or {@code \}} inside a regex literal) also consumes the
     * following character without counting it.
     *
     * <p>
     * This is a pragmatic shim — not a full JS tokenizer. It handles the shapes
     * that arise in generated k6 scripts and is robust to braces appearing in
     * comments, regex literals, and template strings.
     *
     * @param content
     *            the JS source to walk
     * @param openBraceIndex
     *            index of the opening {@code &#123;} whose match is sought
     * @return one past the matching closing brace, or {@code content.length()}
     *         if no match is found before end-of-input
     */
    private static int findMatchingClosingBrace(String content,
            int openBraceIndex) {
        int n = content.length();
        int i = openBraceIndex + 1;
        int depth = 1;
        while (i < n && depth > 0) {
            char c = content.charAt(i);
            // Single-line comment — skip to end of line so braces inside
            // human-written comments are ignored.
            if (c == '/' && i + 1 < n && content.charAt(i + 1) == '/') {
                int eol = content.indexOf('\n', i + 2);
                i = (eol < 0) ? n : eol;
                continue;
            }
            // Block comment.
            if (c == '/' && i + 1 < n && content.charAt(i + 1) == '*') {
                int end = content.indexOf("*/", i + 2);
                i = (end < 0) ? n : end + 2;
                continue;
            }
            // String literals (single, double, backtick). Backtick template
            // strings are treated as opaque — `${...}` interpolation has
            // matched braces, so the depth net-zero across the literal.
            if (c == '\'' || c == '"' || c == '`') {
                char quote = c;
                i++;
                while (i < n) {
                    char d = content.charAt(i);
                    if (d == '\\' && i + 1 < n) {
                        i += 2;
                        continue;
                    }
                    if (d == quote) {
                        i++;
                        break;
                    }
                    // ' and " strings cannot span lines unescaped — bail out
                    // on a stray newline rather than consuming the rest of
                    // the file.
                    if (d == '\n' && quote != '`') {
                        break;
                    }
                    i++;
                }
                continue;
            }
            // Backslash escape outside a string (e.g. `\{` in a regex
            // literal). Consume the following character without counting it.
            if (c == '\\' && i + 1 < n) {
                i += 2;
                continue;
            }
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return i + 1;
                }
            }
            i++;
        }
        return i;
    }

    /**
     * Extracts a SharedArray block from a k6 script, if present. Returns the
     * full block including any preceding comment line, or null if not found.
     *
     * @param content
     *            the k6 script content
     * @return the SharedArray block, or {@code null} if not found
     */
    private String extractSharedArrayBlock(String content) {
        String marker = "const inputData = new SharedArray(";
        int blockStart = content.indexOf(marker);
        if (blockStart < 0) {
            return null;
        }

        // Include the preceding comment line (e.g., "// Input test data from
        // CSV ...")
        int lineStart = content.lastIndexOf('\n', blockStart - 1);
        if (lineStart >= 0) {
            String precedingLine = content.substring(lineStart + 1, blockStart)
                    .trim();
            if (precedingLine.startsWith("//")) {
                blockStart = lineStart + 1;
            }
        }

        // Find the end of the SharedArray(...) call using parenthesis counting
        int parenSearch = content.indexOf("SharedArray(", blockStart);
        int i = parenSearch + "SharedArray(".length();
        int parenCount = 1;
        while (i < content.length() && parenCount > 0) {
            char c = content.charAt(i);
            if (c == '(')
                parenCount++;
            else if (c == ')')
                parenCount--;
            i++;
        }

        return content.substring(blockStart, i);
    }

    /**
     * Creates scenario configurations from test files with equal weights.
     *
     * @param testFiles
     *            the test files to create scenarios for
     * @return list of scenario configurations with evenly distributed weights
     */
    public static List<ScenarioConfig> equalWeights(List<Path> testFiles) {
        int weight = 100 / testFiles.size();
        List<ScenarioConfig> configs = new ArrayList<>();
        for (Path file : testFiles) {
            String name = fileToScenarioName(file);
            configs.add(new ScenarioConfig(name, file, weight));
        }
        return configs;
    }

    /**
     * Converts a file name to a valid JavaScript function name. E.g.,
     * "hello-world.js" -> "helloWorld"
     *
     * @param file
     *            the test file path
     * @return the camelCase scenario name
     */
    private static String fileToScenarioName(Path file) {
        String name = file.getFileName().toString().replaceAll("\\.js$", "")
                .replaceAll("-generated$", "");

        // Convert kebab-case to camelCase
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        for (char c : name.toCharArray()) {
            if (c == '-' || c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
