/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.vaadin.testbench.loadtest.util.ActuatorMetrics;
import com.vaadin.testbench.loadtest.util.ActuatorMetrics.MetricsSummary;
import com.vaadin.testbench.loadtest.util.K6ScenarioCombiner;
import com.vaadin.testbench.loadtest.util.K6ScenarioCombiner.ScenarioConfig;
import com.vaadin.testbench.loadtest.util.LoadProfile;
import com.vaadin.testbench.loadtest.util.LoadProfile.K6Executor;
import com.vaadin.testbench.loadtest.util.LoadProfile.LoadPattern;
import com.vaadin.testbench.loadtest.util.MetricsCollector;

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
 * 
 * <pre>
 * // Run tests sequentially
 * mvn loadtest:run -Dk6.testDir=k6/tests -Dk6.vus=50 -Dk6.duration=1m
 *
 * // Run tests in parallel with equal weights
 * mvn loadtest:run -Dk6.testDir=k6/tests -Dk6.combineScenarios=true -Dk6.vus=50
 *
 * // Run tests in parallel with custom weights (70% hello-world, 30% crud-example)
 * mvn loadtest:run -Dk6.testDir=k6/tests -Dk6.combineScenarios=true \
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
     * Directory containing k6 test files. All .js files (excluding helpers)
     * will be run.
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
     * Spring Boot Actuator management port. If the target application has
     * Spring Boot Actuator enabled, metrics (CPU usage, heap memory) will be
     * fetched and reported after the load test. Set to -1 to disable metrics
     * collection.
     *
     * @see ActuatorMetrics
     */
    @Parameter(property = "k6.managementPort", defaultValue = "8082")
    private int managementPort;

    /**
     * Enable collection of Vaadin-specific metrics from custom VaadinActuator
     * endpoints. This requires the target application to implement
     * VaadinActuator or equivalent that exposes Vaadin view metrics via Spring
     * Boot Actuator.
     *
     * @see ActuatorMetrics
     */
    @Parameter(property = "k6.collectVaadinMetrics", defaultValue = "false")
    private boolean collectVaadinMetrics;

    /**
     * Interval in seconds for collecting server metrics during the load test.
     * Metrics are collected periodically and displayed as a time-series table
     * after the test completes. Set to 0 to collect only a single snapshot at
     * the end.
     */
    @Parameter(property = "k6.metricsInterval", defaultValue = "10")
    private int metricsInterval;

    /**
     * Fail the build if any k6 thresholds are breached.
     */
    @Parameter(property = "k6.failOnThreshold", defaultValue = "true")
    private boolean failOnThreshold;

    /**
     * When true, runs a single 1-VU / 1-iteration warmup pass before each load
     * test to prime caches, JIT, and class loaders on the target application.
     * The warmup runs the recorded scenario(s) once with
     * {@code --summary-mode=disabled --no-thresholds} so its metrics never
     * appear in reports and threshold violations during warmup never fail the
     * build.
     */
    @Parameter(property = "k6.warmup", defaultValue = "false")
    private boolean warmup;

    /**
     * When true, combines all test files into a single k6 test with parallel
     * scenarios. Each scenario runs with its own VU pool based on the
     * configured weights. When false (default), tests are run sequentially.
     */
    @Parameter(property = "k6.combineScenarios", defaultValue = "false")
    private boolean combineScenarios;

    /**
     * Scenario weights for combined execution. Format:
     * "scenarioName:weight,scenarioName:weight" Scenario names are derived from
     * file names (hello-world.js -> helloWorld). If not specified, scenarios
     * are weighted equally. Example: "helloWorld:70,crudExample:30"
     */
    @Parameter(property = "k6.scenarioWeights")
    private String scenarioWeights;

    /**
     * Load pattern controlling how virtual users are ramped up and down. Valid
     * values:
     * <ul>
     * <li>{@code constant} - all VUs start immediately, no ramping (k6
     * constant-vus executor)</li>
     * <li>{@code ramp} (default) - ramp up → sustain → ramp down (k6
     * ramping-vus executor)</li>
     * <li>{@code stress} - gradual increase with a 150% spike phase</li>
     * <li>{@code soak} - quick ramp, extended sustain for long-duration
     * tests</li>
     * <li>{@code custom} - user-defined stages via {@code k6.stages}</li>
     * </ul>
     *
     * @see LoadProfile.LoadPattern
     */
    @Parameter(property = "k6.loadPattern", defaultValue = "ramp")
    private String loadPattern;

    /**
     * Ramp-up duration for the {@code ramp} load pattern. During this period,
     * virtual users increase linearly from 0 to the target VU count. Ignored
     * for other load patterns.
     */
    @Parameter(property = "k6.rampUp", defaultValue = "10s")
    private String rampUp;

    /**
     * Ramp-down duration for the {@code ramp} load pattern. During this period,
     * virtual users decrease linearly from the target VU count to 0. Ignored
     * for other load patterns.
     */
    @Parameter(property = "k6.rampDown", defaultValue = "10s")
    private String rampDown;

    /**
     * Custom stage definitions for the {@code custom} load pattern. Format:
     * "duration:target,duration:target,..." where duration is a k6 time string
     * and target is the VU count at the end of that stage.
     * <p>
     * Example: "10s:20,1m:50,30s:50,15s:80,1m:80,30s:0"
     * <p>
     * When this parameter is set, the load pattern is automatically set to
     * {@code custom} and the {@code vus} and {@code duration} parameters are
     * ignored.
     */
    @Parameter(property = "k6.stages")
    private String stages;

    /**
     * Explicit k6 executor type, overriding the load pattern. Allows direct
     * selection of any k6 executor with full parameter control. Valid values:
     * {@code constant-vus}, {@code ramping-vus}, {@code per-vu-iterations},
     * {@code shared-iterations}, {@code constant-arrival-rate},
     * {@code ramping-arrival-rate}, {@code externally-controlled}.
     * <p>
     * When set, this takes precedence over {@code k6.loadPattern}.
     *
     * @see LoadProfile.K6Executor
     */
    @Parameter(property = "k6.executor")
    private String executor;

    /**
     * Iteration rate for arrival-rate executors ({@code constant-arrival-rate},
     * {@code ramping-arrival-rate}). Specifies how many iterations to start per
     * {@code timeUnit}.
     */
    @Parameter(property = "k6.rate")
    private Integer rate;

    /**
     * Time unit for arrival-rate executors (e.g., "1s", "1m"). Defaults to
     * "1s".
     */
    @Parameter(property = "k6.timeUnit", defaultValue = "1s")
    private String timeUnit;

    /**
     * Pre-allocated VUs for arrival-rate executors. k6 will pre-initialize this
     * many VUs at test start to avoid cold-start latency. Defaults to the
     * {@code vus} value if not specified.
     */
    @Parameter(property = "k6.preAllocatedVUs")
    private Integer preAllocatedVUs;

    /**
     * Maximum VUs for arrival-rate and externally-controlled executors. k6 will
     * not create more VUs than this, even if the arrival rate demands it.
     */
    @Parameter(property = "k6.maxVUs")
    private Integer maxVUs;

    /**
     * Number of iterations for iteration-based executors
     * ({@code per-vu-iterations}, {@code shared-iterations}).
     */
    @Parameter(property = "k6.iterations")
    private Integer iterations;

    /**
     * Starting iteration rate for the {@code ramping-arrival-rate} executor.
     * The rate will ramp from this value according to the configured stages.
     * Defaults to 0 if not specified.
     */
    @Parameter(property = "k6.startRate")
    private Integer startRate;

    /**
     * Fully custom k6 scenario definition as raw JavaScript. The content is
     * inserted directly into the k6 scenario definition block, giving full
     * control over all scenario properties.
     * <p>
     * When set, this takes precedence over all other load configuration
     * parameters ({@code loadPattern}, {@code executor}, etc.).
     * <p>
     * Example:
     *
     * <pre>
     * executor: 'ramping-arrival-rate',
     * startRate: 0,
     * timeUnit: '1s',
     * preAllocatedVUs: 50,
     * maxVUs: 100,
     * stages: [
     *   { duration: '1m', target: 100 },
     *   { duration: '2m', target: 100 },
     *   { duration: '1m', target: 0 },
     * ],
     * </pre>
     */
    @Parameter(property = "k6.customScenario")
    private String customScenario;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping k6:run");
            return;
        }

        // Build list of test files to run
        List<Path> filesToRun = getTestFilesToRun();
        if (filesToRun.isEmpty()) {
            throw new MojoExecutionException(
                    "No test files specified. Use testFile, testFiles, or testDir parameter.");
        }

        // Initialize (extract utilities)
        initialize();

        // Configure summary stats to match threshold percentiles
        nodeRunner.setSummaryTrendStats(
                buildThresholdConfig().toSummaryTrendStats());

        // Enable optional warmup pass (1 VU, 1 iteration) before each test
        nodeRunner.setWarmup(warmup);

        // Write reports (JSON, HTML) to {testDir}/report
        nodeRunner
                .setReportDir(filesToRun.get(0).getParent().resolve("report"));

        // Validate k6 is available
        if (!nodeRunner.isK6Available()) {
            throw new MojoExecutionException(
                    "k6 is required but not found. Please install k6: https://grafana.com/docs/k6/latest/get-started/installation/");
        }

        // Start metrics collection if enabled
        MetricsCollector metricsCollector = null;
        if (managementPort >= 0 && metricsInterval > 0) {
            ActuatorMetrics actuator = new ActuatorMetrics(appIp,
                    managementPort, collectVaadinMetrics);
            metricsCollector = new MetricsCollector(actuator, metricsInterval);
            // Collect baseline before k6 starts to capture pre-test server
            // state
            metricsCollector.collectBaseline();
            metricsCollector.start();
        }

        LoadProfile loadProfile = buildLoadProfile();

        try {
            if (combineScenarios && filesToRun.size() > 1) {
                runCombinedScenarios(filesToRun, loadProfile);
            } else {
                runSequentialTests(filesToRun, loadProfile);
            }
        } finally {
            // Stop metrics collection and report
            if (metricsCollector != null) {
                metricsCollector.stop();
                metricsCollector.printReport();
            } else {
                // Fall back to single snapshot if periodic collection is
                // disabled
                reportActuatorMetrics();
            }
        }
    }

    /**
     * Fetches and reports server metrics from Spring Boot Actuator. Silently
     * skips if actuator is not available or disabled.
     */
    private void reportActuatorMetrics() {
        if (managementPort < 0) {
            getLog().debug(
                    "Actuator metrics collection disabled (managementPort < 0)");
            return;
        }

        ActuatorMetrics actuator = new ActuatorMetrics(appIp, managementPort,
                collectVaadinMetrics);
        Optional<MetricsSummary> metrics = actuator.fetchMetrics();

        if (metrics.isPresent()) {
            getLog().info("");
            getLog().info(CONTENT_BREAK);
            getLog().info(metrics.get().toString());
            getLog().info(CONTENT_BREAK);
        } else {
            getLog().debug("Actuator metrics not available at " + appIp + ":"
                    + managementPort);
        }
    }

    /**
     * Runs all scenarios in parallel using k6's scenario feature.
     */
    private void runCombinedScenarios(List<Path> testFiles,
            LoadProfile loadProfile) throws MojoExecutionException {
        getLog().info("Running " + testFiles.size()
                + " scenarios in parallel (combined mode)");
        getLog().info(CONTENT_BREAK);
        getLog().info("  Total virtual users: " + virtualUsers);
        getLog().info("  Duration: " + duration);
        getLog().info("  Load pattern: " + loadProfile);
        getLog().info("  Target: http://" + appIp + ":" + appPort);

        // Parse weights
        Map<String, Integer> weights = parseScenarioWeights();
        List<ScenarioConfig> scenarios = new ArrayList<>();

        for (Path testFile : testFiles) {
            String scenarioName = fileToScenarioName(testFile);
            int weight = weights.getOrDefault(scenarioName,
                    100 / testFiles.size());
            scenarios.add(new ScenarioConfig(scenarioName, testFile, weight));
            getLog().info("  Scenario: " + scenarioName + " (weight: " + weight
                    + "%)");
        }
        getLog().info(CONTENT_BREAK);
        getLog().info("");

        // Generate combined test file
        Path combinedFile = testFiles.get(0).getParent()
                .resolve("combined-scenarios.js");
        try {
            K6ScenarioCombiner combiner = new K6ScenarioCombiner();
            combiner.combine(scenarios, combinedFile, virtualUsers, duration,
                    buildThresholdConfig(), loadProfile);
            getLog().info("Generated combined test: " + combinedFile);
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to generate combined test file", e);
        }

        // K6ScenarioCombiner unconditionally imports the Vaadin k6 helpers
        // via a `../utils/...` relative path; copy them next to the
        // generated script so k6 can resolve the import even when the input
        // tests do not need them.
        copyVaadinHelpers(combinedFile.getParent());

        // Run the combined test (VUs and duration are embedded in the file,
        // don't override)
        try {
            nodeRunner.runK6Test(combinedFile, virtualUsers, duration, appIp,
                    appPort, true);
            getLog().info("");
            getLog().info(CONTENT_BREAK);
            getLog().info("Combined scenario test completed successfully");
            getLog().info(CONTENT_BREAK);
        } catch (MojoExecutionException e) {
            if (failOnThreshold) {
                throw e;
            } else {
                getLog().warn(
                        "Combined test failed but failOnThreshold is false");
            }
        }
    }

    /**
     * Runs tests sequentially (original behavior).
     */
    private void runSequentialTests(List<Path> filesToRun,
            LoadProfile loadProfile) throws MojoExecutionException {
        getLog().info("Running " + filesToRun.size()
                + " k6 load test(s) sequentially");
        getLog().info(CONTENT_BREAK);
        getLog().info("  Virtual users: " + virtualUsers);
        getLog().info("  Duration: " + duration);
        getLog().info("  Load pattern: " + loadProfile);
        getLog().info("  Target: http://" + appIp + ":" + appPort);
        getLog().info("  Tests: ");
        for (Path test : filesToRun) {
            getLog().info("    - " + test.getFileName());
        }
        getLog().info(CONTENT_BREAK);
        getLog().info("");

        int passed = 0;
        int failed = 0;

        for (Path testPath : filesToRun) {
            getLog().info("");
            getLog().info("----------------------------------------");
            getLog().info("Running: " + testPath.getFileName());
            getLog().info("----------------------------------------");

            try {
                nodeRunner.runK6Test(testPath, virtualUsers, duration, appIp,
                        appPort, loadProfile);
                passed++;
                getLog().info("Test passed: " + testPath.getFileName());
            } catch (MojoExecutionException e) {
                failed++;
                if (failOnThreshold) {
                    getLog().error("Test failed: " + testPath.getFileName());
                    throw e;
                } else {
                    getLog().warn("Test failed but failOnThreshold is false: "
                            + testPath.getFileName());
                }
            }
        }

        getLog().info("");
        getLog().info(CONTENT_BREAK);
        getLog().info("k6 load test summary: " + passed + " passed, " + failed
                + " failed");
        getLog().info(CONTENT_BREAK);
    }

    /**
     * Builds a {@link LoadProfile} from the Maven parameters. Priority order:
     * <ol>
     * <li>{@code customScenario} — fully custom k6 scenario definition</li>
     * <li>{@code executor} — explicit k6 executor with parameters</li>
     * <li>{@code stages} — custom stages (sets pattern to CUSTOM)</li>
     * <li>{@code loadPattern} — predefined pattern (default: ramp)</li>
     * </ol>
     *
     * @return the load profile configuration
     * @throws MojoExecutionException
     *             if the configuration is invalid
     */
    private LoadProfile buildLoadProfile() throws MojoExecutionException {
        // Fully custom scenario takes highest priority
        if (customScenario != null && !customScenario.isBlank()) {
            return LoadProfile.customScenario(customScenario);
        }

        // Explicit executor selection
        if (executor != null && !executor.isBlank()) {
            try {
                K6Executor k6Executor = K6Executor.fromString(executor);
                var stageList = (stages != null && !stages.isBlank())
                        ? LoadProfile.parseStages(stages)
                        : List.<LoadProfile.Stage> of();
                return LoadProfile.executor(k6Executor).stages(stageList)
                        .rate(rate).timeUnit(timeUnit)
                        .preAllocatedVUs(preAllocatedVUs).maxVUs(maxVUs)
                        .iterations(iterations).startRate(startRate);
            } catch (IllegalArgumentException e) {
                throw new MojoExecutionException(
                        "Invalid executor configuration: " + e.getMessage(), e);
            }
        }

        // Custom stages override the load pattern
        if (stages != null && !stages.isBlank()) {
            try {
                return LoadProfile
                        .customStages(LoadProfile.parseStages(stages));
            } catch (IllegalArgumentException e) {
                throw new MojoExecutionException(
                        "Invalid k6.stages format: " + e.getMessage(), e);
            }
        }

        // Predefined load pattern
        LoadPattern pattern;
        try {
            pattern = LoadPattern.valueOf(loadPattern.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new MojoExecutionException("Invalid k6.loadPattern: '"
                    + loadPattern
                    + "'. Valid values: constant, ramp, stress, soak, custom");
        }
        return switch (pattern) {
        case CONSTANT -> LoadProfile.constant();
        case RAMP -> LoadProfile.ramp(rampUp, rampDown);
        case STRESS -> LoadProfile.stress();
        case SOAK -> LoadProfile.soak();
        case CUSTOM -> LoadProfile.customStages(List.of());
        };
    }

    /**
     * Parses scenario weights from the scenarioWeights parameter. Format:
     * "scenarioName:weight,scenarioName:weight"
     */
    private Map<String, Integer> parseScenarioWeights() {
        Map<String, Integer> weights = new HashMap<>();
        if (scenarioWeights != null && !scenarioWeights.isEmpty()) {
            for (String entry : scenarioWeights.split(",")) {
                String[] parts = entry.trim().split(":");
                if (parts.length == 2) {
                    weights.put(parts[0].trim(),
                            Integer.parseInt(parts[1].trim()));
                }
            }
        }
        return weights;
    }

    /**
     * Converts a file name to a valid JavaScript function name. E.g.,
     * "hello-world.js" -> "helloWorld"
     */
    private String fileToScenarioName(Path file) {
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
                    throw new MojoExecutionException(
                            "Test file not found: " + path);
                }
                result.add(path);
            }
        }

        // Add single testFile if specified
        if (testFile != null) {
            Path path = testFile.toPath().toAbsolutePath();
            if (!Files.exists(path)) {
                throw new MojoExecutionException(
                        "Test file not found: " + path);
            }
            if (!result.contains(path)) {
                result.add(path);
            }
        }

        // Add all .js files from testDir (excluding helpers, generated, and
        // combined)
        if (testDir != null && testDir.exists() && testDir.isDirectory()) {
            try (Stream<Path> files = Files.list(testDir.toPath())) {
                files.filter(p -> p.toString().endsWith(".js")).filter(
                        p -> !p.getFileName().toString().contains("helper"))
                        .filter(p -> !p.getFileName().toString()
                                .contains("-generated"))
                        .filter(p -> !p.getFileName().toString()
                                .equals("combined-scenarios.js"))
                        .sorted().forEach(p -> {
                            if (!result.contains(p.toAbsolutePath())) {
                                result.add(p.toAbsolutePath());
                            }
                        });
            } catch (IOException e) {
                throw new MojoExecutionException(
                        "Failed to list test directory: " + testDir, e);
            }
        }

        return result;
    }
}
