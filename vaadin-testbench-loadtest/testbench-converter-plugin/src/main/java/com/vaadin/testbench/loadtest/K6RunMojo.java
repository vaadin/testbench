package com.vaadin.testbench.loadtest;

import com.vaadin.testbench.loadtest.util.ActuatorMetrics;
import com.vaadin.testbench.loadtest.util.ActuatorMetrics.MetricsSummary;
import com.vaadin.testbench.loadtest.util.K6ScenarioCombiner;
import com.vaadin.testbench.loadtest.util.K6ScenarioCombiner.ScenarioConfig;
import com.vaadin.testbench.loadtest.util.MetricsCollector;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Runs k6 load tests.
 * <p>
 * This goal executes one or more k6 test files with configurable virtual users,
 * duration, and target application settings.
 * <p>
 * By default, tests are run sequentially. Use {@code combineScenarios=true} to
 * run all scenarios in parallel with weighted VU distribution.
 * <p>
 * Example usage:
 * <pre>
 * // Run tests sequentially
 * mvn k6:run -Dk6.testDir=k6/tests -Dk6.vus=50 -Dk6.duration=1m
 *
 * // Run tests in parallel with equal weights
 * mvn k6:run -Dk6.testDir=k6/tests -Dk6.combineScenarios=true -Dk6.vus=50
 *
 * // Run tests in parallel with custom weights (70% hello-world, 30% crud-example)
 * mvn k6:run -Dk6.testDir=k6/tests -Dk6.combineScenarios=true \
 *     -Dk6.scenarioWeights="helloWorld:70,crudExample:30"
 * </pre>
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class K6RunMojo extends AbstractK6Mojo {

    /**
     * The k6 test file to run. For multiple files, use testFiles or testDir.
     */
    @Parameter(property = "k6.testFile")
    private File testFile;

    /**
     * List of k6 test files to run. Each file is run sequentially.
     */
    @Parameter(property = "k6.testFiles")
    private List<File> testFiles;

    /**
     * Directory containing k6 test files. All .js files (excluding helpers) will be run.
     */
    @Parameter(property = "k6.testDir")
    private File testDir;

    /**
     * Number of virtual users (concurrent users).
     */
    @Parameter(property = "k6.vus", defaultValue = "10")
    private int virtualUsers;

    /**
     * Test duration (e.g., "30s", "1m", "5m").
     */
    @Parameter(property = "k6.duration", defaultValue = "30s")
    private String duration;

    /**
     * Application IP address.
     */
    @Parameter(property = "k6.appIp", defaultValue = "localhost")
    private String appIp;

    /**
     * Application port.
     */
    @Parameter(property = "k6.appPort", defaultValue = "8080")
    private int appPort;

    /**
     * Spring Boot Actuator management port.
     * If the target application has Spring Boot Actuator enabled, metrics
     * (CPU usage, heap memory) will be fetched and reported after the load test.
     * Set to -1 to disable metrics collection.
     *
     * @see ActuatorMetrics
     */
    @Parameter(property = "k6.managementPort", defaultValue = "8082")
    private int managementPort;

    /**
     * Enable collection of Vaadin-specific metrics from custom VaadinActuator endpoints.
     * This requires the target application to implement VaadinActuator or equivalent
     * that exposes Vaadin view metrics via Spring Boot Actuator.
     *
     * @see ActuatorMetrics
     */
    @Parameter(property = "k6.collectVaadinMetrics", defaultValue = "false")
    private boolean collectVaadinMetrics;

    /**
     * Interval in seconds for collecting server metrics during the load test.
     * Metrics are collected periodically and displayed as a time-series table
     * after the test completes. Set to 0 to collect only a single snapshot at the end.
     */
    @Parameter(property = "k6.metricsInterval", defaultValue = "10")
    private int metricsInterval;

    /**
     * Fail the build if any k6 thresholds are breached.
     */
    @Parameter(property = "k6.failOnThreshold", defaultValue = "true")
    private boolean failOnThreshold;

    /**
     * When true, combines all test files into a single k6 test with parallel scenarios.
     * Each scenario runs with its own VU pool based on the configured weights.
     * When false (default), tests are run sequentially.
     */
    @Parameter(property = "k6.combineScenarios", defaultValue = "false")
    private boolean combineScenarios;

    /**
     * Scenario weights for combined execution. Format: "scenarioName:weight,scenarioName:weight"
     * Scenario names are derived from file names (hello-world.js -> helloWorld).
     * If not specified, scenarios are weighted equally.
     * Example: "helloWorld:70,crudExample:30"
     */
    @Parameter(property = "k6.scenarioWeights")
    private String scenarioWeights;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping k6:run");
            return;
        }

        // Build list of test files to run
        List<Path> filesToRun = getTestFilesToRun();
        if (filesToRun.isEmpty()) {
            throw new MojoExecutionException("No test files specified. Use testFile, testFiles, or testDir parameter.");
        }

        // Initialize (extract utilities)
        initialize();

        // Validate k6 is available
        if (!nodeRunner.isK6Available()) {
            throw new MojoExecutionException(
                    "k6 is required but not found. Please install k6: https://grafana.com/docs/k6/latest/get-started/installation/");
        }

        // Start metrics collection if enabled
        MetricsCollector metricsCollector = null;
        if (managementPort >= 0 && metricsInterval > 0) {
            ActuatorMetrics actuator = new ActuatorMetrics(appIp, managementPort, collectVaadinMetrics);
            metricsCollector = new MetricsCollector(actuator, metricsInterval);
            // Collect baseline before k6 starts to capture pre-test server state
            metricsCollector.collectBaseline();
            metricsCollector.start();
        }

        try {
            if (combineScenarios && filesToRun.size() > 1) {
                runCombinedScenarios(filesToRun);
            } else {
                runSequentialTests(filesToRun);
            }
        } finally {
            // Stop metrics collection and report
            if (metricsCollector != null) {
                metricsCollector.stop();
                metricsCollector.printReport();
            } else {
                // Fall back to single snapshot if periodic collection is disabled
                reportActuatorMetrics();
            }
        }
    }

    /**
     * Fetches and reports server metrics from Spring Boot Actuator.
     * Silently skips if actuator is not available or disabled.
     */
    private void reportActuatorMetrics() {
        if (managementPort < 0) {
            getLog().debug("Actuator metrics collection disabled (managementPort < 0)");
            return;
        }

        ActuatorMetrics actuator = new ActuatorMetrics(appIp, managementPort, collectVaadinMetrics);
        Optional<MetricsSummary> metrics = actuator.fetchMetrics();

        if (metrics.isPresent()) {
            getLog().info("");
            getLog().info("========================================");
            getLog().info(metrics.get().toString());
            getLog().info("========================================");
        } else {
            getLog().debug("Actuator metrics not available at " + appIp + ":" + managementPort);
        }
    }

    /**
     * Runs all scenarios in parallel using k6's scenario feature.
     */
    private void runCombinedScenarios(List<Path> testFiles) throws MojoExecutionException {
        getLog().info("Running " + testFiles.size() + " scenarios in parallel (combined mode)");
        getLog().info("========================================");
        getLog().info("  Total virtual users: " + virtualUsers);
        getLog().info("  Duration: " + duration);
        getLog().info("  Target: http://" + appIp + ":" + appPort);

        // Parse weights
        Map<String, Integer> weights = parseScenarioWeights();
        List<ScenarioConfig> scenarios = new ArrayList<>();

        for (Path testFile : testFiles) {
            String scenarioName = fileToScenarioName(testFile);
            int weight = weights.getOrDefault(scenarioName, 100 / testFiles.size());
            scenarios.add(new ScenarioConfig(scenarioName, testFile, weight));
            getLog().info("  Scenario: " + scenarioName + " (weight: " + weight + "%)");
        }
        getLog().info("========================================");
        getLog().info("");

        // Generate combined test file
        Path combinedFile = testFiles.get(0).getParent().resolve("combined-scenarios.js");
        try {
            K6ScenarioCombiner combiner = new K6ScenarioCombiner();
            combiner.combine(scenarios, combinedFile, virtualUsers, duration, buildThresholdConfig());
            getLog().info("Generated combined test: " + combinedFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to generate combined test file", e);
        }

        // Run the combined test (VUs and duration are embedded in the file, don't override)
        try {
            nodeRunner.runK6Test(combinedFile, virtualUsers, duration, appIp, appPort, true);
            getLog().info("");
            getLog().info("========================================");
            getLog().info("Combined scenario test completed successfully");
            getLog().info("========================================");
        } catch (MojoExecutionException e) {
            if (failOnThreshold) {
                throw e;
            } else {
                getLog().warn("Combined test failed but failOnThreshold is false");
            }
        }
    }

    /**
     * Runs tests sequentially (original behavior).
     */
    private void runSequentialTests(List<Path> filesToRun) throws MojoExecutionException {
        getLog().info("Running " + filesToRun.size() + " k6 load test(s) sequentially");
        getLog().info("========================================");
        getLog().info("  Virtual users: " + virtualUsers);
        getLog().info("  Duration: " + duration);
        getLog().info("  Target: http://" + appIp + ":" + appPort);
        getLog().info("  Tests: ");
        for (Path test : filesToRun) {
            getLog().info("    - " + test.getFileName());
        }
        getLog().info("========================================");
        getLog().info("");

        int passed = 0;
        int failed = 0;

        for (Path testPath : filesToRun) {
            getLog().info("");
            getLog().info("----------------------------------------");
            getLog().info("Running: " + testPath.getFileName());
            getLog().info("----------------------------------------");

            try {
                nodeRunner.runK6Test(testPath, virtualUsers, duration, appIp, appPort);
                passed++;
                getLog().info("Test passed: " + testPath.getFileName());
            } catch (MojoExecutionException e) {
                failed++;
                if (failOnThreshold) {
                    getLog().error("Test failed: " + testPath.getFileName());
                    throw e;
                } else {
                    getLog().warn("Test failed but failOnThreshold is false: " + testPath.getFileName());
                }
            }
        }

        getLog().info("");
        getLog().info("========================================");
        getLog().info("k6 load test summary: " + passed + " passed, " + failed + " failed");
        getLog().info("========================================");
    }

    /**
     * Parses scenario weights from the scenarioWeights parameter.
     * Format: "scenarioName:weight,scenarioName:weight"
     */
    private Map<String, Integer> parseScenarioWeights() {
        Map<String, Integer> weights = new HashMap<>();
        if (scenarioWeights != null && !scenarioWeights.isEmpty()) {
            for (String entry : scenarioWeights.split(",")) {
                String[] parts = entry.trim().split(":");
                if (parts.length == 2) {
                    weights.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                }
            }
        }
        return weights;
    }

    /**
     * Converts a file name to a valid JavaScript function name.
     * E.g., "hello-world.js" -> "helloWorld"
     */
    private String fileToScenarioName(Path file) {
        String name = file.getFileName().toString()
                .replaceAll("\\.js$", "")
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

    /**
     * Builds the list of test files to run from configuration.
     */
    private List<Path> getTestFilesToRun() throws MojoExecutionException {
        List<Path> result = new ArrayList<>();

        // Add from testFiles list
        if (testFiles != null && !testFiles.isEmpty()) {
            for (File f : testFiles) {
                Path path = f.toPath().toAbsolutePath();
                if (!Files.exists(path)) {
                    throw new MojoExecutionException("Test file not found: " + path);
                }
                result.add(path);
            }
        }

        // Add single testFile if specified
        if (testFile != null) {
            Path path = testFile.toPath().toAbsolutePath();
            if (!Files.exists(path)) {
                throw new MojoExecutionException("Test file not found: " + path);
            }
            if (!result.contains(path)) {
                result.add(path);
            }
        }

        // Add all .js files from testDir (excluding helpers, generated, and combined)
        if (testDir != null && testDir.exists() && testDir.isDirectory()) {
            try (Stream<Path> files = Files.list(testDir.toPath())) {
                files.filter(p -> p.toString().endsWith(".js"))
                     .filter(p -> !p.getFileName().toString().contains("helper"))
                     .filter(p -> !p.getFileName().toString().contains("-generated"))
                     .filter(p -> !p.getFileName().toString().equals("combined-scenarios.js"))
                     .sorted()
                     .forEach(p -> {
                         if (!result.contains(p.toAbsolutePath())) {
                             result.add(p.toAbsolutePath());
                         }
                     });
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to list test directory: " + testDir, e);
            }
        }

        return result;
    }
}
