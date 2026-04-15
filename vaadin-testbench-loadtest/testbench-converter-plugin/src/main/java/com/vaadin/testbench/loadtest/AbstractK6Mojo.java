/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.vaadin.pro.licensechecker.Capabilities;
import com.vaadin.pro.licensechecker.Capability;
import com.vaadin.pro.licensechecker.LicenseChecker;
import com.vaadin.testbench.loadtest.util.ExperimentalWarning;
import com.vaadin.testbench.loadtest.util.NodeRunner;
import com.vaadin.testbench.loadtest.util.ResourceExtractor;
import com.vaadin.testbench.loadtest.util.ResponseCheckConfig;
import com.vaadin.testbench.loadtest.util.ThresholdConfig;

/**
 * Base class for k6-related Maven goals. Provides common functionality for
 * resource extraction and utility management.
 *
 * Note: Node.js and npm are no longer required. All HAR processing and k6
 * conversion is now handled by pure Java implementations.
 */
public abstract class AbstractK6Mojo extends AbstractMojo {

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * Directory to extract k6 utilities to. Defaults to target/k6-utils.
     */
    @Parameter(property = "k6.utilsDir", defaultValue = "${project.build.directory}/k6-utils")
    protected String utilsDir;

    /**
     * Skip execution of this goal.
     */
    @Parameter(property = "k6.skip", defaultValue = "false")
    protected boolean skip;

    /**
     * 95th percentile HTTP request duration threshold in milliseconds. The k6
     * test will fail if p(95) response time exceeds this value. Set to 0 to
     * disable this threshold.
     */
    @Parameter(property = "k6.threshold.httpReqDurationP95", defaultValue = "2000")
    protected int httpReqDurationP95;

    /**
     * 99th percentile HTTP request duration threshold in milliseconds. The k6
     * test will fail if p(99) response time exceeds this value. Set to 0 to
     * disable this threshold.
     */
    @Parameter(property = "k6.threshold.httpReqDurationP99", defaultValue = "5000")
    protected int httpReqDurationP99;

    /**
     * Whether to abort the k6 test immediately when a check fails. When true, a
     * single failed check causes the test to stop. When false, failures are
     * still recorded but the test continues.
     */
    @Parameter(property = "k6.threshold.checksAbortOnFail", defaultValue = "true")
    protected boolean checksAbortOnFail;

    /**
     * Custom thresholds for any k6 metric, in addition to the default
     * http_req_duration and checks thresholds. Format:
     * {@code metric:expression,metric:expression,...}
     * <p>
     * Examples:
     * <ul>
     * <li>{@code http_req_failed:rate<0.01} - less than 1% failed requests</li>
     * <li>{@code http_req_waiting:p(95)<500} - 95th percentile waiting time
     * under 500ms</li>
     * <li>{@code http_reqs:count>100} - at least 100 requests completed</li>
     * <li>{@code http_req_duration:p(50)<1000} - adds a median threshold
     * alongside the default p95/p99</li>
     * </ul>
     * Multiple expressions can be separated by commas. Multiple expressions for
     * the same metric are supported.
     */
    @Parameter(property = "k6.threshold.custom")
    protected String customThresholds;

    /**
     * Custom response validation checks to inject into the generated k6
     * scripts, in addition to the built-in Vaadin checks. Format:
     * {@code scope|name|expression;scope|name|expression;...}
     * <p>
     * The scope is optional and defaults to {@code ALL}. Valid scopes:
     * {@code INIT} (init requests only), {@code UIDL} (UIDL requests only),
     * {@code ALL} (both).
     * <p>
     * Examples:
     * <ul>
     * <li>{@code "has title|(r) => r.body.includes('myElement')"} — checks all
     * responses</li>
     * <li>{@code "INIT|has title|(r) => r.body.includes('myElement')"} — init
     * only</li>
     * <li>{@code "UIDL|no warning|(r) => !r.body.includes('warning');ALL|fast|(r) => r.timings.duration < 3000"}</li>
     * </ul>
     */
    @Parameter(property = "k6.checks.custom")
    protected String customChecks;

    protected ResourceExtractor resourceExtractor;
    protected NodeRunner nodeRunner;
    protected Path extractionPath;

    /**
     * Initializes the plugin by extracting utilities. Node.js and npm are no
     * longer required as all processing is done in Java.
     *
     * @throws MojoExecutionException
     *             if initialization fails
     */
    protected void initialize() throws MojoExecutionException {
        checkLicense();

        ExperimentalWarning.log();

        extractionPath = Path.of(utilsDir);

        // Extract bundled utilities (only vaadin-k6-helpers.js is needed for k6
        // runtime)
        resourceExtractor = new ResourceExtractor(extractionPath);
        try {
            resourceExtractor.extractUtilities();
            getLog().debug("Extracted k6 utilities to: " + extractionPath);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to extract k6 utilities",
                    e);
        }

        // Initialize runner (now uses Java implementations internally)
        nodeRunner = new NodeRunner(extractionPath);
    }

    // Visible for testing
    void checkLicense() throws MojoExecutionException {
        Properties properties = new Properties();
        try {
            properties.load(AbstractK6Mojo.class
                    .getResourceAsStream("testbench.properties"));
        } catch (Exception e) {
            throw new MojoExecutionException(
                    "Unable to read TestBench properties file", e);
        }
        LicenseChecker.checkLicenseFromStaticBlock("vaadin-testbench",
                properties.getProperty("testbench.version"), null,
                Capabilities.of(Capability.PRE_TRIAL));
    }

    /**
     * Ensures the output directory exists.
     *
     * @param outputDir
     *            the output directory path
     * @throws MojoExecutionException
     *             if the directory cannot be created
     */
    protected void ensureDirectoryExists(Path outputDir)
            throws MojoExecutionException {
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to create output directory: " + outputDir, e);
        }
    }

    /**
     * Copies the Vaadin k6 helpers to the utils directory next to the output.
     * This ensures the generated k6 tests can import the helpers.
     *
     * @param outputDir
     *            the output directory for k6 tests
     * @throws MojoExecutionException
     *             if the copy fails
     */
    protected void copyVaadinHelpers(Path outputDir)
            throws MojoExecutionException {
        try {
            Path utilsOutputDir = outputDir.resolve("../utils");
            Files.createDirectories(utilsOutputDir);

            Path source = resourceExtractor.getVaadinHelpersScript();
            Path target = utilsOutputDir.resolve("vaadin-k6-helpers.js");

            if (!Files.exists(target) || Files.getLastModifiedTime(source)
                    .compareTo(Files.getLastModifiedTime(target)) > 0) {
                Files.copy(source, target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                getLog().info(
                        "Copied vaadin-k6-helpers.js to " + utilsOutputDir);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy Vaadin helpers",
                    e);
        }
    }

    /**
     * Builds a {@link ThresholdConfig} from the Maven parameters.
     *
     * @return the threshold configuration
     */
    protected ThresholdConfig buildThresholdConfig() {
        ThresholdConfig config = new ThresholdConfig(httpReqDurationP95,
                httpReqDurationP99, checksAbortOnFail);
        if (customThresholds != null && !customThresholds.isBlank()) {
            config = config.withCustomThresholds(customThresholds);
        }
        return config;
    }

    /**
     * Builds a {@link ResponseCheckConfig} from the Maven parameters.
     *
     * @return the response check configuration
     */
    protected ResponseCheckConfig buildResponseCheckConfig() {
        ResponseCheckConfig config = ResponseCheckConfig.EMPTY;
        if (customChecks != null && !customChecks.isBlank()) {
            config = config.withChecks(customChecks);
        }
        return config;
    }

    /**
     * Converts a scenario class name to a kebab-case output file name. E.g.,
     * "HelloWorldScenario" -> "hello-world"
     *
     * @param scenarioClass
     *            the scenario class name
     * @return the kebab-case name
     */
    protected String scenarioToFileName(String scenarioClass) {
        // Remove common suffixes
        String name = scenarioClass.replaceAll("ScenarioIT$", "")
                .replaceAll("Scenario$", "").replaceAll("IT$", "")
                .replaceAll("Test$", "");

        // Convert CamelCase to kebab-case
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('-');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
