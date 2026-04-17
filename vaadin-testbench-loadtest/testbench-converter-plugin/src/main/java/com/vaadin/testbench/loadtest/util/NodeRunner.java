/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.maven.plugin.MojoExecutionException;

import com.vaadin.testbench.loadtest.report.SummaryHtmlReport;

/**
 * Manages process execution for the plugin. Now uses Java implementations for
 * HAR filtering, k6 conversion, and proxy recording. Only k6 execution still
 * requires an external process.
 */
public class NodeRunner {

    private static final Logger log = Logger
            .getLogger(NodeRunner.class.getName());

    private final Path workingDirectory;
    private ProxyRecorder proxyRecorder;
    private String summaryTrendStats = "avg,min,med,max,p(95),p(99)";
    private Path reportDir;

    /**
     * Creates a new node runner for the given working directory.
     *
     * @param workingDirectory
     *            the directory to run node/k6 commands in
     */
    public NodeRunner(Path workingDirectory) {
        ExperimentalWarning.log();
        this.workingDirectory = workingDirectory;
    }

    /**
     * Sets the summary trend stats for k6's {@code --summary-trend-stats} flag.
     * Derived from {@link ThresholdConfig#toSummaryTrendStats()}.
     *
     * @param summaryTrendStats
     *            comma-separated stats (e.g. "avg,min,med,max,p(95),p(99)")
     */
    public void setSummaryTrendStats(String summaryTrendStats) {
        this.summaryTrendStats = summaryTrendStats;
    }

    /**
     * Sets the directory where report files (JSON summary, HTML report) are
     * written. Created automatically if it does not exist.
     *
     * @param reportDir
     *            the report output directory
     */
    public void setReportDir(Path reportDir) {
        this.reportDir = reportDir;
    }

    /**
     * Checks if k6 is available on the system.
     *
     * @return true if k6 is installed and accessible
     */
    public boolean isK6Available() {
        try {
            ProcessBuilder pb = new ProcessBuilder("k6", "version");
            Process process = pb.start();
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (finished && process.exitValue() == 0) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String version = reader.readLine();
                    log.fine("k6 version: " + version);
                }
                return true;
            }
        } catch (IOException | InterruptedException e) {
            log.fine("k6 check failed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Starts the proxy recorder in the background.
     *
     * @param proxyPort
     *            the port for the proxy to listen on
     * @param harFile
     *            the output HAR file path
     * @throws MojoExecutionException
     *             if starting the proxy fails
     */
    public void startProxyRecorder(int proxyPort, Path harFile)
            throws MojoExecutionException {
        log.info("Starting proxy recorder on port " + proxyPort + "...");
        try {
            proxyRecorder = new ProxyRecorder();
            proxyRecorder.start(proxyPort, harFile);
            log.info("Proxy recorder started");
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to start proxy recorder",
                    e);
        }
    }

    /**
     * Stops the proxy recorder gracefully.
     */
    public void stopProxyRecorder() {
        if (proxyRecorder != null && proxyRecorder.isRunning()) {
            log.info("Stopping proxy recorder...");
            try {
                proxyRecorder.stop();
                log.info("Proxy recorder stopped");
            } catch (IOException e) {
                log.warning("Error stopping proxy recorder: " + e.getMessage());
            }
            proxyRecorder = null;
        }
    }

    /**
     * Gets the number of requests recorded by the proxy.
     *
     * @return the number of recorded HAR entries, or 0 if not recording
     */
    public int getRecordedEntryCount() {
        if (proxyRecorder != null) {
            return proxyRecorder.getRecordedEntryCount();
        }
        return 0;
    }

    /**
     * Runs the HAR filter to remove external domain requests.
     *
     * @param harFile
     *            the HAR file to filter
     * @throws MojoExecutionException
     *             if filtering fails
     */
    public void filterHar(Path harFile) throws MojoExecutionException {
        log.info("Filtering external domains from HAR file...");
        try {
            HarFilter filter = new HarFilter();
            filter.filter(harFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to filter HAR file", e);
        }
    }

    /**
     * Converts a HAR file to a k6 test with default thresholds.
     *
     * @param harFile
     *            the input HAR file
     * @param outputFile
     *            the output k6 test file
     * @throws MojoExecutionException
     *             if conversion fails
     */
    public void harToK6(Path harFile, Path outputFile)
            throws MojoExecutionException {
        harToK6(harFile, outputFile, ThresholdConfig.DEFAULT);
    }

    /**
     * Converts a HAR file to a k6 test with configurable thresholds.
     *
     * @param harFile
     *            the input HAR file
     * @param outputFile
     *            the output k6 test file
     * @param thresholdConfig
     *            threshold configuration for the generated script
     * @throws MojoExecutionException
     *             if conversion fails
     */
    public void harToK6(Path harFile, Path outputFile,
            ThresholdConfig thresholdConfig) throws MojoExecutionException {
        harToK6(harFile, outputFile, thresholdConfig,
                ResponseCheckConfig.EMPTY);
    }

    /**
     * Converts a HAR file to a k6 test with configurable thresholds and custom
     * response checks.
     *
     * @param harFile
     *            the input HAR file
     * @param outputFile
     *            the output k6 test file
     * @param thresholdConfig
     *            threshold configuration for the generated script
     * @param responseCheckConfig
     *            custom response validation checks to inject
     * @throws MojoExecutionException
     *             if conversion fails
     */
    public void harToK6(Path harFile, Path outputFile,
            ThresholdConfig thresholdConfig,
            ResponseCheckConfig responseCheckConfig)
            throws MojoExecutionException {
        log.info("Converting HAR to k6 test...");
        try {
            HarToK6Converter converter = new HarToK6Converter();
            converter.convert(harFile, outputFile, thresholdConfig,
                    responseCheckConfig);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to convert HAR to k6", e);
        }
    }

    /**
     * Refactors a k6 test for Vaadin compatibility with default think time
     * settings.
     *
     * @param inputFile
     *            the input k6 test file
     * @param outputFile
     *            the output refactored test file
     * @throws MojoExecutionException
     *             if refactoring fails
     */
    public void refactorK6Test(Path inputFile, Path outputFile)
            throws MojoExecutionException {
        refactorK6Test(inputFile, outputFile,
                K6TestRefactorer.ThinkTimeConfig.DEFAULT);
    }

    /**
     * Refactors a k6 test for Vaadin compatibility with custom think time
     * settings.
     *
     * @param inputFile
     *            the input k6 test file
     * @param outputFile
     *            the output refactored test file
     * @param thinkTimeConfig
     *            think time configuration for realistic user simulation
     * @throws MojoExecutionException
     *             if refactoring fails
     */
    public void refactorK6Test(Path inputFile, Path outputFile,
            K6TestRefactorer.ThinkTimeConfig thinkTimeConfig)
            throws MojoExecutionException {
        log.info("Refactoring k6 test for Vaadin...");
        try {
            K6TestRefactorer refactorer = new K6TestRefactorer(thinkTimeConfig);
            refactorer.refactor(inputFile, outputFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to refactor k6 test", e);
        }
    }

    /**
     * Runs a k6 load test with constant load (no ramping).
     *
     * @param testFile
     *            the k6 test file to run
     * @param virtualUsers
     *            number of virtual users
     * @param duration
     *            test duration (e.g., "30s", "1m")
     * @param appIp
     *            application IP address
     * @param appPort
     *            application port
     * @throws MojoExecutionException
     *             if the test fails
     */
    public void runK6Test(Path testFile, int virtualUsers, String duration,
            String appIp, int appPort) throws MojoExecutionException {
        runK6Test(testFile, virtualUsers, duration, appIp, appPort, false);
    }

    /**
     * Runs a k6 load test with a configurable load profile. For ramping
     * profiles, uses k6's {@code --stage} CLI flags. For constant profiles,
     * uses {@code --vus} and {@code --duration}.
     *
     * @param testFile
     *            the k6 test file to run
     * @param virtualUsers
     *            number of virtual users
     * @param duration
     *            test duration (e.g., "30s", "1m")
     * @param appIp
     *            application IP address
     * @param appPort
     *            application port
     * @param loadProfile
     *            load profile controlling ramping behavior
     * @throws MojoExecutionException
     *             if the test fails
     */
    public void runK6Test(Path testFile, int virtualUsers, String duration,
            String appIp, int appPort, LoadProfile loadProfile)
            throws MojoExecutionException {
        log.info("Running k6 load test: " + testFile.getFileName());
        log.info("  Load pattern: " + loadProfile);
        log.info("  Target: " + appIp + ":" + appPort);

        // For executors that cannot be configured via CLI flags, generate a
        // wrapper script with embedded scenario configuration
        if (loadProfile.requiresEmbeddedConfig()) {
            runWithEmbeddedConfig(testFile, virtualUsers, duration, appIp,
                    appPort, loadProfile);
            return;
        }

        try {
            List<String> command = new ArrayList<>();
            command.add("k6");
            command.add("run");

            if (loadProfile.isRamping()) {
                // Use --stage flags for ramping-vus
                List<LoadProfile.Stage> stages = loadProfile
                        .toStages(virtualUsers, duration);
                for (LoadProfile.Stage stage : stages) {
                    command.add("--stage");
                    command.add(stage.duration() + ":" + stage.target());
                }
                log.info("  Stages: ");
                for (LoadProfile.Stage stage : stages) {
                    log.info("    " + stage.duration() + " → " + stage.target()
                            + " VUs");
                }
            } else {
                // Constant load
                command.add("--vus");
                command.add(String.valueOf(virtualUsers));
                command.add("--duration");
                command.add(duration);
                log.info("  Virtual Users: " + virtualUsers);
                log.info("  Duration: " + duration);
            }

            // Summary trend stats and file path via env var (handleSummary
            // in the script writes the JSON)
            Path summaryFile = resolveSummaryFile(testFile);
            Path csvFile = testFile.getParent().resolve(testFile.getFileName()
                    .toString().replaceAll("\\.js$", "-metrics.csv"));
            command.add("--summary-trend-stats");
            command.add(summaryTrendStats);
            command.add("-e");
            command.add("SUMMARY_FILE=" + summaryFile.toAbsolutePath());
            command.add("--out");
            command.add("csv=" + csvFile.toAbsolutePath());

            command.add("-e");
            command.add("APP_IP=" + appIp);
            command.add("-e");
            command.add("APP_PORT=" + String.valueOf(appPort));
            command.add(testFile.toAbsolutePath().toString());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();

            Process process = pb.start();

            int exitCode = process.waitFor();
            if (Files.exists(summaryFile)) {
                log.info("Summary exported to: " + summaryFile);
                injectCsvMetrics(csvFile, summaryFile);
                SummaryHtmlReport.generate(summaryFile);
            }
            cleanUpTempFile(csvFile);
            if (exitCode != 0) {
                throw new MojoExecutionException(
                        "k6 test failed with exit code: " + exitCode);
            }
            log.info("k6 test completed successfully");
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Failed to run k6 test", e);
        }
    }

    /**
     * Runs a k6 test using a generated wrapper script with embedded scenario
     * configuration. Used for executors that cannot be fully configured via k6
     * CLI flags (e.g., arrival-rate, iteration-based, externally-controlled).
     *
     * <p>
     * The wrapper imports the original test's default function and defines
     * {@code export const options} with the scenario configuration from the
     * load profile.
     */
    private void runWithEmbeddedConfig(Path testFile, int virtualUsers,
            String duration, String appIp, int appPort, LoadProfile loadProfile)
            throws MojoExecutionException {
        Path wrapperFile = testFile.getParent()
                .resolve("wrapper-" + testFile.getFileName());
        try {
            String relativePath = "./" + testFile.getFileName().toString();
            StringBuilder sb = new StringBuilder();
            sb.append(
                    "// Auto-generated wrapper for embedded executor config\n");
            sb.append("import originalTest from '").append(relativePath)
                    .append("';\n");
            sb.append(
                    "import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';\n\n");
            sb.append("export const options = {\n");
            sb.append("  scenarios: {\n");
            sb.append("    default: {\n");
            sb.append(loadProfile.toK6ScenarioProperties(virtualUsers, duration,
                    "      "));
            sb.append("      exec: 'runTest',\n");
            sb.append("    },\n");
            sb.append("  },\n");
            sb.append("};\n\n");
            sb.append("export function runTest() {\n");
            sb.append("  originalTest();\n");
            sb.append("}\n");
            sb.append(HarToK6Converter.HANDLE_SUMMARY_FUNCTION);

            Files.writeString(wrapperFile, sb.toString());
            log.info("  Generated wrapper: " + wrapperFile.getFileName());

            // Run the wrapper with embedded config
            runK6Test(wrapperFile, virtualUsers, duration, appIp, appPort,
                    true);
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to generate wrapper script", e);
        } finally {
            // Clean up wrapper file
            try {
                Files.deleteIfExists(wrapperFile);
            } catch (IOException e) {
                log.warning("Could not delete wrapper file: " + wrapperFile);
            }
        }
    }

    /**
     * Runs a k6 load test.
     *
     * @param testFile
     *            the k6 test file to run
     * @param virtualUsers
     *            number of virtual users
     * @param duration
     *            test duration (e.g., "30s", "1m")
     * @param appIp
     *            application IP address
     * @param appPort
     *            application port
     * @param useEmbeddedConfig
     *            if true, don't pass --vus and --duration (use script's
     *            embedded config)
     * @throws MojoExecutionException
     *             if the test fails
     */
    public void runK6Test(Path testFile, int virtualUsers, String duration,
            String appIp, int appPort, boolean useEmbeddedConfig)
            throws MojoExecutionException {
        log.info("Running k6 load test: " + testFile.getFileName());
        if (!useEmbeddedConfig) {
            log.info("  Virtual Users: " + virtualUsers);
            log.info("  Duration: " + duration);
        } else {
            log.info("  Using embedded scenario configuration");
        }
        log.info("  Target: " + appIp + ":" + appPort);

        try {
            List<String> command = new ArrayList<>();
            command.add("k6");
            command.add("run");

            // Only add VUs and duration if not using embedded config
            if (!useEmbeddedConfig) {
                command.add("--vus");
                command.add(String.valueOf(virtualUsers));
                command.add("--duration");
                command.add(duration);
            }

            // Summary trend stats and file path via env var (handleSummary
            // in the script writes the JSON)
            Path summaryFile = resolveSummaryFile(testFile);
            Path csvFile = testFile.getParent().resolve(testFile.getFileName()
                    .toString().replaceAll("\\.js$", "-metrics.csv"));
            command.add("--summary-trend-stats");
            command.add(summaryTrendStats);
            command.add("-e");
            command.add("SUMMARY_FILE=" + summaryFile.toAbsolutePath());
            command.add("--out");
            command.add("csv=" + csvFile.toAbsolutePath());

            // Always pass environment variables for target server
            command.add("-e");
            command.add("APP_IP=" + appIp);
            command.add("-e");
            command.add("APP_PORT=" + String.valueOf(appPort));
            command.add(testFile.toAbsolutePath().toString());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO(); // Pass through stdin/stdout/stderr for live output

            Process process = pb.start();

            int exitCode = process.waitFor();
            if (Files.exists(summaryFile)) {
                log.info("Summary exported to: " + summaryFile);
                injectCsvMetrics(csvFile, summaryFile);
                SummaryHtmlReport.generate(summaryFile);
            }
            cleanUpTempFile(csvFile);
            if (exitCode != 0) {
                throw new MojoExecutionException(
                        "k6 test failed with exit code: " + exitCode);
            }
            log.info("k6 test completed successfully");
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Failed to run k6 test", e);
        }
    }

    /**
     * Resolves the summary JSON path. Uses reportDir if set, otherwise places
     * the file next to the test file.
     */
    private Path resolveSummaryFile(Path testFile) {
        String baseName = testFile.getFileName().toString().replaceAll("\\.js$",
                "-summary.json");
        if (reportDir != null) {
            try {
                Files.createDirectories(reportDir);
            } catch (IOException e) {
                log.warning("Could not create report dir: " + reportDir);
            }
            return reportDir.resolve(baseName);
        }
        return testFile.getParent().resolve(baseName);
    }

    /**
     * Extracts runtime metrics from the k6 CSV output and injects them into the
     * summary JSON:
     * <ul>
     * <li>{@code vu_timeline} — VU count per second</li>
     * <li>{@code request_timeline} — per-second buckets with request count,
     * pass/fail counts, and avg/max response time</li>
     * </ul>
     *
     * @param csvFile
     *            the k6 CSV metrics output file
     * @param summaryFile
     *            the summary JSON file to inject the data into
     */
    private void injectCsvMetrics(Path csvFile, Path summaryFile) {
        if (!Files.exists(csvFile)) {
            log.fine("No CSV metrics file found: " + csvFile);
            return;
        }
        try {
            // k6 CSV header: metric_name,timestamp,metric_value,...
            StringBuilder vuJson = new StringBuilder("[");
            boolean hasVu = false;
            long firstTimestamp = -1;

            // Per-second request buckets: elapsed -> {count, failed,
            // sumDuration, maxDuration}
            var buckets = new java.util.TreeMap<Long, long[]>();

            try (BufferedReader reader = Files.newBufferedReader(csvFile)) {
                String line;
                int metricCol = -1, tsCol = -1, valueCol = -1;
                while ((line = reader.readLine()) != null) {
                    String[] cols = line.split(",");
                    if (metricCol < 0) {
                        for (int i = 0; i < cols.length; i++) {
                            switch (cols[i].trim()) {
                            case "metric_name" -> metricCol = i;
                            case "timestamp" -> tsCol = i;
                            case "metric_value" -> valueCol = i;
                            }
                        }
                        continue;
                    }
                    if (metricCol >= cols.length || tsCol >= cols.length
                            || valueCol >= cols.length) {
                        continue;
                    }
                    String metric = cols[metricCol].trim();
                    long ts = Long.parseLong(cols[tsCol].trim());
                    double value = Double.parseDouble(cols[valueCol].trim());
                    if (firstTimestamp < 0) {
                        firstTimestamp = ts;
                    }
                    long elapsed = ts - firstTimestamp;

                    if ("vus".equals(metric)) {
                        if (hasVu)
                            vuJson.append(",");
                        vuJson.append("{\"elapsed\":").append(elapsed)
                                .append(",\"vus\":").append((int) value)
                                .append("}");
                        hasVu = true;
                    } else if ("http_req_duration".equals(metric)) {
                        // [count, failed, sumDuration(x100 for precision),
                        // maxDuration(x100)]
                        long[] b = buckets.computeIfAbsent(elapsed,
                                k -> new long[4]);
                        b[0]++;
                        b[2] += (long) (value * 100);
                        b[3] = Math.max(b[3], (long) (value * 100));
                    } else if ("http_req_failed".equals(metric) && value > 0) {
                        long[] b = buckets.computeIfAbsent(elapsed,
                                k -> new long[4]);
                        b[1]++;
                    }
                }
            }
            vuJson.append("]");

            // Build request timeline JSON
            StringBuilder reqJson = new StringBuilder("[");
            boolean firstBucket = true;
            for (var entry : buckets.entrySet()) {
                long[] b = entry.getValue();
                if (b[0] == 0)
                    continue;
                if (!firstBucket)
                    reqJson.append(",");
                double avg = (b[2] / 100.0) / b[0];
                double max = b[3] / 100.0;
                reqJson.append("{\"elapsed\":").append(entry.getKey())
                        .append(",\"count\":").append(b[0])
                        .append(",\"failed\":").append(b[1]).append(",\"avg\":")
                        .append(String.format(java.util.Locale.US, "%.1f", avg))
                        .append(",\"max\":")
                        .append(String.format(java.util.Locale.US, "%.1f", max))
                        .append("}");
                firstBucket = false;
            }
            reqJson.append("]");

            if (!hasVu && firstBucket) {
                log.fine("No runtime data points found in CSV");
                return;
            }

            // Inject into summary JSON before the closing brace
            String json = Files.readString(summaryFile);
            int lastBrace = json.lastIndexOf('}');
            if (lastBrace > 0) {
                StringBuilder extra = new StringBuilder();
                if (hasVu) {
                    extra.append(",\n  \"vu_timeline\": ").append(vuJson);
                }
                if (!firstBucket) {
                    extra.append(",\n  \"request_timeline\": ").append(reqJson);
                }
                String injected = json.substring(0, lastBrace) + extra + "\n}";
                Files.writeString(summaryFile, injected);
                log.info("Runtime metrics injected into summary");
            }
        } catch (IOException | NumberFormatException e) {
            log.warning("Failed to extract runtime metrics from CSV: "
                    + e.getMessage());
        }
    }

    /**
     * Deletes a temporary file, logging a warning on failure.
     */
    private void cleanUpTempFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warning("Could not delete temp file: " + file);
        }
    }

    // Deprecated methods - kept for backwards compatibility but no longer
    // needed

    /**
     * @deprecated Node.js is no longer required. This method always returns
     *             true.
     */
    @Deprecated
    public boolean isNodeAvailable() {
        return true;
    }

    /**
     * @deprecated npm is no longer required. This method always returns true.
     */
    @Deprecated
    public boolean isNpmAvailable() {
        return true;
    }

    /**
     * @deprecated npm dependencies are no longer required. This method is a
     *             no-op.
     */
    @Deprecated
    public void npmInstall() throws MojoExecutionException {
        // No-op - npm dependencies are no longer needed
    }

    /**
     * @deprecated npm dependencies are no longer required. This method always
     *             returns true.
     */
    @Deprecated
    public boolean areDependenciesInstalled() {
        return true;
    }
}
