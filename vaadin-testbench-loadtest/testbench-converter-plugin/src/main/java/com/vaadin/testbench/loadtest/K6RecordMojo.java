/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.vaadin.testbench.loadtest.util.K6TestRefactorer.ThinkTimeConfig;
import com.vaadin.testbench.loadtest.util.SourceHasher;

/**
 * Records TestBench tests through a proxy and converts them to k6 load tests.
 * <p>
 * This goal supports recording multiple test classes. For each test class it:
 * <ol>
 * <li>Starts a recording proxy on the specified port</li>
 * <li>Runs the TestBench test class with proxy configuration</li>
 * <li>Stops the proxy and saves the HAR file</li>
 * <li>Converts the HAR to a k6 test (filters, converts, refactors)</li>
 * </ol>
 * <p>
 * Example usage:
 * 
 * <pre>
 * mvn k6:record -Dk6.testClasses=HelloWorldIT,CrudExampleIT
 * </pre>
 */
@Mojo(name = "record", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class K6RecordMojo extends AbstractK6Mojo {

    /**
     * The TestBench test class to record. Can be a simple class name or fully
     * qualified name. For multiple classes, use testClasses instead.
     */
    @Parameter(property = "k6.testClass")
    private String testClass;

    /**
     * List of TestBench test classes to record. Each class will be recorded
     * separately and generate its own k6 test file.
     */
    @Parameter(property = "k6.testClasses")
    private List<String> testClasses;

    /**
     * Port for the recording proxy.
     */
    @Parameter(property = "k6.proxyPort", defaultValue = "6000")
    private int proxyPort;

    /**
     * Port where the application is running.
     */
    @Parameter(property = "k6.appPort", defaultValue = "8080")
    private int appPort;

    /**
     * Working directory for running the TestBench test. Defaults to the project
     * base directory.
     */
    @Parameter(property = "k6.testWorkDir", defaultValue = "${project.basedir}")
    private File testWorkDir;

    /**
     * Directory to store HAR recordings.
     */
    @Parameter(property = "k6.harDir", defaultValue = "${project.build.directory}")
    private File harDir;

    /**
     * Output directory for generated k6 tests.
     */
    @Parameter(property = "k6.outputDir", defaultValue = "${project.build.directory}/k6/tests")
    private File outputDir;

    /**
     * Timeout for TestBench test execution in seconds.
     */
    @Parameter(property = "k6.testTimeout", defaultValue = "300")
    private int testTimeout;

    /**
     * Additional Maven arguments for running the TestBench test.
     */
    @Parameter(property = "k6.mavenArgs")
    private String mavenArgs;

    /**
     * Force re-recording even if sources haven't changed. By default, recording
     * is skipped if the test source files and pom.xml haven't changed since the
     * last recording.
     */
    @Parameter(property = "k6.forceRecord", defaultValue = "false")
    private boolean forceRecord;

    /**
     * Enable realistic think time delays between user actions. When enabled,
     * the generated k6 scripts will include sleep() calls to simulate real user
     * behavior (reading pages, thinking before actions). Set to false for
     * maximum throughput testing.
     */
    @Parameter(property = "k6.thinkTime.enabled", defaultValue = "true")
    private boolean thinkTimeEnabled;

    /**
     * Base delay in seconds after page load (v-r=init response). Simulates time
     * for a user to read and understand the page. Actual delay will be:
     * baseDelay + random(0, baseDelay * 1.5) Set to 0 to disable page read
     * delays while keeping interaction delays.
     */
    @Parameter(property = "k6.thinkTime.pageReadDelay", defaultValue = "2.0")
    private double pageReadDelay;

    /**
     * Base delay in seconds after user interaction (v-r=uidl response).
     * Simulates thinking time between user actions. Actual delay will be:
     * baseDelay + random(0, baseDelay * 3) Set to 0 to disable interaction
     * delays while keeping page read delays.
     */
    @Parameter(property = "k6.thinkTime.interactionDelay", defaultValue = "0.5")
    private double interactionDelay;

    private SourceHasher sourceHasher;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping k6:record");
            return;
        }

        // Build list of test classes to record
        List<String> classesToRecord = getTestClassesToRecord();
        if (classesToRecord.isEmpty()) {
            throw new MojoExecutionException(
                    "No test classes specified. Use testClass or testClasses parameter.");
        }

        getLog().info(
                "Recording " + classesToRecord.size() + " TestBench test(s)");
        getLog().info("  Proxy port: " + proxyPort);
        getLog().info("  App port: " + appPort);

        // Initialize (extract utilities, validate prerequisites)
        initialize();
        sourceHasher = new SourceHasher();

        Path outputPath = outputDir.toPath().toAbsolutePath();
        ensureDirectoryExists(outputPath);

        List<Path> generatedTests = new ArrayList<>();
        List<Path> cachedTests = new ArrayList<>();

        // Record each test class
        for (String currentTestClass : classesToRecord) {
            try {
                RecordResult result = recordSingleTest(currentTestClass,
                        outputPath);
                if (result.wasCached()) {
                    cachedTests.add(result.testFile());
                } else {
                    generatedTests.add(result.testFile());
                }
            } catch (Exception e) {
                getLog().error(
                        "Failed to record test class: " + currentTestClass, e);
                throw new MojoExecutionException(
                        "Failed to record test class: " + currentTestClass, e);
            }
        }

        // Copy Vaadin helpers once
        copyVaadinHelpers(outputPath);

        getLog().info("");
        if (!generatedTests.isEmpty()) {
            getLog().info("Recorded " + generatedTests.size() + " test(s):");
            for (Path test : generatedTests) {
                getLog().info("  - " + test.getFileName());
            }
        }
        if (!cachedTests.isEmpty()) {
            getLog().info(
                    "Skipped " + cachedTests.size() + " unchanged test(s):");
            for (Path test : cachedTests) {
                getLog().info("  - " + test.getFileName() + " (cached)");
            }
        }
        getLog().info("");
        getLog().info("Run the tests with:");
        for (Path test : generatedTests) {
            getLog().info("  k6 run -e APP_PORT=" + appPort + " " + test);
        }
        for (Path test : cachedTests) {
            getLog().info("  k6 run -e APP_PORT=" + appPort + " " + test);
        }
    }

    /**
     * Result of recording a single test.
     */
    private record RecordResult(Path testFile, boolean wasCached) {
    }

    /**
     * Builds the list of test classes to record from configuration.
     */
    private List<String> getTestClassesToRecord() {
        List<String> result = new ArrayList<>();

        // Add from testClasses list
        if (testClasses != null && !testClasses.isEmpty()) {
            for (String tc : testClasses) {
                // Support comma-separated values in list items
                if (tc.contains(",")) {
                    result.addAll(Arrays.asList(tc.split("\\s*,\\s*")));
                } else {
                    result.add(tc.trim());
                }
            }
        }

        // Add single testClass if specified and not already in list
        if (testClass != null && !testClass.isEmpty()) {
            // Support comma-separated values
            for (String tc : testClass.split("\\s*,\\s*")) {
                if (!result.contains(tc.trim())) {
                    result.add(tc.trim());
                }
            }
        }

        return result;
    }

    /**
     * Records a single test class and returns the path to the generated k6
     * test. Uses hash-based caching to skip recording if sources haven't
     * changed.
     */
    private RecordResult recordSingleTest(String currentTestClass,
            Path outputPath) throws MojoExecutionException,
            InterruptedException, java.io.IOException {

        // Prepare paths
        String outputName = scenarioToFileName(currentTestClass);
        Path harPath = harDir.toPath().resolve(outputName + "-recording.har")
                .toAbsolutePath();
        Path generatedFile = outputPath.resolve(outputName + "-generated.js");
        Path refactoredFile = outputPath.resolve(outputName + ".js");
        Path hashFile = outputPath.resolve(outputName + ".hash");

        // Check if we can use cached version
        if (!forceRecord && Files.exists(refactoredFile)) {
            String currentHash = sourceHasher.calculateSourceHash(
                    testWorkDir.toPath(), currentTestClass);
            String storedHash = sourceHasher.readStoredHash(hashFile);

            if (currentHash != null && currentHash.equals(storedHash)) {
                getLog().info("");
                getLog().info("========================================");
                getLog().info("Skipping: " + currentTestClass + " (unchanged)");
                getLog().info("========================================");
                return new RecordResult(refactoredFile, true);
            }
        }

        getLog().info("");
        getLog().info("========================================");
        getLog().info("Recording: " + currentTestClass);
        getLog().info("========================================");

        ensureDirectoryExists(harPath.getParent());

        // Clean up old files
        try {
            Files.deleteIfExists(harPath);
            Files.deleteIfExists(generatedFile);
        } catch (Exception e) {
            getLog().warn("Could not clean up old files: " + e.getMessage());
        }

        try {
            // Step 1: Start proxy recorder
            nodeRunner.startProxyRecorder(proxyPort, harPath);

            // Step 2: Run TestBench test
            boolean testSuccess = runTestBenchTest(currentTestClass);

            // Step 3: Verify proxy captured traffic before stopping
            int recordedEntries = nodeRunner.getRecordedEntryCount();
            if (recordedEntries == 0) {
                nodeRunner.stopProxyRecorder();
                throw new MojoExecutionException(
                        "Proxy recorded 0 requests for test '"
                                + currentTestClass + "'. "
                                + "The test likely did not route traffic through the proxy on port "
                                + proxyPort + ". "
                                + "Verify that the K6RecordingExtension is on the classpath and "
                                + "junit.jupiter.extensions.autodetection.enabled=true is set.");
            }
            getLog().info("Proxy captured " + recordedEntries + " requests");

            // Step 4: Stop proxy (this writes the HAR file)
            nodeRunner.stopProxyRecorder();

            // Wait a moment for HAR file to be written
            Thread.sleep(1000);

            // Check HAR file
            if (!Files.exists(harPath)) {
                throw new MojoExecutionException(
                        "HAR file was not created: " + harPath);
            }

            getLog().info("HAR file created: " + harPath + " ("
                    + Files.size(harPath) + " bytes)");

            if (!testSuccess) {
                getLog().warn(
                        "TestBench test may have failed, but HAR was recorded. Continuing with conversion...");
            }

            // Step 4: Filter external domains
            nodeRunner.filterHar(harPath);

            // Step 5: Convert HAR to k6 (with configurable thresholds)
            nodeRunner.harToK6(harPath, generatedFile, buildThresholdConfig(),
                    buildResponseCheckConfig());

            // Step 6: Refactor for Vaadin (with think time configuration)
            ThinkTimeConfig thinkTimeConfig = new ThinkTimeConfig(
                    thinkTimeEnabled, pageReadDelay, interactionDelay);
            nodeRunner.refactorK6Test(generatedFile, refactoredFile,
                    thinkTimeConfig);

            // Step 7: Store hash for future cache checks
            String currentHash = sourceHasher.calculateSourceHash(
                    testWorkDir.toPath(), currentTestClass);
            if (currentHash != null) {
                sourceHasher.storeHash(hashFile, currentHash);
            }

            return new RecordResult(refactoredFile, false);

        } finally {
            // Ensure proxy is stopped
            nodeRunner.stopProxyRecorder();
        }
    }

    /**
     * Runs the TestBench test using Maven failsafe plugin.
     *
     * @param currentTestClass
     *            the test class to run
     * @return true if the test completed successfully
     * @throws MojoExecutionException
     *             if test execution fails critically
     */
    private boolean runTestBenchTest(String currentTestClass)
            throws MojoExecutionException {
        getLog().info("Running TestBench test: " + currentTestClass);

        try {
            List<String> command = new ArrayList<>();
            boolean isWindows = System.getProperty("os.name", "").toLowerCase()
                    .contains("win");
            if (isWindows) {
                command.add("cmd.exe");
                command.add("/c");
            }
            command.add("mvn");
            command.add("failsafe:integration-test");
            command.add("-Dit.test=" + currentTestClass);
            command.add("-Dk6.proxy.host=localhost:" + proxyPort);
            command.add("-Dserver.port=" + appPort);
            command.add("-DfailIfNoTests=false");
            // Enable JUnit 5 auto-detection for K6RecordingExtension
            command.add(
                    "-Djunit.jupiter.extensions.autodetection.enabled=true");

            // Add any extra Maven arguments
            if (mavenArgs != null && !mavenArgs.isEmpty()) {
                for (String arg : mavenArgs.split("\\s+")) {
                    command.add(arg);
                }
            }

            getLog().debug("Test command: " + String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(testWorkDir);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Stream output
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    getLog().info("[test] " + line);
                }
            }

            boolean finished = process.waitFor(testTimeout, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                getLog().warn("TestBench test timed out after " + testTimeout
                        + " seconds");
                return false;
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                getLog().warn("TestBench test exited with code: " + exitCode);
                return false;
            }

            return true;

        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run TestBench test", e);
        }
    }
}
